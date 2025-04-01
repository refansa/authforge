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
import com.thumbleweed.authforge.core.exception.RegisterException;
import com.thumbleweed.authforge.event.Handler;
import com.thumbleweed.authforge.util.text.ServerTranslationTextComponent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

public class RegisterCommand implements AuthCommand, Command<CommandSourceStack> {
    protected final Handler handler;
    protected final Guard guard;

    public RegisterCommand(Handler handler, Guard guard) {
        this.handler = handler;
        this.guard = guard;
    }

    @Override
    public RequiredArgumentBuilder<CommandSourceStack, String> getParameters() {
        return Commands
                .argument("password", StringArgumentType.string())
                .then(Commands.argument("confirmation", StringArgumentType.string()).executes(this));
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder() {
        return Commands.literal("register").then(this.getParameters());
    }

    @Override
    public int run(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        return execute(
                commandContext.getSource(),
                this.handler,
                this.guard,
                AuthCommand.toPayload(
                        commandContext.getSource().getPlayerOrException(),
                        StringArgumentType.getString(commandContext, "password"),
                        StringArgumentType.getString(commandContext, "confirmation")
                )
        );
    }

    /**
     * @return 1 if something goes wrong, 0 otherwise.
     */
    public static int execute(CommandSourceStack source, Handler handler, Guard guard, Payload payload) {
        AuthForge.LOGGER.info("register execute");
        try {
            Player player = source.getPlayerOrException();
            AuthForge.LOGGER.info("register player: {}", player);
            if (guard.register(payload) && !handler.isLogged(source.getPlayerOrException())) {
                handler.authorizePlayer(player);
                source.sendSuccess(() -> ServerTranslationTextComponent.CreateComponent("authforge.register.success"), true);
            }
            AuthForge.LOGGER.info("register success");
            return 0;
        } catch (RegisterException e) {
            source.sendFailure(ServerTranslationTextComponent.CreateComponent(e.getTranslationKey(), payload.getPlayer().getUsername()));
        } catch (AuthForgeException e) {
            source.sendFailure(ServerTranslationTextComponent.CreateComponent(e.getTranslationKey(), payload.getPlayer().getUsername()));
            AuthForge.LOGGER.catching(e);
        } catch (CommandSyntaxException e) {
            AuthForge.LOGGER.catching(e);
        } catch (Exception e) {
            AuthForge.LOGGER.info("register error: ", e);
        }
        AuthForge.LOGGER.info("register failed");
        return 1;
    }
}
