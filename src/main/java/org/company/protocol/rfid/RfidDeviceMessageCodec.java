package org.company.protocol.rfid;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.company.protocol.rfid.message.Login;
import org.company.protocol.rfid.message.Register;
import org.jetlinks.core.device.DeviceRegistry;
import org.jetlinks.core.message.DeviceOnlineMessage;
import org.jetlinks.core.message.DeviceRegisterMessage;
import org.jetlinks.core.message.Message;
import org.jetlinks.core.message.codec.*;
import org.jetlinks.core.server.session.DeviceSession;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

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
        return Mono.defer(() -> {
            FromDeviceMessageContext ctx = ((FromDeviceMessageContext) messageDecodeContext);
            ByteBuf byteBuf = ctx.getMessage().getPayload();
            byte[] payload = ByteBufUtil.getBytes(byteBuf, 0, byteBuf.readableBytes(), false);
            log.info("Hello: " + Hex.encodeHexString(payload));
            DeviceSession session = ctx.getSession();
            TcpMessage message;
            try {
                message = TcpMessage.of(payload);
            }
            catch (Exception e)
            {
                return Mono.error(e);
            }

            // 请求注册
            if (message.getType() == MessageType.REGISTER)
            {
                Register request = ((Register)message.getData());
                String deviceId = request.getHeader().getDeviceId();
                DeviceRegisterMessage registerMessage = new DeviceRegisterMessage();
                registerMessage.addHeader("productId", "001");
                registerMessage.addHeader("deviceName", "rfid定位测试设备1号");
                registerMessage.setDeviceId(deviceId);
                registerMessage.setTimestamp(System.currentTimeMillis());
                return Mono.justOrEmpty(registerMessage);

            }

            // 请求登录
            if (message.getType() == MessageType.LOGIN)
            {
                Login request = ((Login) message.getData());
                String deviceId = request.getHeader().getDeviceId();
                return registry
                        .getDevice(deviceId)
                        .flatMap(msg -> {
                            DeviceOnlineMessage onlineMessage = new DeviceOnlineMessage();
                            onlineMessage.setDeviceId(deviceId);
                            onlineMessage.setTimestamp(System.currentTimeMillis());
                            return Mono.justOrEmpty(onlineMessage);
                        });
            }

            if (message.getData() instanceof TcpDeviceMessage) {
                return Mono.justOrEmpty(((TcpDeviceMessage) message.getData()).toDeviceMessage());
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
