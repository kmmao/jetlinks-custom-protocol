package org.company.protocol.rfid;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.company.protocol.rfid.exception.Crc16ErrorException;
import org.company.protocol.rfid.exception.LabelCheckSumErrorException;
import org.company.protocol.rfid.message.*;
import org.jetlinks.core.Value;
import org.jetlinks.core.device.DeviceConfigKey;
import org.jetlinks.core.device.DeviceRegistry;
import org.jetlinks.core.message.ChildDeviceMessage;
import org.jetlinks.core.message.DeviceOnlineMessage;
import org.jetlinks.core.message.DeviceRegisterMessage;
import org.jetlinks.core.message.Message;
import org.jetlinks.core.message.codec.*;
import org.jetlinks.core.message.property.ReportPropertyMessage;
import org.jetlinks.core.server.session.DeviceSession;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j(topic = "system.rfid")
@AllArgsConstructor
public class RfidDeviceMessageCodec implements DeviceMessageCodec {

    private DeviceRegistry registry;

    @Override
    public Transport getSupportTransport() {
        return DefaultTransport.TCP;
    }

    @NotNull
    @Override
    public Publisher<? extends Message> decode(@NotNull MessageDecodeContext messageDecodeContext) {
        return Flux.defer(() -> {
            FromDeviceMessageContext ctx = ((FromDeviceMessageContext) messageDecodeContext);
            ByteBuf byteBuf = ctx.getMessage().getPayload();
            byte[] payload = ByteBufUtil.getBytes(byteBuf, 0, byteBuf.readableBytes(), false);
            DeviceSession session = ctx.getSession();
            TcpMessage message;
            try {
                message = TcpMessage.of(payload);
            }
            catch (Crc16ErrorException e)
            {
                log.error("crc16 incorrect");
                return Mono.error(e);
            }
            catch (LabelCheckSumErrorException e)
            {
                log.error("id check sum error");
                return Mono.error(e);
            }
            catch (Exception e)
            {
                return Mono.error(e);
            }

            TcpMessageHeader head = message.getData();
            String deviceId = head.getDeviceId();
            int seqId = head.getSeqId();

            // 请求注册
            if (message.getType() == MessageType.REGISTER)
            {
                DeviceRegisterMessage registerMessage = new DeviceRegisterMessage();
                registerMessage.addHeader("productId", "002");
                registerMessage.addHeader("deviceName", "rfid定位测试设备" + deviceId);
                registerMessage.setDeviceId(deviceId);

                ByteBuf encodedMessage = TcpMessage.of(MessageType.REGISTER_RESPONSE, RegisterResponse.of(deviceId, seqId)).toByteBuf();
                return session
                        .send(EncodedMessage.simple(encodedMessage))
                        .thenReturn(registerMessage);
            }

            // 请求登录
            if (message.getType() == MessageType.LOGIN)
            {
                return registry
                        .getDevice(deviceId)
                        .flatMap(msg -> {
                            DeviceOnlineMessage onlineMessage = new DeviceOnlineMessage();
                            onlineMessage.setDeviceId(deviceId);
                            onlineMessage.setTimestamp(System.currentTimeMillis());
                            return session
                                    .send(EncodedMessage.simple(TcpMessage.of(MessageType.LOGIN_RESPONSE, LoginResponse.of(deviceId, seqId)).toByteBuf()))
                                    .thenReturn(onlineMessage);
                        });
            }

            if (message.getData() instanceof HeartBeat) {
                DeviceOnlineMessage onlineMessage = new DeviceOnlineMessage();
                onlineMessage.setDeviceId(deviceId);
                return session
                        .send(EncodedMessage.simple(TcpMessage.of(MessageType.HEART_RESPONSE, HeartBeatResponse.of(deviceId, seqId)).toByteBuf()))
                        .thenMany(Flux.create((t) -> {
                            t.next((ReportPropertyMessage)((TcpDeviceMessage) message.getData()).toDeviceMessage());
                            t.next(onlineMessage);
                            t.complete();
                        }));
            }

            if (message.getData() instanceof Upload) {
                List<Upload> obj = ((Upload) message.getData()).getObjList();

                return session.send(EncodedMessage.simple(TcpMessage.of(MessageType.UPLOAD_RESPONSE, UploadResponse.of(deviceId, seqId)).toByteBuf()))
                        .thenMany(
                                Flux.fromIterable(obj)
                                .flatMap(
                                    msg -> registry.getDevice(msg.getLabelId())
                                                    .filterWhen(device -> {return device.getSelfConfig(DeviceConfigKey.parentGatewayId.getKey())
                                                                                        .map(Value::asString)
                                                                                        .map(deviceId::equals);
                                                                            })
                                                    .flatMap(operator ->{return Mono.just(((ChildDeviceMessage)(msg.toPropertyInfo())));})
                                                    .switchIfEmpty(Mono.defer(() -> {return Mono.just(((ChildDeviceMessage)(msg.toRegisterInfo())));})))
                        );
            }
            log.warn("No match type. Now is " + String.valueOf(message.getType().getId()));
            return Mono.empty();
        });
    }

    @NotNull
    @Override
    public Publisher<? extends EncodedMessage> encode(@NotNull MessageEncodeContext messageEncodeContext) {
        log.info("encode");

        return Mono.empty();
    }

}
