package com.malpro.importer.controller;

import com.malpro.importer.service.IStorageService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    
    @InjectMocks
    private ImportController importController;

    @Test
    @DisplayName("File successfully received test")
    void fileSuccessfullyReceivedTest() throws IOException {
        MultipartFile file = mock(MultipartFile.class);

        doNothing().when(iStorageService).saveFile(file);

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