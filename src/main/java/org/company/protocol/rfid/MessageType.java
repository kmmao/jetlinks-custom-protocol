package org.company.protocol.rfid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.company.protocol.rfid.message.*;
import org.jetlinks.core.utils.BytesUtils;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.function.Function;

@AllArgsConstructor
@Getter
@Slf4j
public enum MessageType {
    REGISTER((short)0x0008, "注册", Register::new),
    REGISTER_RESPONSE((short)0x8008, "注册回复", RegisterResponse::new),
    LOGIN((short)0x0001, "登录", Login::new),
    LOGIN_RESPONSE((short)0x8001, "登录回复", LoginResponse::new),
    HEART((short)0x0003, "心跳", HeartBeat::new),
    HEART_RESPONSE((short)0x8003, "心跳回复", HeartBeatResponse::new),
    UPLOAD((short)0x0004, "设备数据上报", Upload::new),
    UPLOAD_RESPONSE((short)0x8004, "设备数据上报回复", UploadResponse::new);

    private short id;

    private String text;
    
    private Function<byte[], ? extends TcpMessageHeader> payLoadSupplier;

    @NotNull
    public static Optional<MessageType> of(byte[] payload)
    {
        short type = (short)BytesUtils.beToInt(payload, 4, 2);
        MessageType[] values = values();
        for (int i = 0; i < values.length; i++)
        {
            if (values[i].id == type)
            {
                return Optional.of(values()[i]);
            }
        }
        return Optional.empty();
    }
}
