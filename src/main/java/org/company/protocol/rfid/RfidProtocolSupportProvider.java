package org.company.protocol.rfid;

import org.jetlinks.core.ProtocolSupport;
import org.jetlinks.core.defaults.CompositeProtocolSupport;
import org.jetlinks.core.device.DeviceRegistry;
import org.jetlinks.core.message.codec.DefaultTransport;
import org.jetlinks.core.metadata.DefaultConfigMetadata;
import org.jetlinks.core.metadata.types.IntType;
import org.jetlinks.core.metadata.types.StringType;
import org.jetlinks.core.spi.ProtocolSupportProvider;
import org.jetlinks.core.spi.ServiceContext;
import org.jetlinks.supports.official.JetLinksDeviceMetadataCodec;
import reactor.core.publisher.Mono;

public class RfidProtocolSupportProvider implements ProtocolSupportProvider {

    private static final DefaultConfigMetadata opParams = new DefaultConfigMetadata("配置参数", "")
            .add("ask", "操作指示", "操作指示", new IntType())
            .add("StayTimeOut", "停留时间（秒）", "停留时间（秒）", new IntType())
            .add("RepeatFilter", "去重过滤（秒）", "去重过滤（秒）", new IntType())
            .add("gprsIp", "远端服务器IP地址", "远端服务器IP地址", new StringType())
            .add("gprsPort", "远端服务器端口", "远端服务器端口", new IntType())
            .add("threshold-1", "1号天线RSSI门限", "1号天线RSSI门限", new IntType())
            .add("threshold-2", "2号天线RSSI门限", "2号天线RSSI门限", new IntType())
            .add("threshold-3", "3号天线RSSI门限", "3号天线RSSI门限", new IntType())
            .add("threshold-4", "4号天线RSSI门限", "4号天线RSSI门限", new IntType());


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

        support.addConfigMetadata(DefaultTransport.TCP, opParams);

        return Mono.just(support);
    }
}
