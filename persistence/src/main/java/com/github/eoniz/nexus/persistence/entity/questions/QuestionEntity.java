package com.github.eoniz.nexus.persistence.entity.questions;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Collection;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionEntity {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("question")
    private String question;
    @JsonProperty("propositions")
    private Collection<String> propositions;
    @JsonProperty("answer")
    private String answer;
    @JsonProperty("anecdote")
    private String anecdote;
    @JsonProperty("difficulty")
    private String difficulty;
}
