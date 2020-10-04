package org.company.protocol.rfid.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.company.protocol.rfid.TcpMessageHeader;
import org.jetlinks.core.utils.BytesUtils;
import org.company.protocol.rfid.TcpPayload;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Register implements TcpPayload {

    private short desc_code;

    private int reg_code;

    private String crc16;

    private TcpMessageHeader header;

    @Override
    public byte[] toBytes() {
        return new byte[0];

    }

    @Override
    public void fromBytes(byte[] bytes, int offset) {
        setHeader(createHeader(bytes));
        short descCode = (short)BytesUtils.beToInt(bytes, offset, 2);
        int regCode = BytesUtils.beToInt(bytes, offset+2, 4);
        setDesc_code(descCode);
        setReg_code(regCode);
    }

//    @Override
//    public DeviceMessage toDeviceMessage() {
//        ReportPropertyMessage message = new ReportPropertyMessage();
//        Map<String, Object> map = new HashMap<>();
//        map.put("desc_code", getDesc_code());
//        map.put("reg_code",  getReg_code());
//        message.setProperties(map);
////        message.setProperties(Collections.singletonMap("desc_code", getDesc_code()));
////        message.setProperties(Collections.singletonMap("reg_code", getReg_code()));
//        message.setDeviceId(getHeader().getDeviceId());
//        message.setTimestamp(System.currentTimeMillis());
//        return message;
//    }

}
