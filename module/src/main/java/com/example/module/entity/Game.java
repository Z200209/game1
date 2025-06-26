package com.example.module.entity;

import java.math.BigInteger;

import lombok.Data;
import lombok.experimental.Accessors;
@Data
@Accessors(chain = true)
public class Game {
    private BigInteger id;
    private String gameName;
    private Float price;
    private String gameIntroduction;
    private String gameDate;
    private String gamePublisher;
    private String images;
    private Integer createTime;
    private Integer updateTime;
    private Integer isDeleted;
    private BigInteger typeId;
}
