package com.github.eoniz.nexus.discord.commands.sokoban;

import com.github.eoniz.nexus.model.sokoban.level.tiles.SokobanTile;
import com.github.eoniz.nexus.model.sokoban.room.SokobanRoom;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class SokobanUtils {
    public static MessageEmbed buildLevel(SokobanRoom sokobanRoom) {
        final StringBuilder stringBuilder = new StringBuilder();

        for (int y = 0; y < sokobanRoom.getSokobanLevel().getHeight(); y++) {
            for (int x = 0; x < sokobanRoom.getSokobanLevel().getWidth(); x++) {
                SokobanTile sokobanTile = sokobanRoom.getSokobanLevel().getTileAt(x, y);
                stringBuilder.append(sokobanTile.getEmoji());
            }
            stringBuilder.append("\n");
        }

        return new EmbedBuilder()
                .setTitle("Sokoban")
                .setDescription(stringBuilder.toString())
                .build();
    }
}
