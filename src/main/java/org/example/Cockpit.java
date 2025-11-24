package org.example;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.network.packet.server.play.SoundEffectPacket;
import net.minestom.server.sound.SoundEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

public class Cockpit {

    public enum MoveState {FORWARD, BACKWARD, LEFT, RIGHT, NONE};
    private MoveState moveState  = MoveState.NONE;

    public enum ButtonType {LONGITUDAL, LATERAL}

    private final InstanceContainer instance;
    private final Submarine submarine;

    // Buttons
    private int ticksSinceLastInput = 0;
    private Entity currentPressedButton = null;

    final Pos controlCenter = new Pos (0.5, 2.016, -2.5, 0, -90);

    final TextColor BUTTON_COLOR = NamedTextColor.GOLD; //TextColor.color(215, 255, 255);
    final int BUTTON_BRIGHTNESS = 6;
    final int BUTTON_PRESSED_BRIGHTNESS = 4;



    public Cockpit(InstanceManager manager, Submarine submarine) {
        this.instance = manager.createInstanceContainer();
        this.instance.setChunkSupplier(LightingChunk::new);
        this.instance.setChunkLoader(new AnvilLoader("worlds/cockpit_world"));

        this.submarine = submarine;

        spawnButton(controlCenter.add(0.175, 0, 0.225), "▲",
            new Vec(2.5, 0.65, 0.65), 0.1f, 0.03f, ButtonType.LONGITUDAL,
            () -> moveState = MoveState.FORWARD);
        spawnButton(controlCenter.add(0.175, 0, 0.375), "▼",
            new Vec(2.5, 0.65, 0.65), 0.1f, 0.03f, ButtonType.LONGITUDAL,
            () -> moveState = MoveState.BACKWARD);
        spawnButton(controlCenter.add(-0.15, 0, 0.15), "▶",
            new Vec(1, 1, 2), 0.15f, 0.03f, ButtonType.LATERAL,
            () -> moveState = MoveState.RIGHT);
        spawnButton(controlCenter.add(-0.35, 0, 0.15), "◀",
            new Vec(1, 1, 2), 0.15f, 0.03f, ButtonType.LATERAL,
            () -> moveState = MoveState.LEFT);

        this.instance.eventNode().addListener(InstanceTickEvent.class, event -> {
            tickButtonReset();
            ticksSinceLastInput++;

            updateDisplay();

            switch (moveState) {
                case FORWARD:
                    submarine.move(1);
                    break;
                case BACKWARD:
                    submarine.move(-1);
                    break;
                case LEFT:
                    submarine.rotate(1);
                    break;
                case RIGHT:
                    submarine.rotate(-1);
                    break;
                default:
                    break;
            }
        });

    }

    private void spawnButton(Pos pos, String icon, Vec scale, float width, float height, ButtonType buttonType, Runnable action) {
        Entity visual = new Entity(EntityType.TEXT_DISPLAY);
        TextDisplayMeta visualMeta = (TextDisplayMeta) visual.getEntityMeta();

        visualMeta.setText(Component.text(icon).color(BUTTON_COLOR).decorate(TextDecoration.BOLD));
        visualMeta.setScale(scale);
        visualMeta.setBackgroundColor(0);
        visualMeta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);
        visualMeta.setHasNoGravity(true);
        visualMeta.setBrightness(BUTTON_BRIGHTNESS, 0);

        visual.setInstance(instance, pos);

        HashSet<Entity> interactionEntities = new HashSet<>();
        if (buttonType == ButtonType.LONGITUDAL) {
            for (int i = 0; i < 4; i++) {
                Entity interaction = new Entity(EntityType.INTERACTION);
                InteractionMeta interactionMeta = (InteractionMeta) interaction.getEntityMeta();

                interactionMeta.setWidth(width);
                interactionMeta.setHeight(height);

                float xOffset = -0.019f;
                interaction.setInstance(instance, pos.sub(width - width*i - xOffset, 0, width));
                interactionEntities.add(interaction);
            }
        } else if (buttonType == ButtonType.LATERAL) {
            Entity interaction = new Entity(EntityType.INTERACTION);
            InteractionMeta interactionMeta = (InteractionMeta) interaction.getEntityMeta();

            interactionMeta.setWidth(width);
            interactionMeta.setHeight(height);

            float xOffset = 0.012f;
            float zOffset = width - 0.015f;
            interaction.setInstance(instance, pos.sub(-xOffset, 0, zOffset));
            interactionEntities.add(interaction);
        }

        MinecraftServer.getGlobalEventHandler().addListener(PlayerEntityInteractEvent.class, event -> {
           if (!interactionEntities.contains(event.getTarget())) return;

           ticksSinceLastInput = 0;

           if (currentPressedButton == visual) return;

           action.run();

           if (currentPressedButton != null) {
               resetButtonVisual(currentPressedButton);
           }

            pressButtonVisual(visual);
            currentPressedButton = visual;
        });
    }

    private void tickButtonReset() {

        if (currentPressedButton == null) return;

        int maxTickBufferTime = 5;
        if (ticksSinceLastInput >= maxTickBufferTime) {
            resetButtonVisual(currentPressedButton);
            currentPressedButton = null;
            moveState = MoveState.NONE;
        }
    }

    private void pressButtonVisual(Entity visual) {
        visual.teleport(visual.getPosition().withY(controlCenter.y()-0.015));

        TextDisplayMeta visualMeta = (TextDisplayMeta)visual.getEntityMeta();
        visualMeta.setBrightness(BUTTON_PRESSED_BRIGHTNESS, 0);

        SoundEffectPacket clickSound = new SoundEffectPacket(SoundEvent.BLOCK_STONE_BUTTON_CLICK_ON, Sound.Source.BLOCK, visual.getPosition(), 0.3f, 1.5f, 0);
        instance.sendGroupedPacket(clickSound);
    }

    private void resetButtonVisual(Entity visual) {
        visual.teleport(visual.getPosition().withY(controlCenter.y()));

        TextDisplayMeta visualMeta = (TextDisplayMeta)visual.getEntityMeta();
        visualMeta.setBrightness(BUTTON_BRIGHTNESS, 0);

        SoundEffectPacket releaseSound = new SoundEffectPacket(SoundEvent.BLOCK_STONE_BUTTON_CLICK_OFF, Sound.Source.BLOCK, visual.getPosition(), 0.3f, 1.5f, 0);
        instance.sendGroupedPacket(releaseSound);
    }

    private void updateDisplay() {
        String hud = submarine.getPositionText();
        for (Player p : instance.getPlayers()) {
            p.sendActionBar(Component.text(hud));
        }
    }

    public InstanceContainer getInstance() {
        return instance;
    }
}
