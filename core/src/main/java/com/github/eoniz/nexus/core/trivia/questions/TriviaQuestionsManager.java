package com.github.eoniz.nexus.core.trivia.questions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.eoniz.nexus.model.trivia.question.TriviaQuestion;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class TriviaQuestionsManager {

    private static final List<TriviaQuestion> TRIVIA_QUESTIONS = new ArrayList<>();

    public static List<TriviaQuestion> getTRIVIA_QUESTIONS() {
        return getTRIVIA_QUESTIONS(TRIVIA_QUESTIONS.size());
    }

    public static List<TriviaQuestion> getTRIVIA_QUESTIONS(int limit) {
        List<TriviaQuestion> questionsCopy = new ArrayList<>(TRIVIA_QUESTIONS);
        Collections.shuffle(questionsCopy);
        return questionsCopy.subList(0, limit);
    }

    private static void enrichQuestionsFromFile(String fileName) {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream ioStream = TriviaQuestionsManager.class
                .getClassLoader()
                .getResourceAsStream(fileName);
        try {
            JsonNode jsonNode = objectMapper.readTree(ioStream);
            JsonNode categoryRootNode = jsonNode.get("quizz").get("fr");
            categoryRootNode
                    .fieldNames()
                    .forEachRemaining(categoryLabel -> {
                        JsonNode category = categoryRootNode.get(categoryLabel);
                        category.forEach(question -> {
                            List<String> propositions = new ArrayList<>();
                            question.get("propositions")
                                    .forEach(proposition -> propositions.add(proposition.asText()));

                            TriviaQuestion triviaQuestionObject = TriviaQuestion.builder()
                                    .id(question.get("id").asInt())
                                    .question(question.get("question").asText())
                                    .propositions(propositions.toArray(new String[]{}))
                                    .answer(question.get("réponse").asText())
                                    .anecdote(question.get("anecdote").asText())
                                    .difficulty(TriviaQuestion.Difficulty.of(categoryLabel))
                                    .build();

                            TRIVIA_QUESTIONS.add(triviaQuestionObject);
                        });
                        System.out.println(category);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        enrichQuestionsFromFile("questions/general/1.json");
        enrichQuestionsFromFile("questions/general/2.json");
        enrichQuestionsFromFile("questions/general/3.json");
        enrichQuestionsFromFile("questions/general/4.json");
        enrichQuestionsFromFile("questions/general/5.json");
    }
}
