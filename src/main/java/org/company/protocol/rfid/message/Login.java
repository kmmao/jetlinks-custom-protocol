package org.company.protocol.rfid.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.company.protocol.rfid.TcpMessageHeader;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Login extends TcpMessageHeader {
    private short ver;

    private short paraCrc16;

    public Login(byte[] bytes)
    {
        super(bytes);
    }

}
