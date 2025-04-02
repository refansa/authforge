package com.thumbleweed.authforge.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.thumbleweed.authforge.AuthForge;
import com.thumbleweed.authforge.core.Guard;
import com.thumbleweed.authforge.core.Payload;
import com.thumbleweed.authforge.core.exception.AuthForgeException;
import com.thumbleweed.authforge.core.exception.LoginException;
import com.thumbleweed.authforge.event.Handler;
import com.thumbleweed.authforge.util.text.TextComponent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

public class LoginCommand implements AuthCommand, Command<CommandSourceStack> {
    protected final Handler handler;
    protected final Guard guard;

    public LoginCommand(Handler handler, Guard guard) {
        this.handler = handler;
        this.guard = guard;
    }

    @Override
    public RequiredArgumentBuilder<CommandSourceStack, String> getParameters() {
        return Commands.argument("password", StringArgumentType.string()).executes(this);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder() {
        return Commands.literal("login").then(this.getParameters());
    }

    @Override
    public int run(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        return execute(
                commandContext.getSource(),
                this.handler,
                this.guard,
                AuthCommand.toPayload(commandContext.getSource().getPlayerOrException(), StringArgumentType.getString(commandContext, "password"))
        );
    }

    public static int execute(CommandSourceStack source, Handler handler, Guard guard, Payload payload) {
        try {
            Player player = source.getPlayerOrException();
            if (!handler.isLogged(player) && guard.authenticate(payload)) {
                handler.authorizePlayer(player);
                source.sendSuccess(() -> TextComponent.Create("authforge.login.success"), true);
            }
            return 0;
        } catch (LoginException e) {
            source.sendFailure(TextComponent.Create(e.getTranslationKey(), payload.getPlayer().getUsername()));
        } catch (AuthForgeException e) {
            source.sendFailure(TextComponent.Create(e.getTranslationKey(), payload.getPlayer().getUsername()));
            AuthForge.LOGGER.catching(e);
        } catch (CommandSyntaxException e) {
            AuthForge.LOGGER.catching(e);
        }
        return 1;
    }
}
