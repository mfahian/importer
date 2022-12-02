package com.malpro.importer.controller;

import java.io.IOException;

import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.malpro.importer.configuration.ApiConfiguration;
import com.malpro.importer.service.IBatchImportService;
import com.malpro.importer.service.IStorageService;

import lombok.AllArgsConstructor;

/**
 * Created by fahian on 30.10.22.
 */
@RestController
@RequestMapping(ApiConfiguration.API_URI_V1)
@AllArgsConstructor
public class ImportController {

    private final IStorageService storageService;

    private final IBatchImportService batchImportService;

    @PostMapping("/supplier/{supplierUUID}")
    public ResponseEntity<String> handleFileUpload(@PathVariable String supplierUUID,
            @RequestParam("file") MultipartFile file) {

        String fileName;

        if (supplierUUID.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            fileName = storageService.saveFile(file);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        if (!fileName.isEmpty()) {
            try {
                batchImportService.processBatch(fileName, supplierUUID);
            } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException
                    | JobParametersInvalidException | JobRestartException e) {
                return ResponseEntity.internalServerError().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.accepted().build();
    }
}