package org.company.protocol.rfid;

public interface TcpPayload extends TcpPayloadUtils {
    byte[] toBytes();

    void fromBytes(byte[] bytes, int offset);
}
