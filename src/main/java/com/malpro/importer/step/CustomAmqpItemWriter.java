package com.malpro.importer.step;

import java.util.Map;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

public class CustomAmqpItemWriter<T> implements ItemWriter<T> {

    private final AmqpTemplate amqpTemplate;

    private final ObjectMapper objectMapper;

    @Getter
    @Setter
    private Map<String, String> headers;

    public CustomAmqpItemWriter(AmqpTemplate amqpTemplate, ObjectMapper objectMapper) {
        this.amqpTemplate = amqpTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void write(@NonNull Chunk<? extends T> items) throws JsonProcessingException {

        String messageJson = objectMapper.writeValueAsString(items.getItems());
        
        var message = MessageBuilder.withBody(messageJson.getBytes());

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            message.setHeader(entry.getKey(), entry.getValue());
        }
                                
        amqpTemplate.send(message.build());
    }
}
