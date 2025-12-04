package se.floremila.checkgo.messaging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import se.floremila.checkgo.config.RabbitMQConfig;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserActivationPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void sendActivationRequest(Long userId) {
        log.info("Sending activation request for user {}", userId);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ACTIVATION_EXCHANGE,
                RabbitMQConfig.ACTIVATION_ROUTING_KEY,
                userId
        );
    }
}

