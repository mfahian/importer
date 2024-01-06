package com.malpro.importer.service;

import static com.malpro.importer.configuration.Constants.BATCH_LOADER_FILE_NAME;
import static com.malpro.importer.configuration.Constants.SUPPLIER_UUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import com.malpro.importer.random.RandomBeansExtension;

/**
 * Created by fahian on 30.10.22.
 */
@ExtendWith({ MockitoExtension.class, RandomBeansExtension.class })
class BatchImportServiceTest {

    @Mock
    private JobLauncher jobLauncher;
    
    @Mock
    private Job itemsProcessJob;

    @InjectMocks
    private BatchImportService batchImportService;

    @Captor
    ArgumentCaptor<JobParameters> jobParametersCator;

    @Test
    @DisplayName("Files directory exists test")
    void filesDirectoryExistsTest() throws JobExecutionAlreadyRunningException, JobRestartException,
    JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        
        var supplierUUID = "1234";
        var fileName = "file.json";

        batchImportService.processBatch(fileName, supplierUUID);

        verify(jobLauncher).run(eq(itemsProcessJob),jobParametersCator.capture());

        var jobParameters = jobParametersCator.getValue();

        assertThat(jobParameters.getParameters().size(), Matchers.is(2));
        assertThat(jobParameters.getString(BATCH_LOADER_FILE_NAME), Matchers.equalTo(fileName));
        assertThat(jobParameters.getString(SUPPLIER_UUID), Matchers.equalTo(supplierUUID));

    }
}