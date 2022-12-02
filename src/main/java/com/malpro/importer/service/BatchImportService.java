package com.malpro.importer.service;

import static com.malpro.importer.configuration.Constants.BATCH_LOADER_FILE_NAME;
import static com.malpro.importer.configuration.Constants.SUPPLIER_UUID;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

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
    public void processBatch(@NonNull String fileName, @NonNull String supplierUUID) throws JobExecutionAlreadyRunningException, JobRestartException,
            JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        jobLauncher.run(itemsProcessJob, prepareJobParameters(fileName, supplierUUID));
    }

    private JobParameters prepareJobParameters(@NonNull String fileName, @NonNull String supplierUUID) {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString(BATCH_LOADER_FILE_NAME, fileName);
        jobParametersBuilder.addString(SUPPLIER_UUID, supplierUUID);
        return jobParametersBuilder.toJobParameters();
    }
}
