package org.company.protocol.rfid.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.company.protocol.rfid.TcpDeviceMessage;
import org.company.protocol.rfid.TcpMessageHeader;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.utils.BytesUtils;

import java.util.Map;

@Slf4j(topic = "system.rfid")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Config extends TcpMessageHeader implements TcpDeviceMessage {

    private byte parm_type;

    private byte return_opt;

    private byte feature;

    private byte mode;

    private short ver;

    private byte buzzer;

    private byte gprsType;

    private short RepeatFilter;

    private byte[] id;

    private short StayTimeOut;

    private byte dhcpEnable;

    private byte[] lanIp;

    private byte[] lanMask;

    private byte[] lanGateWay;

    // 端口 2字节
    private byte[] lanPort;

    private byte[] gprsIp;

    // 端口 2字节
    private byte[] gprsPort;

    private byte[] lan1Ip;

    // 端口 2字节
    private byte[] lan1Port;

    private byte[] lanMac;

    private byte[] reverse1;

    private byte[] antennaVer;

    private byte gprsRssi;

    private byte[] number;

    private byte[] threshold;

    private byte[] gain;

    private byte bluetooth;

    private byte communicaitonStatus;

    private byte labelFilter;

    private byte labelFilterTime;

    private short reverse2;

    public Config(byte[] bytes)
    {
        super(bytes);
        // 2字节起始标识 + 28字节报文头 + 1字节参数类型
        int offset = 31;
        this.parm_type = (byte)BytesUtils.beToInt(bytes, 30 ,1);
        if (this.parm_type == 0x10)
        {
            this.feature = (byte)BytesUtils.beToInt(bytes, offset ,1);
            this.mode = (byte)BytesUtils.beToInt(bytes, offset+1, 1);
            this.ver = (short)BytesUtils.beToInt(bytes, offset+2, 2);
            this.buzzer = (byte)BytesUtils.beToInt(bytes, offset+4, 1);
            this.gprsType = (byte)BytesUtils.beToInt(bytes, offset+5, 1);
            this.RepeatFilter = (short)BytesUtils.beToInt(bytes, offset+6, 2);
            this.id = new byte[16];
            System.arraycopy(bytes, offset+8, this.id, 0, 16);
            this.StayTimeOut = (short)BytesUtils.beToInt(bytes, offset+24, 2);
            this.dhcpEnable = (byte)BytesUtils.beToInt(bytes, offset+26, 1);
            this.lanIp = new byte[4];
            System.arraycopy(bytes, offset+27, this.lanIp, 0, 4);
            this.lanMask = new byte[4];
            System.arraycopy(bytes, offset+31, this.lanMask, 0, 4);
            this.lanGateWay = new byte[4];
            System.arraycopy(bytes, offset+35, this.lanGateWay, 0, 4);
            this.lanPort = new byte[2];
            System.arraycopy(bytes, offset+39, this.lanPort, 0, 2);
            this.gprsIp = new byte[32];
            System.arraycopy(bytes, offset+41, this.gprsIp, 0, 32);
            this.gprsPort = new byte[2];
            System.arraycopy(bytes, offset+73, this.gprsPort, 0, 2);
            this.lan1Ip = new byte[32];
            System.arraycopy(bytes, offset+75, this.lan1Ip, 0, 32);
            this.lan1Port = new byte[2];
            System.arraycopy(bytes, offset+107, this.lan1Port, 0, 2);
            this.lanMac = new byte[6];
            System.arraycopy(bytes, offset+109, this.lanMac, 0, 6);
            this.reverse1 = new byte[28];
            System.arraycopy(bytes, offset+115, this.reverse1, 0, 28);
            this.antennaVer = new byte[8];
            System.arraycopy(bytes, offset+143, this.antennaVer, 0, 8);
            this.gprsRssi = (byte)BytesUtils.beToInt(bytes, offset+151, 1);
            this.number = new byte[16];
            System.arraycopy(bytes, offset+152, this.number, 0, 16);
            this.threshold = new byte[4];
            System.arraycopy(bytes, offset+168, this.threshold, 0, 4);
            this.gain = new byte[4];
            System.arraycopy(bytes, offset+172, this.gain, 0, 4);
            this.bluetooth = (byte)BytesUtils.beToInt(bytes, offset+176, 1);
            this.communicaitonStatus = (byte)BytesUtils.beToInt(bytes, offset+177, 1);
            this.labelFilter = (byte)BytesUtils.beToInt(bytes, offset+178, 1);
            this.labelFilterTime = (byte)BytesUtils.beToInt(bytes, offset+179, 1);
            this.reverse2 = (short)BytesUtils.beToInt(bytes, offset+180, 2);
            log.debug("in - {}", this.toString());
        }
        else {
            this.return_opt = (byte)BytesUtils.beToInt(bytes, 31 ,1);
            log.debug("return_opt = {}", this.return_opt);
        }
    }

    public static Config of(Config obj, int seqId, Map<String, Object> paraMap)
    {
        Config config = obj;

        short StayTimeOut = (short)(int)Integer.valueOf((String)paraMap.get("StayTimeOut"));
        short RepeatFilter = (short)(int)Integer.valueOf((String)paraMap.get("RepeatFilter"));
        String gprsIp = (String)paraMap.get("gprsIp");
        short port = (short)(int)Integer.valueOf((String)paraMap.get("gprsPort"));
        byte threshold1 = (byte)(int)Integer.valueOf((String)paraMap.get("threshold-1"));
        byte threshold2 = (byte)(int)Integer.valueOf((String)paraMap.get("threshold-2"));
        byte threshold3 = (byte)(int)Integer.valueOf((String)paraMap.get("threshold-3"));
        byte threshold4 = (byte)(int)Integer.valueOf((String)paraMap.get("threshold-4"));

        byte[] stayTimeOutBuf = new byte[2];
        BytesUtils.numberToLe(stayTimeOutBuf, StayTimeOut, 0, 2);
        config.StayTimeOut = (short)BytesUtils.beToInt(stayTimeOutBuf, 0, 2);

        byte[] RepeatFilterBuf = new byte[2];
        BytesUtils.numberToLe(RepeatFilterBuf, RepeatFilter, 0, 2);
        config.RepeatFilter = (short)BytesUtils.beToInt(RepeatFilterBuf, 0, 2);

        byte[] ipBytes = gprsIp.getBytes();
        System.arraycopy(ipBytes, 0, config.gprsIp, 0, ipBytes.length);

        config.gprsPort = new byte[2];
        BytesUtils.numberToLe(config.gprsPort, port, 0, 2);

        BytesUtils.numberToBe(config.threshold, threshold1, 0, 1);
        BytesUtils.numberToBe(config.threshold, threshold2, 1, 1);
        BytesUtils.numberToBe(config.threshold, threshold3, 2, 1);
        BytesUtils.numberToBe(config.threshold, threshold4, 3, 1);

        config.setSeqId(seqId);
        return config;
    }

    @Override
    public byte[] toBytes()
    {
        if (this.parm_type == 0x10)
        {
            byte[] buf = new byte[183];
            log.debug("out - {}", this.toString());
            BytesUtils.numberToBe(buf, 0x10, 0, 1);
            BytesUtils.numberToBe(buf, this.feature, 1, 1);
            BytesUtils.numberToBe(buf, this.mode, 2, 1);
            BytesUtils.numberToBe(buf, this.ver, 3, 2);
            BytesUtils.numberToBe(buf, this.buzzer, 5, 1);
            BytesUtils.numberToBe(buf, this.gprsType, 6, 1);
            BytesUtils.numberToBe(buf, this.RepeatFilter, 7, 2);
            System.arraycopy(this.id, 0, buf, 9, 16);
            BytesUtils.numberToBe(buf, this.StayTimeOut, 25, 2);
            BytesUtils.numberToBe(buf, this.dhcpEnable, 27, 1);
            System.arraycopy(this.lanIp, 0, buf, 28, 4);
            System.arraycopy(this.lanMask, 0, buf, 32, 4);
            System.arraycopy(this.lanGateWay, 0, buf, 36, 4);
            System.arraycopy(this.lanPort, 0, buf, 40, 2);
            System.arraycopy(this.gprsIp, 0, buf, 42, 32);
            System.arraycopy(this.gprsPort, 0, buf, 74, 2);
            System.arraycopy(this.lan1Ip, 0, buf, 76, 32);
            System.arraycopy(this.lan1Port, 0, buf, 108,2);
            System.arraycopy(this.lanMac, 0, buf, 110, 6);
            System.arraycopy(this.reverse1, 0, buf, 116, 28);
            System.arraycopy(this.antennaVer, 0, buf, 144, 8);
            BytesUtils.numberToBe(buf, this.gprsRssi, 152, 1);
            System.arraycopy(this.number, 0, buf, 153, 16);
            System.arraycopy(this.threshold, 0, buf, 169, 4);
            System.arraycopy(this.gain, 0, buf, 173, 4);
            BytesUtils.numberToBe(buf, this.bluetooth, 177, 1);
            BytesUtils.numberToBe(buf, this.communicaitonStatus, 178, 1);
            BytesUtils.numberToBe(buf, this.labelFilter, 179, 1);
            BytesUtils.numberToBe(buf, this.labelFilterTime, 180, 1);
            BytesUtils.numberToBe(buf, this.reverse2, 181, 2);

            super.setMessageLength((short)(buf.length + 28));
            super.setMessageTypeId((short)0x800a);
            super.setProtocolId(super.getProtocolId());
            super.setSecureId(super.getSecureId());

            byte[] bytesHead = super.toBytes();

            // 报文头+报文体的crc16结果
            byte[] byteCrc16 = doGetCrc(bytesHead, buf);

            byte[] byteStartTag = getStartTag();

            byte[] result = getFinalMessage(byteStartTag, bytesHead, buf, byteCrc16);

            return result;
        }
        else {
            byte buf[] = new byte[1];
            BytesUtils.numberToBe(buf, 0x80, 0, 1);

            super.setMessageLength((short)(buf.length + 28));
            super.setMessageTypeId((short)0x800a);
            super.setProtocolId(super.getProtocolId());
            super.setSecureId(super.getSecureId());

            byte[] bytesHead = super.toBytes();

            // 报文头+报文体的crc16结果
            byte[] byteCrc16 = doGetCrc(bytesHead, buf);

            byte[] byteStartTag = getStartTag();

            byte[] result = getFinalMessage(byteStartTag, bytesHead, buf, byteCrc16);

            return result;
        }
    }

    public ByteBuf toByteBuf(){
        return Unpooled.wrappedBuffer(this.toBytes());
    }

    @Override
    public DeviceMessage toDeviceMessage() {
        return null;
    }

    @Override
    public String toString() {
        return "Config{" +
                "feature=" + Integer.toHexString(feature) +
                ", mode=" + Integer.toHexString(mode) +
                ", ver=" + Integer.toHexString(ver) +
                ", buzzer=" + Integer.toHexString(buzzer) +
                ", gprsType=" + Integer.toHexString(gprsType) +
                ", RepeatFilter=" + Integer.toHexString(RepeatFilter) +
                ", id=" + new String(id) +
                ", StayTimeOut=" + Integer.toHexString(StayTimeOut) +
                ", dhcpEnable=" + Integer.toHexString(dhcpEnable) +
                ", lanIp=" + Hex.encodeHexString(lanIp) +
                ", lanMask=" + Hex.encodeHexString(lanMask) +
                ", lanGateWay=" + Hex.encodeHexString(lanGateWay) +
                ", lanPort=" + Hex.encodeHexString(lanPort) +
                ", gprsIp=" + Hex.encodeHexString(gprsIp) +
                ", gprsPort=" + Hex.encodeHexString(gprsPort) +
                ", lan1Ip=" + Hex.encodeHexString(lan1Ip) +
                ", lan1Port=" + Hex.encodeHexString(lan1Port) +
                ", lanMac=" + Hex.encodeHexString(lanMac) +
                ", reverse1=" + Hex.encodeHexString(reverse1) +
                ", antennaVer=" + Hex.encodeHexString(antennaVer) +
                ", gprsRssi=" + Integer.toHexString(gprsRssi) +
                ", number=" + new String(number) +
                ", threshold=" + Hex.encodeHexString(threshold) +
                ", gain=" + Hex.encodeHexString(gain) +
                ", bluetooth=" + Integer.toHexString(bluetooth) +
                ", communicaitonStatus=" + Integer.toHexString(communicaitonStatus) +
                ", labelFilter=" + Integer.toHexString(labelFilter) +
                ", labelFilterTime=" + Integer.toHexString(labelFilterTime) +
                ", reverse2=" + Integer.toHexString(reverse2) +
                '}';
    }
}
