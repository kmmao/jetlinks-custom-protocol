package org.company.protocol.rfid.message;

import org.company.protocol.rfid.TcpMessageHeader;
import org.company.protocol.rfid.TcpPayload;

public class RegisterResponse implements TcpPayload {

    // 注册结果
    private byte registerResult;

    // 实时时间,6个字节长度
    private byte[] timeStamp;

    // 负载ip
    private byte[] ip;

    // 端口
    private short port;

    // crc16
    private short crc16;

    private TcpMessageHeader header;

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }

    @Override
    public void fromBytes(byte[] bytes, int offset) {

    }
}
