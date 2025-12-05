package org.example;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.instance.InstanceContainer;

import java.util.ArrayList;

public class SubmarineMoveSFX {

    private final double soundLength = 26.0;
    private double soundLengthLeft = soundLength;

    private final ArrayList<Entity> emitterEntities;
    private final Sound sound;

    private final InstanceContainer instance;

    private final double MAX_SPEED = Submarine.Instance.getMaxSpeed();
    private final double MIN_DISTANCE = 6;
    private final double MAX_DISTANCE = 16;
    private final float VOLUME = 0.75f;
    private final int SOUND_DIRECTIONS = 4;

    private final Pos origin = new Pos(0.5, 1.5, -1.5);

    public SubmarineMoveSFX(InstanceContainer instance) {
        this.instance = instance;

        sound = Sound.sound(
            Key.key("custom:submarine_move"),
            Sound.Source.MASTER,
            VOLUME/SOUND_DIRECTIONS, 1.0f
        );

        emitterEntities = new ArrayList<>();
        emitterEntities.add(new Entity(EntityType.ARMOR_STAND));
        emitterEntities.add(new Entity(EntityType.ARMOR_STAND));
        emitterEntities.add(new Entity(EntityType.ARMOR_STAND));
        emitterEntities.add(new Entity(EntityType.ARMOR_STAND));

        for (Entity e : emitterEntities) {
            ArmorStandMeta meta = (ArmorStandMeta) e.getEntityMeta();
            meta.setHasNoGravity(true);
            meta.setMarker(true);
            meta.setInvisible(true);
            e.setInstance(instance, origin);
        }

        setVolumeFromSpeed(0);
        playSound();
    }

    public void update(int deltaTimeMillis) {
        double deltaTime = deltaTimeMillis / 1000.0;
        soundLengthLeft -= deltaTime;
        if (soundLengthLeft <= 0) {
            playSound();
            soundLengthLeft = soundLength;
        }

        setVolumeFromSpeed(Submarine.Instance.getTotalSpeed());
    }

    private void playSound() {
        for (Entity e : emitterEntities) {
            instance.playSound(sound, e);
        }
    }

    public void setVolumeFromSpeed(double speed) {
        double conversion = (MAX_DISTANCE - MIN_DISTANCE) / MAX_SPEED;
        double distance = (MAX_DISTANCE) - (speed * conversion);

        emitterEntities.get(0).teleport(origin.add(0, 0,  -distance));
        emitterEntities.get(1).teleport(origin.add(0, 0,  distance));
        emitterEntities.get(2).teleport(origin.add(-distance, 0,  0));
        emitterEntities.get(3).teleport(origin.add(distance, 0,  0));
    }
}
