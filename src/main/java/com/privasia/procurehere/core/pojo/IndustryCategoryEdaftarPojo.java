package com.privasia.procurehere.core.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.privasia.procurehere.core.enums.Status;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Data
public class IndustryCategoryEdaftarPojo {

    @JsonProperty(value = "category_code")
    private String categoryCode;

    // it should be ignored everytime as per the documents
    @JsonProperty(value = "category_name_bm")
    private String categoryNameBm;

    @JsonProperty(value = "category_name_bi")
    private String categoryNameBi;

    @Enumerated(EnumType.STRING)
    @JsonProperty(value = "category_status")
    private Status status;

    @JsonProperty(value = "subcategory_desc_bi")
    private String subcategoryDescBi;

    @JsonProperty(value = "activity_desc_bi")
    private String activityDescBi;
}
