package org.company.protocol.rfid;

import org.jetlinks.core.utils.BytesUtils;

import java.util.Calendar;

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
        return TcpMessageHeader.of(messageLength, messageTypeId, seqId, protocolId, secureId, deviceId);
    }

    default int getCrc(byte[] data)
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
//        return Integer.toHexString(crc & 0xffff).toUpperCase();
        return crc;
    }

    default byte[] getHostTimeStamp()
    {
        byte[] timeStamp = new byte[6];
        Calendar calendar = Calendar.getInstance();
        timeStamp[0] = (byte)(calendar.get(Calendar.YEAR) - 2000);
        timeStamp[1] = (byte)(calendar.get(Calendar.MONTH) + 1);
        timeStamp[2] = (byte)calendar.get(Calendar.DATE);
        timeStamp[3] = (byte)calendar.get(Calendar.HOUR_OF_DAY);
        timeStamp[4] = (byte)calendar.get(Calendar.MINUTE);
        timeStamp[5] = (byte)calendar.get(Calendar.SECOND);
        return timeStamp;
    }

    default byte[] getHostIp()
    {
        return new byte[32];
    }

    default byte[] byteMerge(byte[] b1, byte[] b2)
    {
        byte [] result = new byte[b1.length + b2.length];
        System.arraycopy(b1, 0, result, 0, b1.length);
        System.arraycopy(b2, 0, result, b1.length, b2.length);
        return result;
    }

}
