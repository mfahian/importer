package com.malpro.importer.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Created by fahian on 02.12.22.
 */
@ConfigurationProperties("importer")
@Data
public class ImporterProperties {
    private String folder;
}
