package com.malpro.importer.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Created by fahian on 03.12.22.
 */
@Getter
@Setter
public class ItemDto {
    private String etimClass;
    private String referenceFeatureSystem;
    private Map<String, String> featuresMap;

    @Override
    public String toString() {
        return "etimClass: " + etimClass + ", referenceFeatureSystem: " + referenceFeatureSystem;
    }
}
