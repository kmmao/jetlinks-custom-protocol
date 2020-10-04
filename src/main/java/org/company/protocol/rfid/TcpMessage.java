package org.company.protocol.rfid;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 报文2个字节起始标识，28个字节报文头
 */
@Setter
@Getter
@AllArgsConstructor(staticName = "of")
public class TcpMessage {
    private MessageType type;
    private TcpPayload data;

    public static TcpMessage of(byte[] payload) {
        MessageType type = MessageType.of(payload).orElseThrow(IllegalArgumentException::new);
        return TcpMessage.of(type, type.read(payload, 30));
    }

    public ByteBuf toByteBuf(){
        return Unpooled.wrappedBuffer(toBytes());
    }

    public byte[] toBytes() {

        byte[] buf = type.toBytes(data);

        return buf;
    }

}
