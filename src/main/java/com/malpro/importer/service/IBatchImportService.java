package com.malpro.importer.service;

import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.lang.NonNull;

/**
 * Created by fahian on 02.12.22.
 */
public interface IBatchImportService {
    void processBatch(@NonNull String fileName, @NonNull String supplierUUID) throws JobExecutionAlreadyRunningException, JobRestartException,
            JobInstanceAlreadyCompleteException, JobParametersInvalidException;
}
