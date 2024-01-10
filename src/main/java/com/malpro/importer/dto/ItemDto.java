package com.malpro.importer.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by fahian on 03.12.22.
 */
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class ItemDto {
    @ToString.Include
    private String code;
    private String shortDescription;
    private String longDescription;
    private String globalTradeItemNumber;
    private String unit;
    private String priceQuantity;
    // private String manufacturerCode;
    // private String manufacturerName;
    @ToString.Include
    private String etimClass;
    @ToString.Include
    private String referenceFeatureSystem = "ETIM-8.0";
    private Map<String, String> featuresMap;
    // private CatalogProductOrderDetailsDto productOrderDetails;
}
