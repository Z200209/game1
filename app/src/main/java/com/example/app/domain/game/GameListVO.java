package com.example.app.domain.game;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class GameListVO {
   List<GameVO> gameList;
   private String wp;
}
