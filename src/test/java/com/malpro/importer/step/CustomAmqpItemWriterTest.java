package com.malpro.importer.step;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.batch.item.Chunk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.glytching.junit.extension.random.RandomBeansExtension;

@ExtendWith({MockitoExtension.class, RandomBeansExtension.class})
class CustomAmqpItemWriterTest {
    @Mock
    private AmqpTemplate amqpTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CustomAmqpItemWriter<String> customAmqpItemWriter;

    @Test
    @DisplayName("AMQP message write with header test")
    void amqpMessageWriteWithHeaderTest() throws JsonProcessingException {

        var chunk = new Chunk<String>();
        chunk.add("test");

        var headers = new HashMap<String, String>();
        headers.put("test", "header");

        customAmqpItemWriter.setHeaders(headers);

        when(objectMapper.writeValueAsString(chunk.getItems())).thenReturn("{}");

        assertDoesNotThrow(() -> customAmqpItemWriter.write(chunk));

        verify(objectMapper).writeValueAsString(anyList());
        verify(amqpTemplate).send(any(Message.class));
    }

    @Test
    @DisplayName("AMQP message write withpout header test")
    void amqpMessageWriteWithoutHeaderTest() throws JsonProcessingException {

        var chunk = new Chunk<String>();
        chunk.add("test");

        when(objectMapper.writeValueAsString(chunk.getItems())).thenReturn("{}");

        customAmqpItemWriter.setHeaders(new HashMap<String, String>());

        assertDoesNotThrow(() -> customAmqpItemWriter.write(chunk));

        verify(objectMapper).writeValueAsString(anyList());
        verify(amqpTemplate).send(any(Message.class));
    }
}
