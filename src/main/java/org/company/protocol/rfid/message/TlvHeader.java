package org.company.protocol.rfid.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetlinks.core.utils.BytesUtils;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TlvHeader extends Upload{
    private short type;
    private short length;

    public TlvHeader(byte[] bytes)
    {
        type = (short) BytesUtils.beToInt(bytes, 0, 2);
        length = (short)BytesUtils.beToInt(bytes, 2, 2);
    }
}
