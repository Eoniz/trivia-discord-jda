package com.github.eoniz.nexus.discord.commands.connectfour;

import com.github.eoniz.nexus.core.connectfour.service.ConnectFourGameService;
import com.github.eoniz.nexus.discord.annotations.SlashCommand;
import com.github.eoniz.nexus.discord.annotations.SlashCommandOption;
import com.github.eoniz.nexus.discord.commands.AbstractSlashCommand;
import com.github.eoniz.nexus.model.connectfour.player.ConnectFourPlayer;
import com.github.eoniz.nexus.model.connectfour.room.ConnectFourRoom;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

@SlashCommand(name = "connectfour", help = "Démarre une partie de puissance 4")
@SlashCommandOption(name = "user", optionType = OptionType.USER, required = true, description = "Joueur à affronter")
public class ConnectFour extends AbstractSlashCommand {

    private final ConnectFourGameService connectFourGameService = new ConnectFourGameService();

    @Override
    public void handleCommand(Member member, TextChannel textChannel, SlashCommandInteractionEvent event) {
        OptionMapping optUser = event.getOption("user");
        if (optUser == null) {
            event.reply("Erreur interne").queue();
            return;
        }

        User oponent = optUser.getAsUser();

        ConnectFourPlayer firstPlayer = ConnectFourPlayer.builder()
                .id(member.getId())
                .effectiveName(member.getEffectiveName())
                .asMention(member.getAsMention())
                .build();

        ConnectFourPlayer secondPlayer = ConnectFourPlayer.builder()
                .id(oponent.getId())
                .effectiveName(oponent.getName())
                .asMention(oponent.getAsMention())
                .build();

        ConnectFourRoom connectFourRoom = connectFourGameService.createRoom(firstPlayer, secondPlayer);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(
                String.format("%s ⚡ %s", member.getEffectiveName(), oponent.getName())
        );

        StringBuilder generatedBoard = new StringBuilder();
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 7; x++) {
                generatedBoard.append(":black_circle: ");
            }
            generatedBoard.append("\n");
        }
        generatedBoard.append("\n1️⃣ 2️⃣ 3️⃣ 4️⃣ 5️⃣ 6️⃣ 7️⃣");

        embedBuilder.addField("Tour", connectFourRoom.getActualPlayer().getAsMention(), false);
        embedBuilder.addField("Board", generatedBoard.toString(), false);

        event.reply(
                String.format(
                        "La partie va commencer entre %s et %s !",
                        firstPlayer.getAsMention(),
                        secondPlayer.getAsMention()
                )
        ).setEphemeral(true).queue();

        textChannel.sendMessageEmbeds(embedBuilder.build())
                .queue(embed -> {
                    connectFourRoom.setMessageId(embed.getId());
                    connectFourGameService.save(connectFourRoom);

                    embed.addReaction("1️⃣").queue();
                    embed.addReaction("2️⃣").queue();
                    embed.addReaction("3️⃣").queue();
                    embed.addReaction("4️⃣").queue();
                    embed.addReaction("5️⃣").queue();
                    embed.addReaction("6️⃣").queue();
                    embed.addReaction("7️⃣").queue();
                });
    }
}
