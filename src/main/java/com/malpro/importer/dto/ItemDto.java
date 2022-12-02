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
    private String code;
    private String shortDescription;
    private String longDescription;
    private String globalTradeItemNumber;
    private String manufacturerCode;
    private String manufacturerName;
    private String etimClass;
    private String referenceFeatureSystem;
    private Map<String, String> featuresMap;
    // private CatalogProductOrderDetailsDto productOrderDetails;

    @Override
    public String toString() {
        return "etimClass: " + etimClass + ", referenceFeatureSystem: " + referenceFeatureSystem;
    }
}
