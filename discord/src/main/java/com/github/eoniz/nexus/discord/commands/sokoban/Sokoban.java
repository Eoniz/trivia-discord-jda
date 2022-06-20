package com.github.eoniz.nexus.discord.commands.sokoban;

import com.github.eoniz.nexus.core.sokoban.service.SokobanGameService;
import com.github.eoniz.nexus.core.sokoban.service.SokobanLevelService;
import com.github.eoniz.nexus.discord.annotations.ButtonInteractionHandler;
import com.github.eoniz.nexus.discord.annotations.SelectInteractionHandler;
import com.github.eoniz.nexus.discord.annotations.SlashCommand;
import com.github.eoniz.nexus.discord.commands.AbstractSlashCommand;
import com.github.eoniz.nexus.model.sokoban.level.SokobanLevel;
import com.github.eoniz.nexus.model.sokoban.level.tiles.SokobanTile;
import com.github.eoniz.nexus.model.sokoban.player.SokobanPlayer;
import com.github.eoniz.nexus.model.sokoban.room.SokobanRoom;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl;

import java.util.Optional;

@AllArgsConstructor
@SlashCommand(name = "sokoban", help = "Démarre une partie de sokoban")
public class Sokoban extends AbstractSlashCommand {

    private final SokobanGameService sokobanGameService = new SokobanGameService();
    private final SokobanLevelService sokobanLevelService = new SokobanLevelService();

    @Override
    public void handleCommand(Member member, TextChannel textChannel, SlashCommandInteractionEvent event) {
        event.reply("Veuillez séléctionner le niveau")
                .addActionRow(
                        SelectMenu.create("sokoban|level_select")
                                .addOption("Level 1", "level-1")
                                .build()
                )
                .addActionRow(
                        Button.success("sokoban|start_game", "Démarrer le jeu")
                )
                .queue();
    }

    @SelectInteractionHandler(action = "level_select")
    public void handleLevelSelect(SelectMenuInteractionEvent event) {
        System.out.println(event.getInteraction().getSelectedOptions());
        event.reply("ok").setEphemeral(true).queue();
    }

    @ButtonInteractionHandler(action = "start_game")
    public void handleStartGame(ButtonInteractionEvent buttonInteractionEvent) {
        Optional<ItemComponent> selectComponent = buttonInteractionEvent.getMessage().getActionRows().stream()
                .map(c -> {
                    if (c != null) {
                        return c.getComponents();
                    }

                    return null;
                })
                .filter(c -> c != null && c.size() == 1 && c.get(0) instanceof SelectMenu)
                .map(c -> c.get(0))
                .findFirst();

        if (selectComponent.isEmpty()) {
            return;
        }

        SelectMenuImpl selectMenu = (SelectMenuImpl) selectComponent.get();

        if (selectMenu.getOptions().size() != 1) {
            buttonInteractionEvent.reply("Choisissez un niveau !").queue();
            return;
        }

        SelectOption selectedOption = selectMenu.getOptions().get(0);
        Optional<SokobanLevel> sokobanLevel = (
                sokobanLevelService.getSokobanLevelByLabel(selectMenu.getOptions().get(0).getValue())
        );

        if (sokobanLevel.isEmpty()) {
            buttonInteractionEvent
                    .reply("Ce niveau n'existe pas o.o , ce n'est pas censé arriver ça !")
                    .queue();
            return;
        }

        SokobanPlayer sokobanPlayer = SokobanPlayer.builder()
                .id(buttonInteractionEvent.getMember().getId())
                .effectiveName(buttonInteractionEvent.getMember().getEffectiveName())
                .asMention(buttonInteractionEvent.getMember().getAsMention())
                .build();

        SokobanRoom sokobanRoom = sokobanGameService.createRoom(sokobanPlayer);
        sokobanRoom.setRootMessageId(buttonInteractionEvent.getMessageId());
        sokobanRoom.setSokobanLevel(sokobanLevel.get());

        buttonInteractionEvent.reply("La partie va commencer !").queue();

        MessageEmbed messageEmbed = SokobanUtils.buildLevel(sokobanRoom);

        buttonInteractionEvent.getTextChannel()
                .sendMessageEmbeds(messageEmbed)
                .queue((message) -> {
                    sokobanRoom.setGameMessageId(message.getId());
                    sokobanGameService.save(sokobanRoom);

                    message.addReaction("⬅️").queue();
                    message.addReaction("⬆️").queue();
                    message.addReaction("⬇️").queue();
                    message.addReaction("➡️").queue();
                });
    }

}
