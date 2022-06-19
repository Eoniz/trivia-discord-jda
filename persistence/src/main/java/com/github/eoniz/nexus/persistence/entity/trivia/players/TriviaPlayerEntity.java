package com.github.eoniz.nexus.persistence.entity.trivia.players;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriviaPlayerEntity {
    private String id;
    private String effectiveName;
}
