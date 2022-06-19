package com.github.eoniz.nexus.discord.commands.connectfour;

import com.github.eoniz.nexus.model.connectfour.grid.ConnectFourGrid;
import com.github.eoniz.nexus.model.connectfour.room.ConnectFourRoom;
import net.dv8tion.jda.api.EmbedBuilder;

public class ConnectFourUtils {

    public static EmbedBuilder printBoard(ConnectFourRoom room) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(
                String.format(
                        "%s ⚡ %s",
                        room.getFirstPlayer().getEffectiveName(),
                        room.getSecondPlayer().getEffectiveName()
                )
        );

        StringBuilder generatedBoard = new StringBuilder();
        String firstPlayerBoard = Long.toBinaryString(room.getConnectFourGrid().getBoardFirstPlayer());
        String secondPlayerBoard = Long.toBinaryString(room.getConnectFourGrid().getBoardSecondPlayer());

        for (int y = ConnectFourGrid.HEIGHT - 2; y >= 0; y--) {
            for (int x = ConnectFourGrid.WIDTH; x > 0; x--) {
                int index = ((x * ConnectFourGrid.HEIGHT) - 1 - y) + (2 * ConnectFourGrid.WIDTH);
                String str = ":black_circle:";
                if (firstPlayerBoard.charAt(index) == '1') {
                    str = ":red_circle:";
                } else if (secondPlayerBoard.charAt(index) == '1') {
                    str = ":yellow_circle:";
                }

                generatedBoard.append(str).append(" ");
            }

            generatedBoard.append("\n");
        }

        generatedBoard.append("\n1️⃣ 2️⃣ 3️⃣ 4️⃣ 5️⃣ 6️⃣ 7️⃣");

        embedBuilder.addField("Tour", room.getActualPlayer().getAsMention(), false);
        embedBuilder.addField("Board", generatedBoard.toString(), false);

        return embedBuilder;
    }

}
