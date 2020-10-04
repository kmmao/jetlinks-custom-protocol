package org.company.protocol.rfid;

import org.jetlinks.core.ProtocolSupport;
import org.jetlinks.core.defaults.CompositeProtocolSupport;
import org.jetlinks.core.device.DeviceRegistry;
import org.jetlinks.core.message.codec.DefaultTransport;
import org.jetlinks.core.spi.ProtocolSupportProvider;
import org.jetlinks.core.spi.ServiceContext;
import org.jetlinks.supports.official.JetLinksDeviceMetadataCodec;
import reactor.core.publisher.Mono;

public class RfidProtocolSupportProvider implements ProtocolSupportProvider {

    @Override
    public Mono<? extends ProtocolSupport> create(ServiceContext serviceContext) {
        CompositeProtocolSupport support = new CompositeProtocolSupport();

        support.setId("rfid");
        support.setName("rfid定位设备");
        support.setDescription("rfid定位设备协议");
        support.setMetadataCodec(new JetLinksDeviceMetadataCodec());

        serviceContext.getService(DeviceRegistry.class)
                .ifPresent(deviceRegistry -> {
                    RfidDeviceMessageCodec codec = new RfidDeviceMessageCodec(deviceRegistry);
                    support.addMessageCodecSupport(DefaultTransport.TCP, () -> Mono.just(codec));
                });

        return Mono.just(support);
    }
}
