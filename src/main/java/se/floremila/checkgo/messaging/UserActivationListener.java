package se.floremila.checkgo.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import se.floremila.checkgo.config.RabbitMQConfig;
import se.floremila.checkgo.entity.User;
import se.floremila.checkgo.exception.NotFoundException;
import se.floremila.checkgo.repository.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActivationListener {

    private final UserRepository userRepository;

    @RabbitListener(queues = RabbitMQConfig.ACTIVATION_QUEUE)
    public void activateUser(Long userId) {
        log.info("Received activation request for user {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found for activation"));

        user.setEnabled(true);
        userRepository.save(user);

        log.info("User {} activated successfully", userId);
    }
}
