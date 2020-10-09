package org.company.protocol.rfid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.company.protocol.rfid.message.Login;
import org.company.protocol.rfid.message.LoginResponse;
import org.company.protocol.rfid.message.Register;
import org.company.protocol.rfid.message.RegisterResponse;
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
    LOGIN_RESPONSE((short)0x8001, "登录回复", LoginResponse::new);

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
