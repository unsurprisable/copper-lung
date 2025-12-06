package org.example;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.timer.TaskSchedule;

public class CollisionScannerDirection {

    private final Entity entity;
    private final TextDisplayMeta entityMeta;

    private int maxBeepTimeTicks = 30;
    private int minBeepTimeTicks = 4;
    private int beepTimeTicksLeft = 0;
    private int targetBeepTimeTicks = 0;

    private boolean isDetecting = false;
    private double distance = 0;

    public CollisionScannerDirection() {
        this.entity = new Entity(EntityType.TEXT_DISPLAY);
        this.entityMeta = (TextDisplayMeta) entity.getEntityMeta();

        entityMeta.setText(Component.text("⬤").color(Cockpit.GLOWING_COLOR));
        entityMeta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);
        entityMeta.setBrightness(0, 0);
        entityMeta.setScale(new Vec(0.25));
        entityMeta.setHasNoGravity(true);
        entityMeta.setBackgroundColor(0);

        MinecraftServer.getSchedulerManager().buildTask(this::tickUpdate).repeat(TaskSchedule.nextTick()).schedule();

        //test
//        objectDetected(CollisionScanner.MAX_DETECTION_DISTANCE/2);
    }

    private void tickUpdate() {
        if (!isDetecting) return;

        if (beepTimeTicksLeft <= 0) {
            SoundManager.play(SoundManager.PROXIMITY);
            targetBeepTimeTicks = calculateTargetBeepTime(this.distance);
            beepTimeTicksLeft = targetBeepTimeTicks;
            entityMeta.setBrightness(Cockpit.GLOWING_BRIGHTNESS, 0);
        } else if (beepTimeTicksLeft <= targetBeepTimeTicks/2) {
            entityMeta.setBrightness(0, 0);
        }

        beepTimeTicksLeft--;
    }

    public void objectDetected(double distance) {
        isDetecting = true;
        this.distance = distance;
    }

    public void noObjectDetected() {
        isDetecting = false;
        beepTimeTicksLeft = 0;
        entityMeta.setBrightness(0, 0);
    }

    private int calculateTargetBeepTime(double distance) {
        double distRatio = (distance) / CollisionScanner.MAX_DETECTION_DISTANCE;
        int targetTimeTicks = (int)(minBeepTimeTicks + distRatio * (maxBeepTimeTicks - minBeepTimeTicks));
        if (targetTimeTicks % 2 == 1) targetTimeTicks--; // an odd tick number would cause the light/unlight timing to be uneven
        return targetTimeTicks;
    };

    public void setInstance(InstanceContainer instance, Point origin) {
        entity.setInstance(instance, origin);
    }
}
