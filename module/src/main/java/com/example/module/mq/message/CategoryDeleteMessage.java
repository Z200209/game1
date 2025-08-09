package com.example.module.mq.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;
import java.util.List;

@Data
@Accessors(chain = true)
public class CategoryDeleteMessage {
    
    @JsonProperty("categoryId")
    private BigInteger categoryId;
    
    @JsonProperty("categoryName")
    private String categoryName;
    
    @JsonProperty("gameIds")
    private List<BigInteger> gameIds;
    
    @JsonProperty("timestamp")
    private Integer timestamp;
    
    @JsonProperty("operator")
    private BigInteger operator;
}