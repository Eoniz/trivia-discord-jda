package com.github.eoniz.nexus.persistence.entity.questions;

import com.github.eoniz.nexus.model.question.Question;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class QuestionEntityMapper {
    public QuestionEntity of(Question question) {
        return QuestionEntity.builder()
                .id(question.getId())
                .question(question.getQuestion())
                .propositions(List.of(question.getPropositions()))
                .answer(question.getAnswer())
                .anecdote(question.getAnecdote())
                .difficulty(question.getDifficulty().getLabel())
                .build();
    }

    public Question of(QuestionEntity questionEntity) {
        return Question.builder()
                .id(questionEntity.getId())
                .question(questionEntity.getQuestion())
                .propositions(questionEntity.getPropositions().toArray(String[]::new))
                .answer(questionEntity.getAnswer())
                .anecdote(questionEntity.getAnecdote())
                .difficulty(Question.Difficulty.of(questionEntity.getDifficulty()))
                .build();
    }
}
