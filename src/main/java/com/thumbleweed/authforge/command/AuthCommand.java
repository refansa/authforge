package com.thumbleweed.authforge.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.thumbleweed.authforge.core.Payload;
import com.thumbleweed.authforge.core.PayloadImpl;
import com.thumbleweed.authforge.core.PlayerImpl;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.player.Player;

import java.util.Arrays;
import java.util.Objects;

public interface AuthCommand extends Command<CommandSourceStack> {
    RequiredArgumentBuilder<CommandSourceStack, String> getParameters();

    LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder();

    static Payload toPayload(Player entity, String... args) {
        String uuid = entity.getStringUUID();
        String username = entity.getDisplayName().getString();
        return new PayloadImpl(
                new PlayerImpl(username, uuid),
                Arrays.stream(args).filter(Objects::nonNull).toArray(String[]::new)
        );
    }
}
