package com.github.eoniz.nexus.persistence.entity.connectfour.player;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectFourPlayerEntity {
    private String id;
    private String effectiveName;
    private String asMention;
}
