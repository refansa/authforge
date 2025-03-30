package com.thumbleweed.authforge.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.thumbleweed.authforge.event.Handler;
import com.thumbleweed.authforge.util.text.ServerTranslationTextComponent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class LoggedCommand implements AuthCommand {
    private final Handler handler;

    public LoggedCommand(Handler handler) {
        this.handler = handler;
    }

    @Override
    public RequiredArgumentBuilder<CommandSourceStack, String> getParameters() {
        return null;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder() {
        return Commands.literal("logged").executes(this);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        return execute(commandContext.getSource(), commandContext.getSource().getPlayerOrException(), this.handler);
    }

    public static int execute(CommandSourceStack source, ServerPlayer player, Handler handler) {
        boolean logged = handler.isLogged(player);
        String translationKey = "authforge.logged." + (logged ? "yes" : "no");
        source.sendSuccess(() -> ServerTranslationTextComponent.CreateComponent(translationKey), false);
        return 0;
    }
}
