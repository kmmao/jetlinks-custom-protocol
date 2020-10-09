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
    private TcpMessageHeader data;
    public static TcpMessage of(byte[] payload) {
        MessageType type = MessageType.of(payload).orElseThrow(IllegalArgumentException::new);
        TcpMessageHeader data = type.getPayLoadSupplier().apply(payload);
        return TcpMessage.of(type, data);
    }

    public ByteBuf toByteBuf(){
        return Unpooled.wrappedBuffer(data.toBytes());
    }

}
