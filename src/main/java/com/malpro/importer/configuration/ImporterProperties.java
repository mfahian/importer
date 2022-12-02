package com.malpro.importer.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by fahian on 02.12.22.
 */
@ConfigurationProperties("importer")
@Getter
@Setter
public class ImporterProperties {
    private String folder;
}
