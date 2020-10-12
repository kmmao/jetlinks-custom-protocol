package org.company.protocol.rfid.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.company.protocol.rfid.TcpDeviceMessage;
import org.company.protocol.rfid.TcpMessageHeader;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.property.ReportPropertyMessage;
import org.jetlinks.core.utils.BytesUtils;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HeartBeat extends TcpMessageHeader implements TcpDeviceMessage {

    /** 设备工作状态
     * 000000xx
     * bit 0 : gprs连接
     * bit 1 ： 有线连接
     * 000x0000
     * bit 4 : 标签传输标识
     * 00x00000
     * bit 5 : 设备断电标识
     * 0000xxxx 00000000
     * bit 8-11 : 电池电压
     */
    private short deviceWorkStatus;

    /** 设备状态
     * 000000xx
     * bit 0 : gprs传输的
     * bit 1 : 有线传输的
     * xxxxxxxx 00000000
     * bit 8-15 : gprs信号强度
     */
    private short deviceStatus;

    // 设备版本
    private short ver;

    // 设备时间
    private byte[] timeStamp;

    public HeartBeat(byte[] bytes)
    {
        super(bytes);
        this.deviceWorkStatus = (short)BytesUtils.beToInt(bytes, 30, 2);
        this.deviceStatus = (short)BytesUtils.beToInt(bytes, 32, 2);
        this.ver = (short)BytesUtils.beToInt(bytes, 34, 2);
        this.timeStamp = new byte[6];
        BytesUtils.numberToBe(this.timeStamp , BytesUtils.beToInt(bytes, 36, 6), 0, 6);
    }

    @Override
    public DeviceMessage toDeviceMessage() {
        ReportPropertyMessage reportPropertyMessage = new ReportPropertyMessage();
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("deviceWorkStatus_connect_type", (this.deviceWorkStatus & 0x0001));
        properties.put("deviceWorkStatus_label_transmit_status", (this.deviceWorkStatus & 0x0010) >> 4);
        properties.put("deviceWorkStatus_power_supply", (this.deviceWorkStatus & 0x0020) >> 5);
        properties.put("deviceWorkStatus_voltage", (this.deviceWorkStatus & 0x0f00) >> 8);
        properties.put("deviceStatus_transmit_type", (this.deviceStatus & 0x0001));
        properties.put("deviceStatus_rssi", (this.deviceStatus & 0xff00) >> 8);
        reportPropertyMessage.setDeviceId(this.getDeviceId());
        reportPropertyMessage.setProperties(properties);
        return reportPropertyMessage;
    }
}
