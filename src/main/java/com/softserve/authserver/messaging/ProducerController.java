package com.softserve.authserver.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ProducerController {

    private ProducerService producerService;
    private static final Logger logger = LoggerFactory.getLogger(ProducerController.class);

    @Autowired
    public ProducerController(ProducerService producerService) {
        this.producerService = producerService;
    }

    @Value("${app.message}")
    private String response;

    public ResponseEntity<String> sendMessage(String token) {
        producerService.sendMessage(token);
        logger.info("token sent: " + token);
        return ResponseEntity.ok(response);
    }
}