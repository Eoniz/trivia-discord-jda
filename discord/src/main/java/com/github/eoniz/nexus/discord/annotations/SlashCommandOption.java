package com.github.eoniz.nexus.discord.annotations;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = SlashCommandOptions.class)
public @interface SlashCommandOption {
    OptionType optionType();

    String name();

    String description();

    boolean required() default false;

    boolean autoComplete() default false;
}
