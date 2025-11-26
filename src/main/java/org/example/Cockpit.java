package org.example;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.component.DataComponent;
import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.entity.metadata.other.GlowItemFrameMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.entity.metadata.other.ItemFrameMeta;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.SoundEffectPacket;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.Direction;

import java.util.HashSet;

public class Cockpit {

    public enum MoveState {FORWARD, BACKWARD, LEFT, RIGHT, NONE};
    private MoveState moveState  = MoveState.NONE;

    public enum ButtonType {LONGITUDINAL, LATERAL}

    private final InstanceContainer instance;
    private final Submarine submarine;

    // Buttons
    private int ticksSinceLastInput = 0;
    private Entity currentPressedButton = null;

    private final long cameraButtonDelay = 2500;
    private boolean cameraButtonDisabled = false;

    final Pos controlCenter = new Pos (0.5, 2.01, -2.5, 0, -90);

    private final TextColor BUTTON_COLOR = NamedTextColor.GRAY; //TextColor.color(215, 255, 255);
    private final int BUTTON_BRIGHTNESS = 9;
    private final int BUTTON_PRESSED_BRIGHTNESS = 8;
    private final double BUTTON_HEIGHT = 0.025;
    private final double BUTTON_PRESSED_HEIGHT = 0.01;

    private final TextColor GLOWING_COLOR = TextColor.color(175, 215, 80);
    private final int GLOWING_BRIGHTNESS = 12;



    public Cockpit(InstanceManager manager, Submarine submarine) {
        this.instance = manager.createInstanceContainer();
        this.instance.setChunkSupplier(LightingChunk::new);
        this.instance.setChunkLoader(new AnvilLoader("worlds/cockpit_world"));

        this.submarine = submarine;

        spawnControlButton(controlCenter.add(0.175, BUTTON_HEIGHT, 0.225), "▲",
            new Vec(2.5, 0.65, 0.65), 0.1f, 0.03f, ButtonType.LONGITUDINAL,
            () -> moveState = MoveState.FORWARD);
        spawnControlButton(controlCenter.add(0.175, BUTTON_HEIGHT, 0.375), "▼",
            new Vec(2.5, 0.65, 0.65), 0.1f, 0.03f, ButtonType.LONGITUDINAL,
            () -> moveState = MoveState.BACKWARD);
        spawnControlButton(controlCenter.add(-0.15, BUTTON_HEIGHT, 0.15), "▶",
            new Vec(1, 1, 2), 0.15f, 0.03f, ButtonType.LATERAL,
            () -> moveState = MoveState.RIGHT);
        spawnControlButton(controlCenter.add(-0.35, BUTTON_HEIGHT, 0.15), "◀",
            new Vec(1, 1, 2), 0.15f, 0.03f, ButtonType.LATERAL,
            () -> moveState = MoveState.LEFT);

        spawnCameraButton(
            new Vec(5, 1.75, 1),
            new Pos(0.55, 2.2, 2.99, 180, 0)
        );

        spawnCameraMapScreen(new Pos(1.5, 2.5, 2.5));


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

    private void spawnControlButton(Pos pos, String icon, Vec scale, float width, float height, ButtonType buttonType, Runnable action) {
        Entity visual = new Entity(EntityType.TEXT_DISPLAY);
        TextDisplayMeta visualMeta = (TextDisplayMeta) visual.getEntityMeta();

        visualMeta.setText(Component.text(icon).color(BUTTON_COLOR).decorate(TextDecoration.BOLD));
        visualMeta.setScale(scale);
        visualMeta.setBackgroundColor(0);
        visualMeta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);
        visualMeta.setHasNoGravity(true);
        visualMeta.setBrightness(BUTTON_BRIGHTNESS, 0);

        visual.setInstance(instance, pos);

        Entity shadow = new Entity(EntityType.TEXT_DISPLAY);
        TextDisplayMeta shadowMeta = (TextDisplayMeta) shadow.getEntityMeta();

        shadowMeta.setText(visualMeta.getText().color(NamedTextColor.BLACK));
        shadowMeta.setScale(scale);
        shadowMeta.setBackgroundColor(0);
        shadowMeta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);
        shadowMeta.setHasNoGravity(true);
        shadowMeta.setBrightness(0, 0);

        shadow.setInstance(instance, pos.withY(controlCenter.y()));

        HashSet<Entity> interactionEntities = new HashSet<>();
        if (buttonType == ButtonType.LONGITUDINAL) {
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
        visual.teleport(visual.getPosition().withY(controlCenter.y()+ BUTTON_PRESSED_HEIGHT));

        TextDisplayMeta visualMeta = (TextDisplayMeta)visual.getEntityMeta();
        visualMeta.setBrightness(BUTTON_PRESSED_BRIGHTNESS, 0);

        SoundEffectPacket clickSound = new SoundEffectPacket(SoundEvent.BLOCK_STONE_BUTTON_CLICK_ON, Sound.Source.BLOCK, visual.getPosition(), 0.3f, 1.5f, 0);

        Main.player.sendPacket(clickSound);
    }

    private void resetButtonVisual(Entity visual) {
        visual.teleport(visual.getPosition().withY(controlCenter.y()+BUTTON_HEIGHT));

        TextDisplayMeta visualMeta = (TextDisplayMeta)visual.getEntityMeta();
        visualMeta.setBrightness(BUTTON_BRIGHTNESS, 0);

        SoundEffectPacket releaseSound = new SoundEffectPacket(SoundEvent.BLOCK_STONE_BUTTON_CLICK_OFF, Sound.Source.BLOCK, visual.getPosition(), 0.3f, 1.5f, 0);
        Main.player.sendPacket(releaseSound);
    }

    private void spawnCameraButton(Vec scale, Pos pos) {
        Entity glow = new Entity(EntityType.TEXT_DISPLAY);
        TextDisplayMeta glowMeta = (TextDisplayMeta)glow.getEntityMeta();

        glowMeta.setText(Component.text("■").color(GLOWING_COLOR));
        glowMeta.setScale(scale);
        glowMeta.setBackgroundColor(0);
        glowMeta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);
        glowMeta.setHasNoGravity(true);
        glowMeta.setBrightness(GLOWING_BRIGHTNESS, 0);

        glow.setInstance(instance, pos);

        Entity button = new Entity(EntityType.TEXT_DISPLAY);
        TextDisplayMeta buttonMeta = (TextDisplayMeta)button.getEntityMeta();
        final float scaleDiff = 0.4f;

        buttonMeta.setText(Component.text("■").color(BUTTON_COLOR));
        buttonMeta.setScale(scale.sub(scaleDiff, scaleDiff, 0));
        buttonMeta.setBackgroundColor(0);
        buttonMeta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);
        buttonMeta.setHasNoGravity(true);
        buttonMeta.setBrightness(2, 0);

        button.setInstance(instance, pos.add(-0.007, 0.054, -0.0001));

        Entity interaction = new Entity(EntityType.INTERACTION);
        InteractionMeta interactionMeta = (InteractionMeta)interaction.getEntityMeta();

        interactionMeta.setWidth((float)scale.x()/8);
        interactionMeta.setHeight((float)scale.y()/8);
        interactionMeta.setHasNoGravity(true);

        interaction.setInstance(instance, pos.add(-0.06, 0.13, 0.25));

        MinecraftServer.getGlobalEventHandler().addListener(PlayerEntityInteractEvent.class, event -> {
            if (cameraButtonDisabled || event.getTarget() != interaction) return;
            pressCameraButton(button);
        });
    }

    private void pressCameraButton(Entity button) {
        SoundEffectPacket clickSound = new SoundEffectPacket(SoundEvent.BLOCK_CHERRY_WOOD_FENCE_GATE_OPEN, Sound.Source.BLOCK, button.getPosition(), 0.3f, 1.5f, 3);
        Main.player.sendPacket(clickSound);

        submarine.takePhoto();

        cameraButtonDisabled = true;

        MinecraftServer.getSchedulerManager().scheduleTask(
            () -> cameraButtonDisabled = false,
            TaskSchedule.millis(cameraButtonDelay), TaskSchedule.stop()
        );
    }

    private void spawnCameraMapScreen(Pos pos) {
        Entity screen = new Entity(EntityType.GLOW_ITEM_FRAME);
        GlowItemFrameMeta screenMeta = (GlowItemFrameMeta)screen.getEntityMeta();

        ItemStack mapItem = ItemStack.of(Material.FILLED_MAP)
            .with(DataComponents.MAP_ID, 0);

        screenMeta.setItem(mapItem);
        screenMeta.setInvisible(true);
        screenMeta.setDirection(Direction.WEST);
        screenMeta.setHasNoGravity(true);

        screen.setInstance(instance, pos);
    }

    private void updateDisplay() {
        String hud = submarine.getPositionText();
        Main.player.sendActionBar(Component.text(hud));
    }

    public InstanceContainer getInstance() {
        return instance;
    }
}
