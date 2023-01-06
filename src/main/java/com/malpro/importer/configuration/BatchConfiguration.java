package com.malpro.importer.configuration;

import com.malpro.importer.dto.ItemDto;
import com.malpro.importer.example.PersonItemProcessor;
import com.malpro.importer.processor.ItemDtoProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.amqp.AmqpItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;

import java.io.File;
import java.nio.file.Paths;

import static com.malpro.importer.configuration.Constants.BATCH_LOADER;
import static com.malpro.importer.configuration.Constants.BATCH_LOADER_FILE_NAME;
import static com.malpro.importer.configuration.Constants.LOADER_STEP;
import static com.malpro.importer.configuration.Constants.PROCESS_ITEM_READER;

/**
 * Created by fahian on 02.12.22.
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final ImporterProperties importerProperties;

    private final RabbitTemplate rabbitTemplate;

    public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
                              ImporterProperties importerProperties, RabbitTemplate rabbitTemplate) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.importerProperties = importerProperties;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Bean
    public Job itemsProcessJob(Step step) {
        return this.jobBuilderFactory
                .get(BATCH_LOADER)
//                .validator(validator())
                .start(step)
                .build();
    }

//    @Bean
//    public JobParametersValidator validator() {
//        return jobParameters -> {
//            String fileName = jobParameters.getString(BATCH_LOADER_FILE_NAME);
//            if (StringUtils.isBlank(fileName)) {
//                throw new JobParametersInvalidException("File is empty");
//            }
//            try {
//                Path file = Paths.get(fileName);
//                if (Files.notExists(file) || !Files.isReadable(file)) {
//                    throw new Exception("Unreadable file");
//                }
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        };
//    }

    @Bean
    @StepScope
    public FlatFileItemReader<ItemDto> reader(@Value("#{jobParameters['" + BATCH_LOADER_FILE_NAME + "']}") String fileName) {
        return new FlatFileItemReaderBuilder<ItemDto>()
                .name(PROCESS_ITEM_READER)
                .resource(new PathResource(Paths.get(importerProperties.getFolder() + File.separator + fileName)))
                .delimited()
                .names(new String[]{"firstName", "lastName"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(ItemDto.class);
                }})
                .build();
    }

    @Bean
    @StepScope
    public JsonItemReader<ItemDto> jsonReader(@Value("#{jobParameters['" + BATCH_LOADER_FILE_NAME + "']}") String fileName) {
        return new JsonItemReaderBuilder<ItemDto>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(ItemDto.class))
                .resource(new PathResource(Paths.get(importerProperties.getFolder() + File.separator + fileName)))
                .name(PROCESS_ITEM_READER + "-json")
                .build();
    }

    @Bean
    @StepScope
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }

    @Bean
    public ItemDtoProcessor itemDtoProcessor() {
        return new ItemDtoProcessor();
    }

    @Bean
    @StepScope
    public ItemWriter<ItemDto> writer() {
        return list -> {
            for (ItemDto person : list) {
                System.err.println("Person item: " + person);
            }
        };
    }

    @Bean
    public AmqpItemWriter<ItemDto> amqpWriter(){
        AmqpItemWriter<ItemDto> amqpItemWriter = new AmqpItemWriter<>(this.rabbitTemplate);
        return amqpItemWriter;
    }

    @Bean
    public Step step(JsonItemReader<ItemDto> reader, ItemDtoProcessor processor, ItemWriter<ItemDto> writer) {
        return stepBuilderFactory.get(LOADER_STEP)
                .<ItemDto, ItemDto>chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}