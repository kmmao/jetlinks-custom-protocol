package org.company.protocol.rfid.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.company.protocol.rfid.TcpMessageHeader;
import org.jetlinks.core.utils.BytesUtils;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Register extends TcpMessageHeader {

    private short desc_code;

    private int reg_code;

    private String crc16;

    public Register(byte[] bytes)
    {
        super(bytes);
        short descCode = (short)BytesUtils.beToInt(bytes, 30, 2);
        int regCode = BytesUtils.beToInt(bytes, 32, 4);
        setDesc_code(descCode);
        setReg_code(regCode);
    }

}
