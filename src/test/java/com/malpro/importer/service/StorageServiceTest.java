package com.malpro.importer.service;

import com.malpro.importer.configuration.ImporterProperties;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;


/**
 * Created by fahian on 30.10.22.
 */
@ExtendWith(MockitoExtension.class)
class StorageServiceTest {
    private static final String FILE_NAME = "test.json";
    static Path uploadPath;
    static MultipartFile multipartFile;
    @Mock
    private ImporterProperties importerProperties;
    @InjectMocks
    private StorageService storageService;


    @BeforeAll
    static void setup() {
        multipartFile = new MockMultipartFile(FILE_NAME, new byte[16]);
    }

    @BeforeEach
    void initializeConfig() {
        when(importerProperties.getFolder()).thenReturn("importer/files-to-process");
        uploadPath = Paths.get(importerProperties.getFolder());
    }

    @AfterEach
    void teardown() throws IOException {
        try (Stream<Path> walk = Files.walk(uploadPath)) {
            walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Test
    @DisplayName("Store file test")
    void storeFileTest() throws IOException {
        long filesCount;
        String fileName;

        storageService.saveFile(multipartFile);

        try (Stream<Path> files = Files.list(uploadPath)) {
            final var paths = files.toList();
            filesCount = paths.size();
            fileName = paths.get(0).getFileName().toString();
        }

        assertThat(filesCount, Matchers.is(1L));
        assertThat(fileName, Matchers.not(FILE_NAME));
    }

    @Test
    @DisplayName("Files directory does not exist test")
    void filesDirectoryDoesNotExistTest() throws IOException {
        if (Files.exists(uploadPath)) {
            Files.delete(uploadPath);
        }

        storageService.saveFile(multipartFile);

        assertThat(Files.exists(uploadPath), Matchers.is(true));
    }

    @Test
    @DisplayName("Files directory exists test")
    void filesDirectoryExistsTest() throws IOException {
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        storageService.saveFile(multipartFile);

        assertThat(Files.exists(uploadPath), Matchers.is(true));
    }
}