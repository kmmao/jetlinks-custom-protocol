package org.company.protocol.rfid.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.company.protocol.rfid.TcpMessageHeader;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HeartBeatResponse extends TcpMessageHeader {

    /**
     * 回复设备的心跳报文
     * 操作指示
     *
     */
    private byte opIndicator;

    /**
     * 平台的实时时间, 6个字节
     */
    private byte[] timeStamp;

    public static HeartBeatResponse of(String deviceId)
    {
        HeartBeatResponse heartBeatResponse = new HeartBeatResponse();
        heartBeatResponse.setDeviceId(deviceId);
        return heartBeatResponse;
    }

    /**
     * 报文回复时候用到
     * @return 回复给设备的报文
     */
    @Override
    public byte[] toBytes() {
        byte[] bytesBody = new byte[7];
        // 操作指示
        bytesBody[0] = 0x00;
        // 平台时间
        System.arraycopy(getHostTimeStamp(), 0, bytesBody, 1, 6);

        super.setMessageLength((short)(bytesBody.length + 28));
        super.setMessageTypeId((short)0x8003);
        super.setSeqId(1);
        super.setProtocolId((short)(0x0001));
        super.setSecureId((short)0x8000);
        byte[] bytesHead = super.toBytes();

        // 报文头+报文体的crc16结果
        byte[] byteCrc16 = doGetCrc(bytesHead, bytesBody);

        byte[] byteStartTag = getStartTag();

        byte[] result = getFinalMessage(byteStartTag, bytesHead, bytesBody, byteCrc16);

        return result;

    }

    public HeartBeatResponse(byte[] bytes)
    {

    }
}
