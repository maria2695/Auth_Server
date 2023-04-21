package com.softserve.authserver.messaging;

import com.softserve.authserver.auth.JwtUtils;
import com.softserve.authserver.dto.UserDTO;
import com.softserve.authserver.model.User;
import com.softserve.authserver.repository.RoleRepository;
import com.softserve.authserver.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

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
        logger.info("User Details Received is.. " + user);
    }
    /*@RabbitListener(queues = "${spring.rabbitmq.authqueue}")
    public void receivedAuthMessage(UserDTO userDTO) {
        logger.info("User Details Received is.. " + userDTO);
        if (userRepository.findByEmail(userDTO.getEmail()) == null) {
            throw new EntityNotFoundException();
        }
        User user = userRepository.findByEmail(userDTO.getEmail());
        if (!userDTO.getPassword().equals(user.getPassword())) {
            throw new EntityNotFoundException();
        }
        String token = jwtUtils.generateToken(user);
        producerController.sendMessage(token);
    }*/

}