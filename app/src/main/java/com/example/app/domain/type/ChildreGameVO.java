package com.example.app.domain.type;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;
@Data
@Accessors(chain = true)
public class ChildreGameVO {
        private BigInteger gameId;
        private String gameName;
        private String  image;
        private String typeName;
    }


