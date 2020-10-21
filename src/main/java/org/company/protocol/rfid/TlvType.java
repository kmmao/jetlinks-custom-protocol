package org.company.protocol.rfid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.company.protocol.rfid.message.Tlv8b01;
import org.company.protocol.rfid.message.TlvHeader;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.function.Function;

@AllArgsConstructor
@Getter
public enum TlvType {
    ELECTRONICS((short)0x8b01, 21,"电子标签", Tlv8b01::new);

    private short id;

    private int length;

    private String text;

    private Function<byte[], ? extends TlvHeader> payLoadSupplier;

    @NotNull
    public static Optional<TlvType> of(short type)
    {
        TlvType[] values = values();
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
