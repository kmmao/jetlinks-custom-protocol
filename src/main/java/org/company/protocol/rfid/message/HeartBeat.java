package org.company.protocol.rfid.message;

import org.company.protocol.rfid.TcpMessageHeader;
import org.company.protocol.rfid.TcpPayload;

public class HeartBeat implements TcpPayload {

    private TcpMessageHeader header;

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }

    @Override
    public void fromBytes(byte[] bytes, int offset) {

    }
}
