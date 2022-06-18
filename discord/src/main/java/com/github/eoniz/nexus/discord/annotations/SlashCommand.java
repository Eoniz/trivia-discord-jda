package com.github.eoniz.nexus.discord.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SlashCommand {
    String name();

    String help() default "";
}
