package com.malpro.importer.service;

import com.malpro.importer.configuration.ImporterProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Created by fahian on 30.10.22.
 */
@Service
public class StorageService implements IStorageService {

    private final ImporterProperties importerProperties;

    public StorageService(ImporterProperties importerProperties) {
        this.importerProperties = importerProperties;
    }

    @Override
    public String saveFile(MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(importerProperties.getFolder());

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID() + ".json";

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        return fileName;
    }
}
