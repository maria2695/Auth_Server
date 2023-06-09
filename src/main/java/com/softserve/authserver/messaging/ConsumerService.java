package com.softserve.authserver.messaging;

import com.softserve.authserver.auth.JwtUtils;
import com.softserve.authserver.dto.AuthDTO;
import com.softserve.authserver.model.User;
import com.softserve.authserver.repository.RoleRepository;
import com.softserve.authserver.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProducerController producerController;
    private final JwtUtils jwtUtils;
    private static final Logger logger = LoggerFactory.getLogger(ConsumerService.class);

    @Autowired
    public ConsumerService(UserRepository userRepository, RoleRepository roleRepository, ProducerController producerController, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.producerController = producerController;
        this.jwtUtils = jwtUtils;
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue}")
    public void receivedMessage(User user) {
        user.setRole(roleRepository.getReferenceById(2L));
        User save = userRepository.save(user);
        logger.info("persisted " + save);
    }
    @RabbitListener(queues = "${spring.rabbitmq.authqueue}")
    public void receivedAuthMessage(AuthDTO authDTO) {
        logger.info("User Details Received is.. " + authDTO);
        if (userRepository.findByEmail(authDTO.getUsername()) == null) {
            throw new EntityNotFoundException("User not found");
        }
        User user = userRepository.findByEmail(authDTO.getUsername());
        if (!passwordEncoder.matches(authDTO.getPassword(), user.getPassword())) {
            System.out.println(user.getPassword());
            System.out.println(authDTO.getPassword());
            throw new EntityNotFoundException("Wrong password");
        }
        String token = jwtUtils.generateToken(user);
        producerController.sendMessage(token);
    }

}