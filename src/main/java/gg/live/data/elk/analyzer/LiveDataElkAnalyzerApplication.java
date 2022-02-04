package gg.live.data.elk.analyzer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.util.MimeType;

import java.nio.charset.StandardCharsets;

import static net.logstash.logback.argument.StructuredArguments.kv;

@SpringBootApplication
@Slf4j
public class LiveDataElkAnalyzerApplication implements RabbitListenerConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(LiveDataElkAnalyzerApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    public DefaultMessageHandlerMethodFactory messageHandlerMethodFactory() {
        final DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        final MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter(
                new MimeType("application", "json", StandardCharsets.UTF_8),
                new MimeType("application", "octet-stream", StandardCharsets.UTF_8)
        );
        converter.setObjectMapper(objectMapper());
        factory.setMessageConverter(converter);
        return factory;
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar rabbitListenerEndpointRegistrar) {
        rabbitListenerEndpointRegistrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
    }

    @RabbitListener(queues = "${live-data.queue}")
    public void listen(RoutableMessage message) {
        log.info(
                "Getting payload for {}", message.getLiveDataMatchUrn(),
                kv("payload", message.getPayload()),
                kv("type", message.getType()),
                kv("subject", message.getSubject()),
                kv("action", message.getAction()));
    }
}
