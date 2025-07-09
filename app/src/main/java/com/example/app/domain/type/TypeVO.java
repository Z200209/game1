package com.example.app.domain.type;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;
import java.util.List;

@Data
@Accessors(chain = true)
public class TypeVO {
    private BigInteger typeId;
    private String typeName;
    private String image;
    private List<ChildrenVO> childrenList;
}
