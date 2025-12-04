package se.floremila.checkgo.config;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ACTIVATION_QUEUE = "user-activation-queue";
    public static final String ACTIVATION_EXCHANGE = "user-activation-exchange";
    public static final String ACTIVATION_ROUTING_KEY = "user.activation";

    @Bean
    public Queue activationQueue() {
        return new Queue(ACTIVATION_QUEUE, true);
    }

    @Bean
    public TopicExchange activationExchange() {
        return new TopicExchange(ACTIVATION_EXCHANGE);
    }

    @Bean
    public Binding activationBinding() {
        return BindingBuilder
                .bind(activationQueue())
                .to(activationExchange())
                .with(ACTIVATION_ROUTING_KEY);
    }
}


