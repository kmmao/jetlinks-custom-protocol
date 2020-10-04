package org.company.protocol.rfid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class TcpMessageHeader {

    // 起始标识 固定 55 aa
    final private short startTag = 0x55aa;

    // 报文长度
    private short messageLength;

    // 命令码
    private short messageTypeId;

    // 报文流水号
    private int seqId;

    // 报文协议版本
    private short protocolId;

    // 报文安全标识
    private short secureId;

    // 设备ID
    private String deviceId;

    @Override
    public String toString() {
        return "TcpMessageHeader{" +
                "startTag=" + startTag +
                ", messageLength=" + messageLength +
                ", messageTypeId=" + messageTypeId +
                ", seqId=" + seqId +
                ", protocolId=" + protocolId +
                ", secureId=" + secureId +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }

}
