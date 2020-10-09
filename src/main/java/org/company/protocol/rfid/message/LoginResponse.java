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

    // 登录结果
    private byte loginResult;

    // 实时时间, 6个字节
    private byte[] timeStamp;

    private TcpMessageHeader header;

    @Override
    public byte[] toBytes() {
        byte[] bytesBody = new byte[7];
        TcpMessageHeader head = getHeader();
        head.setMessageLength((short)(bytesBody.length + 28));
        head.setMessageTypeId((short)0x8001);
        head.setSeqId(1);
        head.setProtocolId((short)0x0001);
        head.setSecureId((short)0x8000);
        byte[] bytesHead = head.toBytes();

        // 登录结果
        bytesBody[0] = 0;

        // 时间戳
        System.arraycopy(getHostTimeStamp(), 0, bytesBody, 1, 6);

        // 报文头+报文体的crc16结果
        byte[] byteCrc16 = doGetCrc(bytesHead, bytesBody);

        byte[] byteStartTag = getStartTag();

        byte[] result = getFinalMessage(byteStartTag, bytesHead, bytesBody, byteCrc16);

        return result;
    }

    @Override
    public void fromBytes(byte[] bytes, int offset) {

    }

    public static LoginResponse of(String deviceId)
    {
        LoginResponse loginResponse = new LoginResponse();
        TcpMessageHeader head = new TcpMessageHeader();
        head.setDeviceId(deviceId);
        loginResponse.setHeader(head);
        return loginResponse;
    }
}
