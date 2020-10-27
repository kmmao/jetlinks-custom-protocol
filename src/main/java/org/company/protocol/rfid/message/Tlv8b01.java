package org.company.protocol.rfid.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.company.protocol.rfid.exception.LabelCheckSumErrorException;
import org.jetlinks.core.message.ChildDeviceMessage;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.DeviceRegisterMessage;
import org.jetlinks.core.message.property.ReportPropertyMessage;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Tlv8b01 extends TlvHeader {
    private byte antennaChannel;
    private byte labelType;
    private String labelId;
    private byte idCheckSum;
    // 携带信息,目前暂未使用
    private short dummy;
    private byte labelStatus;
    private byte rssi;
    private byte[] timeStamp;

    public Tlv8b01(byte[] bytes)
    {
        super(bytes);
        byte[] idCheckSumByte = new byte[5];
        antennaChannel = bytes[4];
        labelType = bytes[5];
        byte[] labelBuf = new byte[4];
        System.arraycopy(bytes, 6, labelBuf, 0, 4);
        labelId = Hex.encodeHexString(labelBuf);
        idCheckSum = bytes[10];
        System.arraycopy(bytes, 5, idCheckSumByte, 0, 5);
        byte checkSumResult = sendRcvByteNum(idCheckSumByte);
        if (checkSumResult != idCheckSum)
        {
            throw new LabelCheckSumErrorException();
        }
        labelStatus = bytes[13];
        rssi = bytes[14];
        timeStamp = new byte[6];
        System.arraycopy(bytes, 15, timeStamp, 0, 6);
    }

    public DeviceMessage toRegisterInfo() {
        DeviceRegisterMessage deviceRegisterMessage = new DeviceRegisterMessage();
        ChildDeviceMessage child = new ChildDeviceMessage();
        // 设置子设备id
        child.setChildDeviceId(this.getLabelId());
        // 设置子设备的父设备id
        child.setDeviceId(this.getDeviceId());
        deviceRegisterMessage.setDeviceId(this.getLabelId());
        deviceRegisterMessage.addHeader("productId", "002-8b01");
        deviceRegisterMessage.addHeader("deviceName", "rfid定位标签" + this.getLabelId());
        child.setChildDeviceMessage(deviceRegisterMessage);
        return child;
    }

    public DeviceMessage toPropertyInfo() {
        ReportPropertyMessage reportPropertyMessage = new ReportPropertyMessage();
        ChildDeviceMessage child = new ChildDeviceMessage();
        // 设置子设备id
        child.setChildDeviceId(this.getLabelId());
        // 设置子设备的父设备id
        child.setDeviceId(this.getDeviceId());
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("labelType", labelType);
        properties.put("labelId", labelId);
        properties.put("rssi", rssi);
        properties.put("timeStamp", toTimeString(timeStamp));
        // 标签是否在基站范围内。1：在范围内 0：不在范围内
        int isInboundary = (antennaChannel & 0x80) >> 7;
        // 基站停留标识。1：在基站停留 0：不在基站停留
        int attachStation = (antennaChannel & 0x40) >> 6;
        int antennaDireciton = (antennaChannel & 0x0f);
        int isRemoved = (labelStatus & 0x10) >> 4;
        int lowPower = (labelStatus & 0x01);
        properties.put("isInboundary", isInboundary);
        properties.put("attachStation", attachStation);
        properties.put("isRemoved", isRemoved);
        properties.put("lowPower", lowPower);
        properties.put("antennaDireciton", antennaDireciton);
        reportPropertyMessage.setProperties(properties);
        reportPropertyMessage.setDeviceId(this.getLabelId());
        child.setChildDeviceMessage(reportPropertyMessage);
        return child;
    }
}
