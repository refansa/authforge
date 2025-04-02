package com.thumbleweed.authforge.event;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.thumbleweed.authforge.config.Config;
import com.thumbleweed.authforge.core.PlayerDescriptor;
import com.thumbleweed.authforge.util.text.TextComponent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Handler {
    /**
     * A scheduler to keep track of the player's timing before getting kicked.
     */
    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
    /**
     * Descriptors of the player current position, removed when the player successfully logged in.
     */
    private final Map<Player, PlayerDescriptor> descriptors = new HashMap<>();
    /**
     * Stored value of the player logging status.
     */
    private final Map<Player, Boolean> logged = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        Vec3 position = player.position();

        PlayerDescriptor descriptor = new PlayerDescriptor(position.x, position.y, position.z);
        descriptors.put(player, descriptor);

        // initializing a timer for kicking player if they haven't logged in.
        scheduler.schedule(
                () -> {
                    if (descriptors.containsKey(player)) {
                        descriptors.remove(player);
                        logged.remove(player);
                        ((ServerPlayer) player).connection.send(new ClientboundDisconnectPacket(wakeUp()));
                    }
                },
                Config.delay.get(),
                TimeUnit.SECONDS
        );
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();

        logged.remove(player);
    }

    @SubscribeEvent
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getEntity();

        if (descriptors.containsKey(player) && event.getSide() == LogicalSide.SERVER) {
            event.setCanceled(true);
            teleportTo(player, descriptors.get(player));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerEvent(PlayerEvent event) {
        Player player = event.getEntity();

        if (descriptors.containsKey(player) && event.isCancelable()) {
            event.setCanceled(true);
            teleportTo(player, descriptors.get(player));
            sayWelcome(player);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onCommand(CommandEvent event) {
        CommandSourceStack source = event.getParseResults().getContext().getSource();
        try {
            Player player = source.getPlayerOrException();

            List<? extends String> whitelistedCommands = Config.whitelistedCommands.get();

            // Get the name of the command. i.e. after the character '/'.
            String name = event.getParseResults().getContext().getNodes().get(0).getNode().getName();

            boolean isCommandAllowed = whitelistedCommands.contains(name);
            if (descriptors.containsKey(player) && !isCommandAllowed && event.isCancelable()) {
                event.setCanceled(true);
                source.sendSuccess(
                        () -> TextComponent.Create("authforge.welcome"),
                        false
                );
            }
        } catch (CommandSyntaxException e) {
            source.sendSuccess(
                    () -> Component.literal(e.getMessage()), false
            );
        }
    }

    @SubscribeEvent
    public void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        Player player = event.player;

        if (descriptors.containsKey(player) && event.side == LogicalSide.SERVER) {
            teleportTo(player, descriptors.get(player));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatEvent(ServerChatEvent event) {
        Player player = event.getPlayer();

        if (descriptors.containsKey(player)) {
            event.setCanceled(true);
            this.sayWelcome(player);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTossEvent(ItemTossEvent event) {
        Player player = event.getPlayer();

        if (event.isCancelable() && descriptors.containsKey(player)) {
            event.setCanceled(true);
            player.getInventory().add(event.getEntity().getItem());
            sayWelcome(player);
        }
    }

    private void handleLivingEvents(LivingEvent event, Entity entity) {
        if (entity instanceof Player player && event.isCancelable() && descriptors.containsKey(player)) {
            event.setCanceled(true);
            PlayerDescriptor desc = descriptors.get(player);
            player.teleportTo(desc.getX(), desc.getY(), desc.getZ());
            sayWelcome(player);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingAttackEvent(LivingAttackEvent event) {
        handleLivingEvents(event, event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingDeathEvent(LivingDeathEvent event) {
        handleLivingEvents(event, event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingEntityUseItemEvent(LivingEntityUseItemEvent event) {
        handleLivingEvents(event, event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingHealEvent(LivingHealEvent event) {
        handleLivingEvents(event, event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingHurtEvent(LivingHurtEvent event) {
        handleLivingEvents(event, event.getEntity());
    }

    public void authorizePlayer(Player player) {
        logged.put(player, true);
        descriptors.remove(player);
    }

    public boolean isLogged(Player player) {
        return logged.getOrDefault(player, false);
    }

    private void sayWelcome(Player player) {
        player.displayClientMessage(
                TextComponent.Create("authforge.welcome", player.getUUID()),
                false
        );
    }

    private static Component wakeUp() {
        return TextComponent.Create("authforge.wakeUp", Config.delay.get());
    }

    private void teleportTo(Player player, PlayerDescriptor pos) {
        player.setPos(pos.getX(), pos.getY(), pos.getZ());
        player.teleportTo(pos.getX(), pos.getY(), pos.getZ());
    }
}
