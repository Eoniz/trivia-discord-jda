package com.github.eoniz.nexus.model.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Question {
    private final Integer id;
    private final String question;
    private final String[] propositions;
    private final String answer;
    private final String anecdote;
    private final Difficulty difficulty;

    @Getter
    @AllArgsConstructor
    public enum Difficulty {
        EASY("Facile"),
        MEDIUM("Modéré"),
        HARD("Dur"),
        UNKNOWN("Inconnu");

        private final String label;

        public static Difficulty of(String str) {
            for (Difficulty difficulty : Difficulty.values()) {
                if (difficulty.getLabel().equals(str)) {
                    return difficulty;
                }
            }

            return Difficulty.UNKNOWN;
        }
    }
}
