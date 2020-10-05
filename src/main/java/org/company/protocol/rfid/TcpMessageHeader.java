package org.company.protocol.rfid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetlinks.core.utils.BytesUtils;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class TcpMessageHeader {

    // 起始标识 固定 55 aa
//    final private short startTag = 0x55aa;

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

    public byte[] toBytes()
    {
        byte[] bytes = new byte[28];
        BytesUtils.numberToBe(bytes, messageLength, 0 ,2);
        BytesUtils.numberToBe(bytes, messageTypeId, 2, 2);
        BytesUtils.numberToBe(bytes, seqId, 4, 4);
        BytesUtils.numberToBe(bytes, protocolId, 8, 2);
        BytesUtils.numberToBe(bytes, secureId, 10, 2);
        byte[] deviceBytes = deviceId.getBytes();
        System.arraycopy(deviceBytes, 0, bytes, 12, deviceBytes.length);
        return bytes;
    }

}
