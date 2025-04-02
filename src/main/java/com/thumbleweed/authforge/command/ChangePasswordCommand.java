package com.thumbleweed.authforge.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.thumbleweed.authforge.AuthForge;
import com.thumbleweed.authforge.core.Guard;
import com.thumbleweed.authforge.core.Payload;
import com.thumbleweed.authforge.core.exception.AuthForgeException;
import com.thumbleweed.authforge.core.exception.ChangePasswordException;
import com.thumbleweed.authforge.event.Handler;
import com.thumbleweed.authforge.util.text.TextComponent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

public class ChangePasswordCommand implements AuthCommand {
    protected final Handler handler;
    protected final Guard guard;

    public ChangePasswordCommand(Handler handler, Guard guard) {
        this.handler = handler;
        this.guard = guard;
    }

    @Override
    public RequiredArgumentBuilder<CommandSourceStack, String> getParameters() {
        return Commands
                .argument("old", StringArgumentType.string())
                .then(
                        Commands
                                .argument("new", StringArgumentType.string())
                                .then(Commands.argument("confirmation", StringArgumentType.string()).executes(this))
                );
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder() {
        return Commands.literal("changepassword").then(this.getParameters());
    }

    @Override
    public int run(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        return execute(
                commandContext.getSource(),
                this.handler,
                this.guard,
                AuthCommand.toPayload(
                        commandContext.getSource().getPlayerOrException(),
                        StringArgumentType.getString(commandContext, "old"),
                        StringArgumentType.getString(commandContext, "new"),
                        StringArgumentType.getString(commandContext, "confirmation")
                )
        );
    }

    /**
     * @return 1 if something goes wrong, 0 otherwise.
     */
    public static int execute(CommandSourceStack source, Handler handler, Guard guard, Payload payload) {
        AuthForge.LOGGER.info("changepassword execute");
        try {
            Player player = source.getPlayerOrException();
            if (handler.isLogged(player)) {
                guard.updatePassword(payload);
                source.sendSuccess(() -> TextComponent.Create("authforge.changepassword.success"), true);
                AuthForge.LOGGER.info("changepassword success");
                return 0;
            } else {
                source.sendSuccess(() -> TextComponent.Create("authforge.welcome"), true);
            }
        } catch (ChangePasswordException e) {
            source.sendFailure(TextComponent.Create(e.getTranslationKey()));
        } catch (AuthForgeException e) {
            source.sendFailure(TextComponent.Create(e.getTranslationKey()));
            AuthForge.LOGGER.catching(e);
        } catch (CommandSyntaxException e) {
            AuthForge.LOGGER.catching(e);
        } catch (Exception e) {
            AuthForge.LOGGER.info("changepassword error: ", e);
        }
        AuthForge.LOGGER.info("changepassword failed");
        return 1;
    }
}
