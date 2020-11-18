package org.company.protocol.rfid.message;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.company.protocol.rfid.TcpMessageHeader;
import org.jetlinks.core.utils.BytesUtils;

@Slf4j(topic = "system.rfid")
@NoArgsConstructor
public class ConfigResponse extends TcpMessageHeader {

    public ConfigResponse(byte[] bytes)
    {}

    public static ConfigResponse of(String deviceId, int seqId)
    {
        ConfigResponse configResponse = new ConfigResponse();
        configResponse.setDeviceId(deviceId);
        configResponse.setSeqId(seqId);
        return configResponse;
    }

    @Override
    public byte[] toBytes()
    {
        byte[] parm_type = new byte[1];
        BytesUtils.numberToBe(parm_type, 0x80, 0 ,1);

        super.setMessageLength((short)(1 + 28));
        super.setMessageTypeId((short)0x800a);
        super.setProtocolId((short)(0x0001));
        super.setSecureId((short)0x0000);
        byte[] bytesHead = super.toBytes();

        // 报文头+报文体的crc16结果
        byte[] byteCrc16 = doGetCrc(bytesHead, parm_type);

        byte[] byteStartTag = getStartTag();

        byte[] result = getFinalMessage(byteStartTag, bytesHead, parm_type, byteCrc16);

        return result;
    }
}
