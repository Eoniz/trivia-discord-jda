package com.github.eoniz.nexus.core.trivia.questions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.eoniz.nexus.model.question.Question;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class QuestionsManager {

    private static final List<Question> questions = new ArrayList<>();

    public static List<Question> getQuestions() {
        return getQuestions(questions.size());
    }

    public static List<Question> getQuestions(int limit) {
        List<Question> questionsCopy = new ArrayList<>(questions);
        Collections.shuffle(questionsCopy);
        return questionsCopy.subList(0, limit);
    }

    private static void enrichQuestionsFromFile(String fileName) {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream ioStream = QuestionsManager.class
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

                            Question questionObject = Question.builder()
                                    .id(question.get("id").asInt())
                                    .question(question.get("question").asText())
                                    .propositions(propositions.toArray(new String[]{}))
                                    .answer(question.get("réponse").asText())
                                    .anecdote(question.get("anecdote").asText())
                                    .difficulty(Question.Difficulty.of(categoryLabel))
                                    .build();

                            questions.add(questionObject);
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
