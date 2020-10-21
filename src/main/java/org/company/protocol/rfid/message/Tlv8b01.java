package org.company.protocol.rfid.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.company.protocol.rfid.exception.LabelCheckSumErrorException;
import org.jetlinks.core.message.ChildDeviceMessage;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.DeviceRegisterMessage;
import org.jetlinks.core.message.property.ReportPropertyMessage;
import org.jetlinks.core.utils.BytesUtils;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Tlv8b01 extends TlvHeader {
    private byte antennaChannel;
    private byte labelType;
    private long labelId;
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
        labelId = BytesUtils.beToLong(bytes, 6, 4);
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
        child.setChildDeviceId(String.valueOf(this.getLabelId()));
        // 设置子设备的父设备id
        child.setDeviceId(this.getDeviceId());
        deviceRegisterMessage.setDeviceId(String.valueOf(this.getLabelId()));
        deviceRegisterMessage.addHeader("productId", "002-8b01");
        deviceRegisterMessage.addHeader("deviceName", "rfid定位标签" + this.getLabelId());
        child.setChildDeviceMessage(deviceRegisterMessage);
        return child;
    }

    public DeviceMessage toPropertyInfo() {
        ReportPropertyMessage reportPropertyMessage = new ReportPropertyMessage();
        ChildDeviceMessage child = new ChildDeviceMessage();
        // 设置子设备id
        child.setChildDeviceId(String.valueOf(this.getLabelId()));
        // 设置子设备的父设备id
        child.setDeviceId(this.getDeviceId());
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("antennaChannel", antennaChannel);
        properties.put("labelType", labelType);
        properties.put("labelId", labelId);
        properties.put("idCheckSum", idCheckSum);
        properties.put("labelStatus", labelStatus);
        properties.put("rssi", rssi);
        properties.put("timeStamp", toTimeString(timeStamp));
        reportPropertyMessage.setProperties(properties);
        reportPropertyMessage.setDeviceId(String.valueOf(this.getLabelId()));
        child.setChildDeviceMessage(reportPropertyMessage);
        return child;
    }
}
