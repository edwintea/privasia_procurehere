package com.privasia.procurehere.core.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PORequest {
    @JsonProperty("PO_Header")
    private SapPoToPhPoPojo poHeader;

    @JsonProperty("PO_Items")
    private List<SapPoToPhPoPojoItem> poItems;
}
