package org.company.protocol.rfid.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.company.protocol.rfid.RfidLabelPayload;
import org.company.protocol.rfid.TcpMessageHeader;
import org.company.protocol.rfid.TlvType;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.utils.BytesUtils;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@NoArgsConstructor
@Getter
public class Upload extends TcpMessageHeader implements RfidLabelPayload {

    private List<Upload> objList;

    public Upload(byte[] bytes)
    {
        super(bytes);
        short bodyLenTotal = (short)(BytesUtils.beToInt(bytes, 2, 2));
        this.objList = new LinkedList<>();
        int offset = 30;
        int index = 1;
        int lenTotalLeft = bodyLenTotal - super.getHeadLength();
        if (lenTotalLeft > 0)
        {
            byte[] tlvs = new byte[lenTotalLeft];
            System.arraycopy(bytes, offset, tlvs, 0, lenTotalLeft);
            while (lenTotalLeft > 0)
            {
                short theType = (short)BytesUtils.beToInt(tlvs, 0, 2);
                TlvType type = TlvType.of(theType).orElseThrow(IllegalArgumentException::new);
                int tlvRawDataLength = type.getLength();
                byte[] tlvRawData = new byte[tlvRawDataLength];
                System.arraycopy(tlvs, 0, tlvRawData, 0, tlvRawDataLength);
                Upload tlvObj = type.getPayLoadSupplier().apply(tlvRawData);
                tlvObj.setDeviceId(this.getDeviceId());
                objList.add(tlvObj);
                lenTotalLeft -= tlvRawDataLength;
                if (lenTotalLeft > 0) {
                    tlvs = new byte[lenTotalLeft];
                    System.arraycopy(bytes, offset + tlvRawDataLength * index, tlvs, 0, lenTotalLeft);
                    index++;
                }
            }
        }
    }

    @Override
    public DeviceMessage toRegisterInfo() {
        return this.toRegisterInfo();
    }

    @Override
    public DeviceMessage toPropertyInfo() {
        return this.toPropertyInfo();
    }

    @Override
    public DeviceMessage toOnlineInfo()
    {
        return this.toOnlineInfo();
    }

    @Override
    public DeviceMessage toUnRegisterInfo()
    {
        return this.toUnRegisterInfo();
    }

    @Override
    public DeviceMessage toOffLineInfo()
    {
        return this.toOffLineInfo();
    }

    public String getLabelId() { return this.getLabelId(); }

}
