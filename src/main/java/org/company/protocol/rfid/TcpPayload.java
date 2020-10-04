package org.company.protocol.rfid;

import org.jetlinks.core.utils.BytesUtils;

public interface TcpPayload {
    byte[] toBytes();

    void fromBytes(byte[] bytes, int offset);

    default TcpMessageHeader createHeader(byte[] payload)
    {
        short messageLength = (short) BytesUtils.beToInt(payload, 2, 2);
        short messageTypeId = (short)BytesUtils.beToInt(payload, 4, 2);
        int seqId = BytesUtils.beToInt(payload, 6, 4);
        short protocolId = (short)BytesUtils.beToInt(payload, 10, 2);
        short secureId = (short)BytesUtils.beToInt(payload, 12, 2);
        String deviceId = new String(payload, 14, 15);
//        String deviceId = getDeviceId(payload, 14, 15);
        return TcpMessageHeader.of(messageLength, messageTypeId, seqId, protocolId, secureId, deviceId);
    }

    default String getCrc(byte[] data)
    {
        int crc = 0xffff;
        for (int i = 0; i < data.length; i++)
        {
            crc = (data[i] << 8) ^ crc;
            for (int j = 0; j < 8; j++)
            {
                if ((crc & 0x8000) != 0)
                {
                    crc = (crc << 1) ^ 0x1021;
                }
                else
                    crc <<= 1;
            }
        }
        return Integer.toHexString(crc & 0xffff).toUpperCase();
    }

}
