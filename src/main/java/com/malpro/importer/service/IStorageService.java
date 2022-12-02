package com.malpro.importer.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created by fahian on 30.10.22.
 */
public interface IStorageService {

    String saveFile(MultipartFile file) throws IOException;
}
