package com.example.app.domain.type;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
@Data
@Accessors(chain = true)
public class ChildrenListVO {
    private List<ChildrenVO> childrenList;
    private List<ChildreGameVO> gameList;
}
