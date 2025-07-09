package com.example.console.domain.type;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;
import java.util.List;
@Data
@Accessors(chain = true)
public class TypeTreeVO {
    private String image;
    private BigInteger typeId;
    private String typeName;
    private List<TypeTreeVO> childrenList;
}
