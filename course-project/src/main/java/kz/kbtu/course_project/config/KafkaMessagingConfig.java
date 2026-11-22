package kz.kbtu.course_project.config;

import kz.kbtu.course_project.domain.event.CharacterCreatedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DelegatingByTypeSerializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@EnableScheduling
@ConditionalOnProperty(
        name = "app.messaging.kafka.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class KafkaMessagingConfig {

    @Bean
    public ProducerFactory<String, Object> kafkaProducerFactory(
            KafkaProperties kafkaProperties
    ) {
        // Preserve all Spring Boot-bound broker, SSL and security settings.
        Map<String, Object> producerProperties =
                new HashMap<>(kafkaProperties.buildProducerProperties());
        producerProperties.put(ProducerConfig.ACKS_CONFIG, "all");
        producerProperties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        producerProperties.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);

        DelegatingByTypeSerializer valueSerializer = new DelegatingByTypeSerializer(Map.of(
                byte[].class, new ByteArraySerializer(),
                CharacterCreatedEvent.class,
                new JacksonJsonSerializer<CharacterCreatedEvent>().noTypeInfo()
        ));

        return new DefaultKafkaProducerFactory<>(
                producerProperties,
                new StringSerializer(),
                valueSerializer
        );
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(
            ProducerFactory<String, Object> kafkaProducerFactory
    ) {
        return new KafkaTemplate<>(kafkaProducerFactory);
    }

    @Bean
    public NewTopic charactersCreatedTopic() {
        return TopicBuilder.name("characters.created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic charactersCreatedDeadLetterTopic() {
        return TopicBuilder.name("characters.created.DLT")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public CommonErrorHandler kafkaErrorHandler(
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception) -> new TopicPartition(
                        record.topic() + ".DLT",
                        record.partition()
                )
        );

        return new DefaultErrorHandler(recoverer, new FixedBackOff(1_000L, 2));
    }
}
