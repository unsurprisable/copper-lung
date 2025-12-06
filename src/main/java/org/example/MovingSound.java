package org.example;

import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.instance.InstanceContainer;

public class MovingSound {
    private final InstanceContainer instance;
    private final Sound sound;
    private final double soundLength;
    private final Point worldOrigin;
    private final double maxDistanceSqr;

    private final double MAX_EMITTER_DISTANCE = 16;
    private final double MIN_EMITTER_DISTANCE = 1;
    private final Entity emitter;
    private double soundLengthLeft = 0;
    private Pos playerPos = new Pos(0, 0, 32);

    private boolean isTooFar = false;

    public MovingSound(InstanceContainer instance, Sound sound, double soundLength, Point worldOrigin, double maxDistance) {
        this.instance = instance;
        this.sound = sound;
        this.worldOrigin = worldOrigin;
        this.soundLength = soundLength;
        this.maxDistanceSqr = maxDistance * maxDistance;

        this.emitter = new Entity(EntityType.ARMOR_STAND);
        ArmorStandMeta meta = (ArmorStandMeta) emitter.getEntityMeta();
        meta.setInvisible(true);
        meta.setMarker(true);
        meta.setHasNoGravity(true);
//        meta.setHasGlowingEffect(true); // debug
        emitter.setInstance(instance, playerPos);
    }

    private void playSound() {
        SoundManager.play(sound, emitter);
    }

    public void update(int deltaTimeMillis) {
        playerPos = Main.player.getPosition();
        updateEmitterPosition();

        if (isTooFar) return;

        double deltaTime = deltaTimeMillis / 1000.0;
        soundLengthLeft -= deltaTime;
        if (soundLengthLeft <= 0) {
            playSound();
            soundLengthLeft = soundLength;
        }
    }

    private void updateEmitterPosition() {
        double distanceSqr = getWorldDistanceSqr();

        if (!isTooFar && distanceSqr > maxDistanceSqr) {
            isTooFar = true;
            SoundManager.stop(sound);
            soundLengthLeft = 0;
        } else if (distanceSqr <= maxDistanceSqr) {
            isTooFar = false;
        }

        if (isTooFar) return;

        double distanceRatio = distanceSqr / maxDistanceSqr;
        double emitterDistance = MIN_EMITTER_DISTANCE + distanceRatio * (MAX_EMITTER_DISTANCE - MIN_EMITTER_DISTANCE);
        double emitterAngle = -getAngle();

        double radians = Math.toRadians(emitterAngle);
        double offsetX = -Math.sin(radians) * emitterDistance;
        double offsetZ = Math.cos(radians) * emitterDistance;

        Pos finalEmitterPos = playerPos.add(offsetX, 0, offsetZ);
        emitter.teleport(finalEmitterPos);
    }

    private double getWorldDistanceSqr() {
        return Submarine.Instance.getWorldPosition().distanceSquared(worldOrigin);
    }

    private double getAngle() {
        Pos subPosition = Submarine.Instance.getWorldPosition();
        return subPosition.yaw() - subPosition.withLookAt(worldOrigin).yaw() - 180;
    }
}
