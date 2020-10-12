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
public class TcpMessageHeader implements TcpPayload {

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

    @Override
    public void fromBytes(byte[] bytes, int offset) {

    }

    public TcpMessageHeader (byte[] payload)
    {
        boolean result = checkCrc16(payload);
        if (result == false)
        {
            throw new Crc16ErrorException();
        }
        short messageLength = (short) BytesUtils.beToInt(payload, 2, 2);
        short messageTypeId = (short)BytesUtils.beToInt(payload, 4, 2);
        int seqId = BytesUtils.beToInt(payload, 6, 4);
        short protocolId = (short)BytesUtils.beToInt(payload, 10, 2);
        short secureId = (short)BytesUtils.beToInt(payload, 12, 2);
        String deviceId = new String(payload, 14, 15);
        this.messageLength = messageLength;
        this.messageTypeId = messageTypeId;
        this.seqId = seqId;
        this.protocolId = protocolId;
        this.secureId = secureId;
        this.deviceId = deviceId;
    }
}
