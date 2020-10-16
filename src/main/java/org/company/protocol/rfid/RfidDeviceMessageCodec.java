package org.company.protocol.rfid;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.company.protocol.rfid.exception.Crc16ErrorException;
import org.company.protocol.rfid.exception.LabelCheckSumErrorException;
import org.company.protocol.rfid.message.*;
import org.jetlinks.core.device.DeviceRegistry;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.DeviceOnlineMessage;
import org.jetlinks.core.message.DeviceRegisterMessage;
import org.jetlinks.core.message.Message;
import org.jetlinks.core.message.codec.*;
import org.jetlinks.core.server.session.DeviceSession;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;

@Slf4j
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
                log.info("crc16 incorrect");
                return Mono.error(e);
            }
            catch (LabelCheckSumErrorException e)
            {
                log.info("id check sum error");
                return Mono.error(e);
            }
            catch (Exception e)
            {
                return Mono.error(e);
            }

            TcpMessageHeader head = message.getData();
            String deviceId = head.getDeviceId();

            // 请求注册
            if (message.getType() == MessageType.REGISTER)
            {
                DeviceRegisterMessage registerMessage = new DeviceRegisterMessage();
                registerMessage.addHeader("productId", "002");
                registerMessage.addHeader("deviceName", "rfid定位测试设备" + deviceId);
                registerMessage.setDeviceId(deviceId);
                return session
                        .send(EncodedMessage.simple(TcpMessage.of(MessageType.REGISTER_RESPONSE, RegisterResponse.of(deviceId)).toByteBuf()))
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
                                    .send(EncodedMessage.simple(TcpMessage.of(MessageType.LOGIN_RESPONSE, LoginResponse.of(deviceId)).toByteBuf()))
                                    .thenReturn(onlineMessage);
                        });
            }

            if (message.getData() instanceof HeartBeat) {
                return session
                        .send(EncodedMessage.simple(TcpMessage.of(MessageType.HEART_RESPONSE, HeartBeatResponse.of(deviceId)).toByteBuf()))
                        .thenReturn(((TcpDeviceMessage) message.getData()).toDeviceMessage());
            }

            if (message.getData() instanceof Upload) {
                List<DeviceMessage> labelInfoList = new LinkedList<>();

                return session.send(EncodedMessage.simple(TcpMessage.of(MessageType.UPLOAD_RESPONSE, UploadResponse.of(deviceId)).toByteBuf()))
                        .thenMany(Flux.create((t) -> {
                                for (Upload obj: ((Upload)message.getData()).getObjList())
                                {
                                    if (registry.getDevice(String.valueOf(obj.getLabelId())) == null) {
                                        t.next(obj.toRegisterInfo());
                                    }
                                    t.next(obj.toPropertyInfo());
                                }
                                t.complete();
                                }));
            }
            return Mono.just(((TcpDeviceMessage) message.getData()).toDeviceMessage());
        });
    }

    @NotNull
    @Override
    public Publisher<? extends EncodedMessage> encode(@NotNull MessageEncodeContext messageEncodeContext) {
        return null;
    }
}
