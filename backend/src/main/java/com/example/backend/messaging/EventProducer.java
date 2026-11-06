package com.example.backend.messaging;

import com.example.backend.config.JmsConfig;
import com.example.backend.dto.event.UserCreatedEvent;
import com.example.backend.entity.User;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EventProducer {

    private final JmsTemplate jmsTemplate;

    public EventProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendUserCreatedEvent(User user) {
        UserCreatedEvent event = new UserCreatedEvent(
            user.getId(),
            LocalDateTime.now(),
            "USER_CREATED",
            user.getNom(),
            user.getPrenom(),
            user.getEmail(),
            user.getRole()
        );
        jmsTemplate.convertAndSend(JmsConfig.ALUMNI_QUEUE, event);
    }
}