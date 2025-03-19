package com.privasia.procurehere.core.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PrResponseUpdateWrapper {
    @JsonProperty("MT_PRUpdate_PH_Res")
    private PrResponsePojo prResponsePojo;
}
