package com.github.eoniz.nexus.persistence.entity.sokoban.player;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SokobanPlayerEntity {
    private String id;
    private String effectiveName;
    private String asMention;
}
