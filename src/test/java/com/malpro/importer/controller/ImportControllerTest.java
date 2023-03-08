package com.malpro.importer.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.UUID;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import com.malpro.importer.service.IBatchImportService;
import com.malpro.importer.service.IStorageService;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;

/**
 * Created by fahian on 14.11.22.
 */
@ExtendWith({ MockitoExtension.class, RandomBeansExtension.class })
class ImportControllerTest {

    @Mock
    private IStorageService iStorageService;

    @Mock
    private IBatchImportService batchImportService;

    @InjectMocks
    private ImportController importController;

    @Test
    @DisplayName("File successfully received test")
    void fileSuccessfullyReceivedTest(@Random UUID uuid) throws IOException, JobInstanceAlreadyCompleteException,
            JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        var supplierUUID = "1234";
        var fileName = uuid.toString() + ".json";
        MultipartFile file = mock(MultipartFile.class);

        assertThat(supplierUUID, Matchers.notNullValue());
        assertThat(fileName, Matchers.notNullValue());
        
        when(iStorageService.saveFile(file)).thenReturn(fileName);
        doNothing().when(batchImportService).processBatch(fileName, supplierUUID);

        final var response = importController.handleFileUpload(supplierUUID, file);

        assertThat(response.getStatusCode(), Matchers.is(HttpStatus.ACCEPTED));
 
        verify(batchImportService).processBatch(fileName, supplierUUID);
    }

    @ParameterizedTest
    @DisplayName("Supplier UUID is blank test")
    @ValueSource(strings = { "", "     " })
    void failedToProcessReceivedFileTest(String supplierUUID) throws IOException, JobInstanceAlreadyCompleteException,
            JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        MultipartFile file = mock(MultipartFile.class);

        final var response = importController.handleFileUpload(supplierUUID, file);

        assertThat(response.getStatusCode(), Matchers.is(HttpStatus.BAD_REQUEST));

        verify(batchImportService, never()).processBatch(anyString()+"", anyString()+"");

    }

    @Test
    @DisplayName("Failed to process file test")
    void failedToProcessFileTest(@Random String supplierUUID) throws IOException, JobExecutionAlreadyRunningException, 
            JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        MultipartFile file = mock(MultipartFile.class);

        doThrow(new IOException()).when(iStorageService).saveFile(file);

        final var response = importController.handleFileUpload(supplierUUID, file);

        assertThat(response.getStatusCode(), Matchers.is(HttpStatus.INTERNAL_SERVER_ERROR));
        verify(batchImportService, never()).processBatch(anyString()+"", anyString()+"");
    }

    @Test
    @DisplayName("No file name test")
    void noFileNameTest(@Random String supplierUUID) throws IOException, JobExecutionAlreadyRunningException, 
            JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        MultipartFile file = mock(MultipartFile.class);

        when(iStorageService.saveFile(file)).thenReturn("");

        final var response = importController.handleFileUpload(supplierUUID, file);

        assertThat(response.getStatusCode(), Matchers.is(HttpStatus.NOT_FOUND));
        verify(batchImportService, never()).processBatch(anyString()+"", anyString()+"");
    }

    @Test
    @DisplayName("Failed to start job test")
    void failedToStartJobTest(@Random UUID uuid) throws IOException, JobInstanceAlreadyCompleteException,
            JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        var fileName = uuid.toString() + ".json";
        var supplierUUID = "1234";
        MultipartFile file = mock(MultipartFile.class);

        assertThat(supplierUUID, Matchers.notNullValue());
        assertThat(fileName, Matchers.notNullValue());

        when(iStorageService.saveFile(file)).thenReturn(fileName);
        doThrow(new JobInstanceAlreadyCompleteException("exception")).when(batchImportService).processBatch(fileName, supplierUUID);;

        final var response = importController.handleFileUpload(supplierUUID, file);

        assertThat(response.getStatusCode(), Matchers.is(HttpStatus.INTERNAL_SERVER_ERROR));
 
        verify(batchImportService).processBatch(fileName, supplierUUID);
    }
}