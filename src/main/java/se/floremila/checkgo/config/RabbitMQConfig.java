package se.floremila.checkgo.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ACTIVATION_QUEUE = "activationQueue";

    @Bean
    public Queue activationQueue() {
        return new Queue(ACTIVATION_QUEUE, true);
    }
}

