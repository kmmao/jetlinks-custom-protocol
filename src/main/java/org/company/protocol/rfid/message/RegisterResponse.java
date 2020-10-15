package org.company.protocol.rfid.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.company.protocol.rfid.TcpMessageHeader;
import org.jetlinks.core.utils.BytesUtils;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse extends TcpMessageHeader {

    // 注册结果, 1字节
    private byte registerResult;

    // 实时时间, 6个字节
    private byte[] timeStamp;

    // 负载ip, 32字节
    private byte[] ip;

    // 端口, 2字节
    private short port;

    // crc16
    private short crc16;

    public RegisterResponse(byte[] bytes) {
    }

    @Override
    public byte[] toBytes() {

        byte[] bytesBody = new byte[41];
        super.setMessageLength((short)(bytesBody.length + 28));
        super.setMessageTypeId((short)0x8008);
        super.setProtocolId((short)0x0001);
        super.setSecureId((short)0x8000);
//        super.setSeqId(1);
        super.seqIdInc();
        byte[] bytesHead = super.toBytes();

        // 注册结果
        setRegisterResult((byte) 1);

        // 当前时间戳
        setTimeStamp(getTimeStamp());

        // 服务器端口
        setPort((short)12345);

        //服务器ip
        setIp(getHostIp());

        BytesUtils.numberToBe(bytesBody, getRegisterResult(), 0, 1);
        System.arraycopy(getHostTimeStamp(), 0, bytesBody, 1, 6);
        System.arraycopy(getHostIp(), 0, bytesBody, 7, 32);
        BytesUtils.numberToBe(bytesBody, getPort(), 39, 2);

        byte[] byteCrc16 = doGetCrc(bytesHead, bytesBody);

        byte[] byteStartTag = getStartTag();

        byte[] result = getFinalMessage(byteStartTag, bytesHead, bytesBody, byteCrc16);

        return result;
    }

    public static RegisterResponse of(String deviceID)
    {
        RegisterResponse rr = new RegisterResponse();
        rr.setDeviceId(deviceID);
        return rr;
    }
}
