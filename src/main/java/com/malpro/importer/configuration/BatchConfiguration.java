package com.malpro.importer.configuration;

import static com.malpro.importer.configuration.Constants.BATCH_LOADER;
import static com.malpro.importer.configuration.Constants.BATCH_LOADER_FILE_NAME;
import static com.malpro.importer.configuration.Constants.LOADER_STEP;
import static com.malpro.importer.configuration.Constants.PROCESS_ITEM_READER;
import static com.malpro.importer.configuration.Constants.SUPPLIER_UUID;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.amqp.AmqpItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malpro.importer.dto.ItemDto;
import com.malpro.importer.step.CustomAmqpItemWriter;
import com.malpro.importer.step.ItemDtoProcessor;

/**
 * Created by fahian on 02.12.22.
 */
@Configuration
public class BatchConfiguration {

    private final ImporterProperties importerProperties;

    private final RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper;

    public BatchConfiguration(ImporterProperties importerProperties,
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper) {
        this.importerProperties = importerProperties;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Bean
    public Job itemsProcessJob(JobRepository jobRepository,
            Step step) {
        return new JobBuilder(BATCH_LOADER, jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(step)
                .end()
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            JsonItemReader<ItemDto> jsonReader,
            ItemDtoProcessor itemDtoProcessor,
            CustomAmqpItemWriter<ItemDto> amqpWriter) {
        return new StepBuilder(LOADER_STEP, jobRepository)
                .<ItemDto, ItemDto>chunk(10, transactionManager)
                .reader(jsonReader)
                .processor(itemDtoProcessor)
                .writer(amqpWriter)
                .build();
    }

    @Bean
    @StepScope
    public JsonItemReader<ItemDto> jsonReader(@Value("#{jobParameters['" +
            BATCH_LOADER_FILE_NAME + "']}") String fileName) {
        return new JsonItemReaderBuilder<ItemDto>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(ItemDto.class))
                .resource(new PathResource(Paths.get(importerProperties.getFolder() + File.separator + fileName)))
                .name(PROCESS_ITEM_READER + "-json")
                .build();
    }

    @Bean
    public ItemDtoProcessor itemDtoProcessor() {
        return new ItemDtoProcessor();
    }

    @Bean
    public AmqpItemWriter<ItemDto> amqpWriter() {
        return new AmqpItemWriter<>(this.rabbitTemplate);
    }

    @Bean
    @StepScope
    public CustomAmqpItemWriter<ItemDto> customAmqpWriter(@Value("#{jobParameters['" + SUPPLIER_UUID + "']}") String supplierUUID) {
        var writer = new CustomAmqpItemWriter<ItemDto>(this.rabbitTemplate, this.objectMapper);

        if (!supplierUUID.isBlank()) {
            var headers = new HashMap<String, String>();
            headers.put(SUPPLIER_UUID, supplierUUID);
            writer.setHeaders(headers);
        }

        return writer;    
    }
}