package org.company.protocol.rfid.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.company.protocol.rfid.TcpMessageHeader;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse extends TcpMessageHeader {

    // 登录结果
    private byte loginResult;

    // 实时时间, 6个字节
    private byte[] timeStamp;

    @Override
    public byte[] toBytes() {
        byte[] bytesBody = new byte[7];
        super.setMessageLength((short)(bytesBody.length + 28));
        super.setMessageTypeId((short)0x8001);
        super.setProtocolId((short)0x0200);
        super.setSecureId((short)0x0000);
        byte[] bytesHead = super.toBytes();

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

    public static LoginResponse of(String deviceId, int seqId)
    {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setDeviceId(deviceId);
        loginResponse.setSeqId(seqId);
        return loginResponse;
    }

    public LoginResponse(byte[] bytes)
    {

    }
}
