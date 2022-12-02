package com.malpro.importer.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.malpro.importer.configuration.Constants.BATCH_LOADER_FILE_NAME;

/**
 * Created by fahian on 02.12.22.
 */
@Service
public class BatchImportService implements IBatchImportService {

    private final JobLauncher jobLauncher;
    private final Job itemsProcessJob;

    public BatchImportService(JobLauncher jobLauncher, Job itemsProcessJob) {
        this.jobLauncher = jobLauncher;
        this.itemsProcessJob = itemsProcessJob;
    }

    @Override
    public void processBatch(String fileName) throws JobExecutionAlreadyRunningException, JobRestartException,
    JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        Map<String, JobParameter> parameterMap = new HashMap<>();
        parameterMap.put(BATCH_LOADER_FILE_NAME, new JobParameter(fileName));
        jobLauncher.run(itemsProcessJob, new JobParameters(parameterMap));
    }
}
