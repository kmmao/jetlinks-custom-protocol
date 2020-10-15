package org.company.protocol.rfid;

import org.jetlinks.core.utils.BytesUtils;

import java.util.Calendar;

public interface TcpPayloadUtils {

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
        return crc & 0xffff;
    }

    default byte[] byteMerge(byte[] b1, byte[] b2)
    {
        byte [] result = new byte[b1.length + b2.length];
        System.arraycopy(b1, 0, result, 0, b1.length);
        System.arraycopy(b2, 0, result, b1.length, b2.length);
        return result;
    }

    default byte[] doGetCrc(byte[] bytesHead, byte[] bytesBody)
    {
        byte[] byteCrc16 = new byte[2];
        BytesUtils.numberToBe(byteCrc16, getCrc(byteMerge(bytesHead, bytesBody)), 0, 2);
        return byteCrc16;
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

    default String toTimeString(byte[] bytes)
    {
        return String.format("%d-%02d-%02d %02d:%02d:%02d", bytes[0] + 2000, bytes[1], bytes[2], bytes[3], bytes[4], bytes[5]);
    }

    default byte[] getHostIp()
    {
        return new byte[32];
    }

    default byte[] getStartTag()
    {
        byte[] startTag = new byte[2];
        BytesUtils.numberToBe(startTag, 0x55aa, 0, 2);
        return startTag;
    }

    default byte[] getFinalMessage(byte[] byteStartTag, byte[] bytesHead, byte[] bytesBody, byte[] byteCrc16)
    {
        byte[] result = new byte[bytesHead.length + bytesBody.length + byteStartTag.length + byteCrc16.length];
        System.arraycopy(byteStartTag, 0, result, 0 ,byteStartTag.length);
        System.arraycopy(bytesHead, 0, result, byteStartTag.length, bytesHead.length);
        System.arraycopy(bytesBody, 0, result, byteStartTag.length + bytesHead.length, bytesBody.length);
        System.arraycopy(byteCrc16, 0, result, byteStartTag.length + bytesHead.length + bytesBody.length, byteCrc16.length);
        return result;
    }

    default boolean checkCrc16(byte[] payload)
    {
        int payloadBodyLength = BytesUtils.beToInt(payload, 2, 2);
        int crcInpayload = BytesUtils.beToInt(payload, 2+payloadBodyLength, 2);
        byte[] payloadTobeCrc = new byte[payloadBodyLength];
        System.arraycopy(payload, 2, payloadTobeCrc, 0, payloadBodyLength);
        int crcCalced = getCrc(payloadTobeCrc);
        if (crcInpayload == crcCalced)
            return true;
        else
            return false;
    }
}
