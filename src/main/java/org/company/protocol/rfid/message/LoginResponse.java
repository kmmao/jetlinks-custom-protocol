package org.company.protocol.rfid.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.company.protocol.rfid.TcpMessageHeader;
import org.company.protocol.rfid.TcpPayload;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse implements TcpPayload {

    private TcpMessageHeader header;

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }

    @Override
    public void fromBytes(byte[] bytes, int offset) {

    }

    public LoginResponse of(String deviceId)
    {
        LoginResponse loginResponse = new LoginResponse();
        TcpMessageHeader head = new TcpMessageHeader();
        head.setDeviceId(deviceId);
        loginResponse.setHeader(head);
        return loginResponse;
    }
}
