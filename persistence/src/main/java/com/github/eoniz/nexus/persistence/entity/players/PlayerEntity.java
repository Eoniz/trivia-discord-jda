package com.github.eoniz.nexus.persistence.entity.players;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerEntity {
    private String id;
    private String effectiveName;
}
