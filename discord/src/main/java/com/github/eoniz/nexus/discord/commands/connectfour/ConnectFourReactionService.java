package com.github.eoniz.nexus.discord.commands.connectfour;

import com.github.eoniz.nexus.core.connectfour.service.ConnectFourGameService;
import com.github.eoniz.nexus.model.connectfour.player.ConnectFourPlayer;
import com.github.eoniz.nexus.model.connectfour.room.ConnectFourRoom;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.Optional;

@AllArgsConstructor
public class ConnectFourReactionService {

    private final ConnectFourGameService connectFourGameService = new ConnectFourGameService();

    public void handleReaction(MessageReactionAddEvent event, ConnectFourRoom connectFourRoom) {
        String emoji = event.getReaction().getReactionEmote().getEmoji();
        int x = getXFromEmoji(emoji);

        if (x == -1) {
            return;
        }

        if (!connectFourGameService.checkIfCorrectPlayer(connectFourRoom, event.getMember().getId())) {
            event.getReaction().removeReaction(event.getUser()).queue();
            return;
        }

        connectFourGameService.place(connectFourRoom, x);

        event.getChannel()
                .retrieveMessageById(event.getMessageId())
                .queue((message) -> {
                    EmbedBuilder embedBuilder = ConnectFourUtils.printBoard(connectFourRoom);
                    message.editMessageEmbeds(embedBuilder.build()).queue();
                });

        Optional<ConnectFourPlayer> winner = connectFourGameService.checkIfWin(connectFourRoom);
        if (winner.isPresent()) {
            event.getTextChannel().sendMessage(
                    winner.get().getAsMention() + " a gagné ! trop fort bg"
            ).queue();

            connectFourGameService.destroy(connectFourRoom.getMessageId());
        }

        event.getReaction().removeReaction(event.getUser()).queue();
    }

    private int getXFromEmoji(String emoji) {
        switch (emoji) {
            case "1️⃣":
                return 0;
            case "2️⃣":
                return 1;
            case "3️⃣":
                return 2;
            case "4️⃣":
                return 3;
            case "5️⃣":
                return 4;
            case "6️⃣":
                return 5;
            case "7️⃣":
                return 6;
            default:
                return -1;
        }
    }
}
