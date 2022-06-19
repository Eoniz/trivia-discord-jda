package com.github.eoniz.nexus.discord.commands.trivia;

import com.github.eoniz.nexus.core.trivia.exceptions.*;
import com.github.eoniz.nexus.core.trivia.game.service.TriviaGameService;
import com.github.eoniz.nexus.discord.annotations.ButtonInteractionHandler;
import com.github.eoniz.nexus.discord.annotations.SlashCommand;
import com.github.eoniz.nexus.discord.annotations.SlashCommandOption;
import com.github.eoniz.nexus.discord.commands.AbstractSlashCommand;
import com.github.eoniz.nexus.model.trivia.player.TriviaPlayer;
import com.github.eoniz.nexus.model.trivia.question.TriviaQuestion;
import com.github.eoniz.nexus.model.trivia.room.TriviaRoom;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@SlashCommand(name = "trivia", help = "Démarrer une partie de Trivia")
@SlashCommandOption(optionType = OptionType.INTEGER, name = "questions", description = "number of questions")
public class Trivia extends AbstractSlashCommand {

    private final TriviaGameService triviaGameService = new TriviaGameService();

    @Override
    public void handleCommand(
            @NotNull Member member,
            @NotNull TextChannel textChannel,
            @NotNull SlashCommandInteractionEvent event
    ) {
        TriviaPlayer triviaPlayer = getPlayer(event.getMember());

        OptionMapping optionQuestions = event.getOption("questions");
        int numberOfQuestions = 10;

        if (optionQuestions != null) {
            numberOfQuestions = clamp(1, 20, optionQuestions.getAsInt());
        }

        TriviaRoom triviaRoom = triviaGameService.createRoom(triviaPlayer, numberOfQuestions);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Trivia");
        embedBuilder.setColor(Color.ORANGE);
        embedBuilder.setDescription("La partie va bientôt commencer !");
        embedBuilder.addField(
                "Rejoindre la partie",
                "Pour rejoindre la partie, appuyez sur le bouton \"Rejoindre\"",
                false
        );
        embedBuilder.setFooter(String.format("%s - made with ♥ by @Eoniz", triviaRoom.getRoomId()));

        event.replyEmbeds(embedBuilder.build())
                .addActionRow(
                        Button.primary(String.format("trivia|join|%s", triviaRoom.getRoomId()), "Rejoindre"),
                        Button.success(String.format("trivia|start|%s", triviaRoom.getRoomId()), "Démarrer")
                )
                .queue();
    }

    private int clamp(int min, int max, int value) {
        if (value < min) {
            return min;
        }

        if (value > max) {
            return max;
        }

        return value;
    }

    @ButtonInteractionHandler(action = "join")
    public void handleJoinInteraction(@NotNull ButtonInteractionEvent event, String roomId) {
        TriviaPlayer triviaPlayer = getPlayer(event.getMember());

        try {
            triviaGameService.joinRoom(roomId, triviaPlayer);

            event.reply(event.getMember().getAsMention() + " a rejoint la partie !")
                    .queue();
        } catch (TriviaRoomDoesNotExistsException e) {
            event.reply("Cette partie n'existe plus :(")
                    .setEphemeral(true)
                    .queue();
        } catch (TriviaAlreadyJoinedException e) {
            event.reply("Vous êtes déjà inscrit dans la partie !")
                    .setEphemeral(true)
                    .queue();
        }
    }

    @ButtonInteractionHandler(action = "start")
    public void handleStartInteraction(@NotNull ButtonInteractionEvent event, String roomId) {
        TriviaPlayer triviaPlayer = getPlayer(event.getMember());

        try {
            TriviaQuestion triviaQuestion = triviaGameService.startGame(roomId, triviaPlayer);
            printQuestion(event, triviaQuestion, roomId);
        } catch (TriviaRoomDoesNotExistsException e) {
            event.reply("Cette partie n'existe plus :(")
                    .setEphemeral(true)
                    .queue();
        } catch (TriviaGameAlreadyStartedException e) {
            event.reply("Cette partie a déjà commencé :(")
                    .setEphemeral(true)
                    .queue();
        } catch (TriviaGameAlreadyFinishedException e) {
            event.reply("Cette partie est finie :(")
                    .setEphemeral(true)
                    .queue();
        } catch (TriviaOnlyOwnerCanStartGameException e) {
            event.reply("Seul le créateur de la room peut démarrer :(")
                    .setEphemeral(true)
                    .queue();
        }
    }

    @ButtonInteractionHandler(action = "answer")
    public void handleAnswerInteraction(
            @NotNull ButtonInteractionEvent event,
            String roomId,
            String questionId,
            String answer
    ) {
        TriviaPlayer triviaPlayer = getPlayer(event.getMember());

        try {
            boolean allPlayerAnswered = triviaGameService.answerToQuestion(roomId, triviaPlayer, questionId, answer);

            event.reply(event.getMember().getAsMention() + " a répondu à la question !").queue();
            if (!allPlayerAnswered) {
                return;
            }

            TriviaQuestion triviaQuestion = triviaGameService.getCurrentQuestion(roomId);
            Map<String, String> playerAnswers = triviaGameService.getPlayerAnswers(roomId);
            printAnswer(event, triviaQuestion, playerAnswers);
            Optional<TriviaQuestion> nextQuestion = triviaGameService.nextQuestion(roomId);

            if (nextQuestion.isPresent()) {
                printQuestion(event, nextQuestion.get(), roomId);
                return;
            }

            Map<String, Integer> mapScores = triviaGameService.getReadableMapScores(roomId);
            printScores(event, mapScores);

            triviaGameService.destroy(roomId);
        } catch (TriviaRoomDoesNotExistsException e) {
            event.reply("Cette partie n'existe plus :(")
                    .setEphemeral(true)
                    .queue();
        } catch (TriviaGameNotStartedException e) {
            event.reply("Cette partie n'a pas encore commencé :(")
                    .setEphemeral(true)
                    .queue();
        } catch (TriviaGameAlreadyFinishedException e) {
            event.reply("Cette partie est finie :(")
                    .setEphemeral(true)
                    .queue();
        } catch (TriviaPlayerAlreadyAnsweredException e) {
            event.reply("Vous avez déjà répondu à la question :(")
                    .setEphemeral(true)
                    .queue();
        } catch (TriviaWrongQuestionAnsweredException e) {
            event.reply("Cette question n'est pas celle en cours :(")
                    .setEphemeral(true)
                    .queue();
        }
    }

    private TriviaPlayer getPlayer(@Nullable Member member) {
        if (member == null) {
            return TriviaPlayer.builder()
                    .effectiveName("?")
                    .id("?")
                    .build();
        }

        return TriviaPlayer.builder()
                .effectiveName(member.getEffectiveName())
                .id(member.getId())
                .build();
    }

    private void printScores(GenericComponentInteractionCreateEvent event, Map<String, Integer> playerScores) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("Trivia");
        embedBuilder.setColor(Color.YELLOW);

        embedBuilder.setDescription("Partie terminée, vous pouvez retrouver les scores ci dessous !");

        List<Pair<String, Integer>> scores = new ArrayList<>();

        playerScores.forEach((playerName, score) -> {
            scores.add(new ImmutablePair<>(playerName, score));
        });

        List<String> sortedScores = scores.stream()
                .sorted(Comparator.comparingInt(Pair::getRight))
                .map((pair) -> String.format("- **%s**: %s", pair.getLeft(), pair.getRight()))
                .collect(Collectors.toList());
        Collections.reverse(sortedScores);

        String stringifiedScores = String.join("\n", sortedScores);

        embedBuilder.addField(
                "Scores",
                stringifiedScores,
                false
        );

        event.getTextChannel()
                .sendMessageEmbeds(embedBuilder.build())
                .queue();
    }

    private void printAnswer(
            GenericComponentInteractionCreateEvent event,
            TriviaQuestion triviaQuestion,
            Map<String, String> playerAnswers
    ) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("Trivia");
        embedBuilder.setColor(Color.GREEN);

        embedBuilder.setDescription(triviaQuestion.getQuestion());

        embedBuilder.addField(
                "Réponse",
                String.format("La bonne réponse était: **%s**", triviaQuestion.getAnswer()),
                false
        );

        embedBuilder.addField(
                "Le saviez tu ?",
                triviaQuestion.getAnecdote(),
                false
        );

        List<String> answersStringified = new ArrayList<>();
        playerAnswers.forEach((s, s2) -> {
            answersStringified.add("- " + s + ": " + s2);
        });

        embedBuilder.addField(
                "Réponses des joueurs",
                String.join("\n", answersStringified),
                false
        );

        event.getTextChannel()
                .sendMessageEmbeds(embedBuilder.build())
                .queue();
    }

    private void printQuestion(
            GenericComponentInteractionCreateEvent event,
            TriviaQuestion triviaQuestion,
            String roomId
    ) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("Trivia");
        embedBuilder.setColor(Color.ORANGE);
        embedBuilder.addField(
                triviaQuestion.getDifficulty().getLabel(),
                triviaQuestion.getQuestion(),
                false
        );

        AtomicInteger i = new AtomicInteger(0);

        List<String> mappedQuestions = Arrays.stream(triviaQuestion.getPropositions())
                .map(s -> getLetterFromIndex(i.getAndIncrement()) + s)
                .collect(Collectors.toList());

        embedBuilder.addField(
                "Réponses",
                String.join("\n", mappedQuestions),
                false
        );

        List<Button> row = Arrays.stream(triviaQuestion.getPropositions())
                .map(label -> Button.primary(
                        String.format(
                                "trivia|answer|%s|%s|%s",
                                roomId,
                                triviaQuestion.getId(),
                                label
                        ),
                        label
                ))
                .collect(Collectors.toList());

        event.getTextChannel()
                .sendMessageEmbeds(embedBuilder.build())
                .flatMap(c -> {
                    return c.editMessageComponents(ActionRow.of(row));
                })
                .queue();
    }

    private String getLetterFromIndex(int idx) {
        if (idx == 0) {
            return "A) ";
        }
        if (idx == 1) {
            return "B) ";
        }
        if (idx == 2) {
            return "C) ";
        }
        if (idx == 3) {
            return "D) ";
        }
        return "";
    }
}
