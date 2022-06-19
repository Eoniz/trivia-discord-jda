package com.github.eoniz.nexus.persistence.entity.trivia.questions;

import com.github.eoniz.nexus.model.trivia.question.TriviaQuestion;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class TriviaQuestionEntityMapper {
    public TriviaQuestionEntity of(TriviaQuestion triviaQuestion) {
        return TriviaQuestionEntity.builder()
                .id(triviaQuestion.getId())
                .question(triviaQuestion.getQuestion())
                .propositions(List.of(triviaQuestion.getPropositions()))
                .answer(triviaQuestion.getAnswer())
                .anecdote(triviaQuestion.getAnecdote())
                .difficulty(triviaQuestion.getDifficulty().getLabel())
                .build();
    }

    public TriviaQuestion of(TriviaQuestionEntity triviaQuestionEntity) {
        return TriviaQuestion.builder()
                .id(triviaQuestionEntity.getId())
                .question(triviaQuestionEntity.getQuestion())
                .propositions(triviaQuestionEntity.getPropositions().toArray(String[]::new))
                .answer(triviaQuestionEntity.getAnswer())
                .anecdote(triviaQuestionEntity.getAnecdote())
                .difficulty(TriviaQuestion.Difficulty.of(triviaQuestionEntity.getDifficulty()))
                .build();
    }
}
