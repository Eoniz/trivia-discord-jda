package com.github.eoniz.nexus.discord.commands;

import com.github.eoniz.nexus.discord.annotations.AnnotationsHelper;
import com.github.eoniz.nexus.discord.annotations.ButtonInteractionHandler;
import com.github.eoniz.nexus.discord.annotations.SlashCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.reflections.scanners.Scanners.TypesAnnotated;

@Getter
@AllArgsConstructor
public class CommandsManager {
    private static final Map<String, AbstractSlashCommand> slashCommands = new HashMap();

    public static void handleCommand(String name, SlashCommandInteractionEvent event) {
        AbstractSlashCommand abstractSlashCommand = slashCommands.get(name);
        if (abstractSlashCommand == null) {
            return;
        }

        abstractSlashCommand.handleCommand(
                event.getMember(),
                event.getTextChannel(),
                event
        );
    }

    public static void handleButtonInteraction(ButtonInteractionEvent event, String name, String[] args) {
        AbstractSlashCommand abstractSlashCommand = slashCommands.get(name);
        if (abstractSlashCommand == null) {
            return;
        }

        if (args.length == 0) {
            return;
        }

        List<Method> methods = AnnotationsHelper.getMethodsAnnotatedWith(
                abstractSlashCommand.getClass(),
                ButtonInteractionHandler.class
        );

        for (Method method : methods) {
            ButtonInteractionHandler annotation = method.getAnnotation(ButtonInteractionHandler.class);
            if (annotation.action().equals(args[0])) {
                try {
                    List<Object> methodArgs = new ArrayList<>();
                    String[] givenArgs = Arrays.copyOfRange(args, 1, args.length);
                    methodArgs.add(event);
                    methodArgs.addAll(List.of(givenArgs));

                    method.invoke(abstractSlashCommand, methodArgs.toArray());
                    break;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static Map<String, AbstractSlashCommand> getSlashCommands() {
        return slashCommands;
    }

    static {
        Reflections reflections = new Reflections("com.github.eoniz.nexus.discord.commands");
        Set<Class<?>> classes = reflections.get(TypesAnnotated.with(SlashCommand.class).asClass());
        classes.forEach((klass) -> {
            SlashCommand annotation = klass.getAnnotation(SlashCommand.class);
            try {
                AbstractSlashCommand abstractSlashCommand = (
                        (AbstractSlashCommand) klass.getConstructor().newInstance()
                );
                slashCommands.put(annotation.name(), abstractSlashCommand);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
