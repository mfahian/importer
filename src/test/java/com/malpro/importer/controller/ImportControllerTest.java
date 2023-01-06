package com.malpro.importer.controller;

import com.malpro.importer.service.IBatchImportService;
import com.malpro.importer.service.IStorageService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by fahian on 14.11.22.
 */
@ExtendWith(MockitoExtension.class)
class ImportControllerTest {

    @Mock
    private IStorageService iStorageService;

    @Mock
    private IBatchImportService batchImportService;

    @InjectMocks
    private ImportController importController;

    @Test
    @DisplayName("File successfully received test")
    void fileSuccessfullyReceivedTest() throws IOException, JobInstanceAlreadyCompleteException,
            JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        MultipartFile file = mock(MultipartFile.class);

        when(iStorageService.saveFile(file)).thenReturn(UUID.randomUUID() + ".json");
        doNothing().when(batchImportService).processBatch(anyString());

        final var response = importController.handleFileUpload(file);

        assertThat(response.getStatusCode(), Matchers.is(HttpStatus.ACCEPTED));
    }

    @Test
    @DisplayName("Failed to process received file test")
    void failedToProcessReceivedFileTest() throws IOException {
        MultipartFile file = mock(MultipartFile.class);

        doThrow(new IOException()).when(iStorageService).saveFile(file);

        final var response = importController.handleFileUpload(file);

        assertThat(response.getStatusCode(), Matchers.is(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}