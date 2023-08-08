package tech.bytespot.hamonygpsserver.version2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.messaging.MessageChannel;

@Slf4j
@Configuration
@EnableIntegration
@IntegrationComponentScan
public class TcpConfig {
    @Bean
    public AbstractServerConnectionFactory serverCF() {
        TcpNetServerConnectionFactory scf = new TcpNetServerConnectionFactory(5500);
        scf.setDeserializer(new CustomGpsDeserializer());
        return scf;
    }


    @Bean
    public TcpInboundGateway inbound(AbstractServerConnectionFactory connectionFactory)  {
        TcpInboundGateway inGate = new TcpInboundGateway();
        inGate.setConnectionFactory(connectionFactory);
        inGate.setRequestChannel(tcpIn());
        log.info("Incoming message from sender: {}", connectionFactory.getSender().toString());
        return inGate;
    }

    @Bean
    public MessageChannel tcpIn() {
        return new DirectChannel();
    }
}
