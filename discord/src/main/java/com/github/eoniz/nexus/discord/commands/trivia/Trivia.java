package com.github.eoniz.nexus.discord.commands.trivia;

import com.github.eoniz.nexus.core.trivia.exceptions.*;
import com.github.eoniz.nexus.core.trivia.game.service.GameService;
import com.github.eoniz.nexus.discord.annotations.ButtonInteractionHandler;
import com.github.eoniz.nexus.discord.annotations.SlashCommand;
import com.github.eoniz.nexus.discord.annotations.SlashCommandOption;
import com.github.eoniz.nexus.discord.commands.AbstractSlashCommand;
import com.github.eoniz.nexus.model.player.Player;
import com.github.eoniz.nexus.model.question.Question;
import com.github.eoniz.nexus.model.room.Room;
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

    private final GameService gameService = new GameService();

    @Override
    public void handleCommand(
            @NotNull Member member,
            @NotNull TextChannel textChannel,
            @NotNull SlashCommandInteractionEvent event
    ) {
        Player player = getPlayer(event.getMember());

        OptionMapping optionQuestions = event.getOption("questions");
        int numberOfQuestions = 10;

        if (optionQuestions != null) {
            numberOfQuestions = optionQuestions.getAsInt();
        }

        Room room = gameService.createRoom(player, numberOfQuestions);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Trivia");
        embedBuilder.setColor(Color.ORANGE);
        embedBuilder.setDescription("La partie va bientôt commencer !");
        embedBuilder.addField(
                "Rejoindre la partie",
                "Pour rejoindre la partie, appuyez sur le bouton \"Rejoindre\"",
                false
        );
        embedBuilder.setFooter(String.format("%s - made with ♥ by @Eoniz", room.getRoomId()));

        event.replyEmbeds(embedBuilder.build())
                .addActionRow(
                        Button.primary(String.format("trivia|join|%s", room.getRoomId()), "Rejoindre"),
                        Button.success(String.format("trivia|start|%s", room.getRoomId()), "Démarrer")
                )
                .queue();
    }

    @ButtonInteractionHandler(action = "join")
    public void handleJoinInteraction(@NotNull ButtonInteractionEvent event, String roomId) {
        Player player = getPlayer(event.getMember());

        try {
            gameService.joinRoom(roomId, player);

            event.reply(event.getMember().getAsMention() + " a rejoint la partie !")
                    .queue();
        } catch (RoomDoesNotExistsException e) {
            event.reply("Cette partie n'existe plus :(")
                    .setEphemeral(true)
                    .queue();
        } catch (AlreadyJoinedException e) {
            event.reply("Vous êtes déjà inscrit dans la partie !")
                    .setEphemeral(true)
                    .queue();
        }
    }

    @ButtonInteractionHandler(action = "start")
    public void handleStartInteraction(@NotNull ButtonInteractionEvent event, String roomId) {
        Player player = getPlayer(event.getMember());

        try {
            Question question = gameService.startGame(roomId, player);
            printQuestion(event, question, roomId);
        } catch (RoomDoesNotExistsException e) {
            event.reply("Cette partie n'existe plus :(")
                    .setEphemeral(true)
                    .queue();
        } catch (GameAlreadyStartedException e) {
            event.reply("Cette partie a déjà commencé :(")
                    .setEphemeral(true)
                    .queue();
        } catch (GameAlreadyFinishedException e) {
            event.reply("Cette partie est finie :(")
                    .setEphemeral(true)
                    .queue();
        } catch (OnlyOwnerCanStartGameException e) {
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
        Player player = getPlayer(event.getMember());

        try {
            boolean allPlayerAnswered = gameService.answerToQuestion(roomId, player, questionId, answer);

            event.reply(event.getMember().getAsMention() + " a répondu à la question !").queue();
            if (!allPlayerAnswered) {
                return;
            }

            Question question = gameService.getCurrentQuestion(roomId);
            Map<String, String> playerAnswers = gameService.getPlayerAnswers(roomId);
            printAnswer(event, question, playerAnswers);
            Optional<Question> nextQuestion = gameService.nextQuestion(roomId);

            if (nextQuestion.isPresent()) {
                printQuestion(event, nextQuestion.get(), roomId);
                return;
            }

            Map<String, Integer> mapScores = gameService.getReadableMapScores(roomId);
            printScores(event, mapScores);

            gameService.destroy(roomId);
        } catch (RoomDoesNotExistsException e) {
            event.reply("Cette partie n'existe plus :(")
                    .setEphemeral(true)
                    .queue();
        } catch (GameNotStartedException e) {
            event.reply("Cette partie n'a pas encore commencé :(")
                    .setEphemeral(true)
                    .queue();
        } catch (GameAlreadyFinishedException e) {
            event.reply("Cette partie est finie :(")
                    .setEphemeral(true)
                    .queue();
        } catch (PlayerAlreadyAnsweredException e) {
            event.reply("Vous avez déjà répondu à la question :(")
                    .setEphemeral(true)
                    .queue();
        } catch (WrongQuestionAnsweredException e) {
            event.reply("Cette question n'est pas celle en cours :(")
                    .setEphemeral(true)
                    .queue();
        }
    }

    private Player getPlayer(@Nullable Member member) {
        if (member == null) {
            return Player.builder()
                    .effectiveName("?")
                    .id("?")
                    .build();
        }

        return Player.builder()
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
            Question question,
            Map<String, String> playerAnswers
    ) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("Trivia");
        embedBuilder.setColor(Color.GREEN);

        embedBuilder.setDescription(question.getQuestion());

        embedBuilder.addField(
                "Réponse",
                String.format("La bonne réponse était: **%s**", question.getAnswer()),
                false
        );

        embedBuilder.addField(
                "Le saviez tu ?",
                question.getAnecdote(),
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
            Question question,
            String roomId
    ) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("Trivia");
        embedBuilder.setColor(Color.ORANGE);
        embedBuilder.addField(
                question.getDifficulty().getLabel(),
                question.getQuestion(),
                false
        );

        AtomicInteger i = new AtomicInteger(0);

        List<String> mappedQuestions = Arrays.stream(question.getPropositions())
                .map(s -> getLetterFromIndex(i.getAndIncrement()) + s)
                .collect(Collectors.toList());

        embedBuilder.addField(
                "Réponses",
                String.join("\n", mappedQuestions),
                false
        );

        List<Button> row = Arrays.stream(question.getPropositions())
                .map(label -> Button.primary(
                        String.format(
                                "trivia|answer|%s|%s|%s",
                                roomId,
                                question.getId(),
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
