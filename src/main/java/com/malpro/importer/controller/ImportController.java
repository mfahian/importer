package com.malpro.importer.controller;

import com.malpro.importer.configuration.ApiConfiguration;
import com.malpro.importer.service.BatchImportService;
import com.malpro.importer.service.IBatchImportService;
import com.malpro.importer.service.IStorageService;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.malpro.importer.configuration.Constants.BATCH_LOADER_FILE_NAME;

/**
 * Created by fahian on 30.10.22.
 */
@RestController
@RequestMapping(ApiConfiguration.API_URI_V1)
@AllArgsConstructor
public class ImportController {

    private final IStorageService storageService;

    private final IBatchImportService batchImportService;

    @PostMapping("/")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {

        try {
            final var fileName = storageService.saveFile(file);
            batchImportService.processBatch(fileName);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        } catch (JobInstanceAlreadyCompleteException e) {
            throw new RuntimeException(e);
        } catch (JobExecutionAlreadyRunningException e) {
            throw new RuntimeException(e);
        } catch (JobParametersInvalidException e) {
            throw new RuntimeException(e);
        } catch (JobRestartException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.accepted().build();
    }
}
