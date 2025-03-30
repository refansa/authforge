package com.thumbleweed.authforge.event;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.thumbleweed.authforge.config.Config;
import com.thumbleweed.authforge.core.PlayerDescriptor;
import com.thumbleweed.authforge.util.text.ServerTranslationTextComponent;
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
    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
    private final Map<Player, PlayerDescriptor> descriptors = new HashMap<>();
    private final Map<Player, Boolean> logged = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player entity = event.getEntity();

        // initializing timer for kicking player if they haven't logged in
        Vec3 pos = entity.position();
        PlayerDescriptor dc = new PlayerDescriptor(pos.x, pos.y, pos.z);
        descriptors.put(entity, dc);
        scheduler.schedule(
                () -> {
                    if (descriptors.containsKey(entity)) {
                        descriptors.remove(entity);
                        logged.remove(entity);
                        ((ServerPlayer) event.getEntity()).connection.send(new ClientboundDisconnectPacket(wakeUp()));
                    }
                },
                Config.delay.get(),
                TimeUnit.SECONDS
        );
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        logged.remove(event.getEntity());
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
        Player entity = event.getEntity();
        if (descriptors.containsKey(entity) && event.isCancelable()) {
            event.setCanceled(true);
            teleportTo(entity, descriptors.get(entity));
            sayWelcome(entity);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onCommand(CommandEvent event) throws CommandSyntaxException {
        CommandSourceStack source = event.getParseResults().getContext().getSource();
        try {
            Player playerEntity = source.getPlayerOrException();
            List<? extends String> whitelist = Config.whitelistedCommands.get();
            String name = event.getParseResults().getContext().getNodes().get(0).getNode().getName();
            boolean isCommandAllowed = whitelist.contains(name);
            if (descriptors.containsKey(playerEntity) && !isCommandAllowed && event.isCancelable()) {
                event.setCanceled(true);
                event
                        .getParseResults()
                        .getContext().getSource()
                        .sendSuccess(() -> ServerTranslationTextComponent.CreateComponent("authforge.welcome"), false);
            }
        } catch (CommandSyntaxException e) {
            // raised when command comes from non-player entity
            return;
        }
    }

    @SubscribeEvent
    public void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        if (descriptors.containsKey(event.player) && event.side == LogicalSide.SERVER) {
            teleportTo(event.player, descriptors.get(event.player));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatEvent(ServerChatEvent event) {
        Player entity = event.getPlayer();
        if (descriptors.containsKey(entity)) {
            this.sayWelcome(entity);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTossEvent(ItemTossEvent event) {
        Player entity = event.getPlayer();
        if (event.isCancelable() && descriptors.containsKey(entity)) {
            event.setCanceled(true);
            entity.getInventory().add(event.getEntity().getItem());
            sayWelcome(entity);
        }
    }

    private void handleLivingEvents(LivingEvent event, Entity entity) {
        //noinspection SuspiciousMethodCalls
        if (event.getEntity() instanceof Player player && event.isCancelable() && descriptors.containsKey(entity)) {
            event.setCanceled(true);
            @SuppressWarnings("SuspiciousMethodCalls")
            PlayerDescriptor desc = descriptors.get(event.getEntity());
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

    private void sayWelcome(Player playerEntity) {
        playerEntity.displayClientMessage(ServerTranslationTextComponent.CreateComponent("authforge.welcome", playerEntity.getUUID()), false);
    }

    private static Component wakeUp() {
        return ServerTranslationTextComponent.CreateComponent("authforge.wakeUp", Config.delay.get());
    }

    private void teleportTo(Player player, PlayerDescriptor pos) {
        player.setPos(pos.getX(), pos.getY(), pos.getZ());
        player.teleportTo(pos.getX(), pos.getY(), pos.getZ());
    }
}
