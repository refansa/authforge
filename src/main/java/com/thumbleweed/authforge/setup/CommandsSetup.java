package com.thumbleweed.authforge.setup;

import com.mojang.brigadier.CommandDispatcher;
import com.thumbleweed.authforge.AuthForge;
import com.thumbleweed.authforge.command.ChangePasswordCommand;
import com.thumbleweed.authforge.command.LoggedCommand;
import com.thumbleweed.authforge.command.LoginCommand;
import com.thumbleweed.authforge.command.RegisterCommand;
import com.thumbleweed.authforge.config.Config;
import com.thumbleweed.authforge.core.Guard;
import com.thumbleweed.authforge.event.Handler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.Logger;

public class CommandsSetup {
    public static final Logger LOGGER = AuthForge.LOGGER;
    private final Guard guard;
    private final Handler handler;

    public CommandsSetup(Handler handler, Guard guard) {
        this.handler = handler;
        this.guard = guard;
    }

    @SubscribeEvent
    public void onCommandsRegister(RegisterCommandsEvent event) {
        LOGGER.info("Register commands");
        this.registerCommands(event);
    }

    private void registerCommands(RegisterCommandsEvent event) {
        if (this.guard != null) {
            registerLoginCommands(Config.enableLogin.get(), event.getDispatcher(), this.guard);
            registerRegisterCommand(Config.enableRegister.get(), event.getDispatcher(), this.guard);
            registerChangePasswordCommand(Config.enableChangePassword.get(), event.getDispatcher(), this.guard);
        } else {
            LOGGER.warn("{} is disabled because guard is NULL", AuthForge.ID);
        }
    }

    private void registerChangePasswordCommand(
            boolean enabled,
            CommandDispatcher<CommandSourceStack> commandDispatcher,
            Guard guard
    ) {
        if (enabled) {
            LOGGER.info("Registering /changepassword command");
            commandDispatcher.register(new ChangePasswordCommand(handler, guard).getCommandBuilder());
        }
    }

    private void registerRegisterCommand(
            boolean enabled,
            CommandDispatcher<CommandSourceStack> commandDispatcher,
            Guard guard
    ) {
        if (enabled) {
            LOGGER.info("Registering /register command");
            RegisterCommand command = new RegisterCommand(handler, guard);
            commandDispatcher.register(command.getCommandBuilder());
        }
    }

    private void registerLoginCommands(
            boolean enabled,
            CommandDispatcher<CommandSourceStack> commandDispatcher,
            Guard guard
    ) {
        if (enabled) {
            LOGGER.info("Registering /login command");
            LoginCommand command = new LoginCommand(handler, guard);
            commandDispatcher.register(command.getCommandBuilder());
            LOGGER.info("Registering /logged command");
            commandDispatcher.register(new LoggedCommand(handler).getCommandBuilder());
        }
    }
}
