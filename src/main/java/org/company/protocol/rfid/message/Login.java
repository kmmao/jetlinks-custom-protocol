package org.company.protocol.rfid.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.company.protocol.rfid.TcpMessageHeader;
import org.company.protocol.rfid.TcpPayload;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Login implements TcpPayload {
    private short ver;

    private short paraCrc16;

    private TcpMessageHeader header;

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }

    @Override
    public void fromBytes(byte[] bytes, int offset) {
        setHeader(createHeader(bytes));
//        SetVer(BytesUtils.beToInt(bytes, offset,2));
//        SetCrc16(BytesUtils.beToInt(bytes, offset+2,2));
    }

}
