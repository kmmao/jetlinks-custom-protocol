package org.company.protocol.rfid;

import org.jetlinks.core.message.DeviceMessage;

public interface RfidLabelPayload {

    DeviceMessage toRegisterInfo();

    DeviceMessage toPropertyInfo();

    DeviceMessage toOnlineInfo();
}
