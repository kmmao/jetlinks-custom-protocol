package org.company.protocol.rfid.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.company.protocol.rfid.TcpMessageHeader;
import org.company.protocol.rfid.TcpPayload;
import org.jetlinks.core.utils.BytesUtils;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse implements TcpPayload {

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

    private TcpMessageHeader header;

    public ByteBuf toByteBuf(){
        return Unpooled.wrappedBuffer(toBytes());
    }

    @Override
    public byte[] toBytes() {
        byte[] bytesBody = new byte[41];
        TcpMessageHeader head = getHeader();
        head.setMessageLength((short)(bytesBody.length + 28));
        head.setMessageTypeId((short)0x8008);
        head.setProtocolId((short)0x0001);
        head.setSecureId((short)0x8000);
        head.setSeqId(1);
        byte[] bytesHead = head.toBytes();

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

    @Override
    public void fromBytes(byte[] bytes, int offset) {

    }

    public static RegisterResponse of(String deviceID)
    {
        RegisterResponse rr = new RegisterResponse();
        TcpMessageHeader head = new TcpMessageHeader();
        head.setDeviceId(deviceID);
        rr.setHeader(head);
        return rr;
    }


}
