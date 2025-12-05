package org.example;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Submarine {

    public static Submarine Instance;

    private final InstanceContainer oceanInstance;
    private final SubmarineCamera camera;

    public enum MoveState {FORWARD, BACKWARD, LEFT, RIGHT, NONE};
    private MoveState moveState  = MoveState.NONE;

    private double x;
    private double z;
    private double yaw;
    private int moveDirection = 0;
    private int rotateDirection = 0;

    private final double minMoveAccel = 0.5;
    private final double maxMoveAccel = 4.8;
    private final double moveDrag = 3.2;
    private final int moveAccelChargeTicks = 50;
    private final double minAngAccel = 0;
    private final double maxAngAccel = 80;
    private final double angDrag = 3;
    private final int angAccelChargeTicks = 100;


    private int inputHeldTicks = 0;
    final double tickDeltaTime = 1.0/20.0;

    private Vec moveVelocity =  Vec.ZERO;
    private final double moveVelocityCutoffSqr = 0.005;
    private double angVelocity = 0;
    private final double angVelocityCutoff = 0.4;

    public Submarine(Vec startPos) {
        this(UnitConvert.fromMapYToWorldX(startPos.y()), UnitConvert.fromMapXToWorldZ(startPos.x()), startPos.z());
    }

    public Submarine(double worldX, double worldZ, double yaw) {
        if (Instance == null) {
            Instance = this;
        }

        this.oceanInstance = MinecraftServer.getInstanceManager().createInstanceContainer();
        oceanInstance.setChunkLoader(new AnvilLoader("worlds/ocean_world"));
        oceanInstance.enableAutoChunkLoad(false);

        long timeBeforeChunkLoading = System.currentTimeMillis();
        preloadOceanInstance(16);
        System.out.printf("Chunk loading completed in %d milliseconds\n", System.currentTimeMillis() - timeBeforeChunkLoading);

        SpriteObjectScanner.scanWorld(oceanInstance);

        this.camera = new SubmarineCamera(this.oceanInstance);

        this.x = worldZ;
        this.z = worldX;
        this.yaw = yaw;

        this.oceanInstance.eventNode().addListener(InstanceTickEvent.class, event -> {
            moveDirection = moveState == MoveState.FORWARD ? 1 : moveState == MoveState.BACKWARD ? -1 : 0;
            rotateDirection = moveState == MoveState.LEFT ? 1 : moveState == MoveState.RIGHT ? -1 : 0;

            // ACCELERATION
            double easingRatio = Math.clamp((double)inputHeldTicks/ moveAccelChargeTicks, 0.0, 1.0);
            double finalMoveAccel = (maxMoveAccel - minMoveAccel) * Math.pow(easingRatio, 2) + minMoveAccel;

            final Vec moveDragForce = this.moveVelocity.mul(-moveDrag);
            final Vec moveInputForce = new Vec(Math.cos(Math.toRadians(this.yaw)), Math.sin(Math.toRadians(this.yaw)))
                .mul(moveDirection * finalMoveAccel);
            final Vec netMoveForce = moveInputForce.add(moveDragForce);

            this.moveVelocity = this.moveVelocity.add(netMoveForce.mul(tickDeltaTime));
            if (!(moveState == MoveState.FORWARD || moveState == MoveState.BACKWARD) && moveVelocity.lengthSquared() <= moveVelocityCutoffSqr) {
                moveVelocity = Vec.ZERO;
            }

            easingRatio = Math.clamp((double)inputHeldTicks/ angAccelChargeTicks, 0.0, 1.0);
            double finalAngAccel = (maxAngAccel - minAngAccel) * Math.pow(easingRatio, 0.9) + minAngAccel;

            final double angDragForce = this.angVelocity * -angDrag;
            final double angInputForce = this.rotateDirection * finalAngAccel;
            final double netAngForce =  angDragForce + angInputForce;

            this.angVelocity += netAngForce * tickDeltaTime;
            if (!(moveState == MoveState.LEFT || moveState == MoveState.RIGHT) && Math.abs(angVelocity) <= angVelocityCutoff) {
                angVelocity = 0;
            }

            // VELOCITY
            if (!moveVelocity.equals(Vec.ZERO) || angVelocity != 0) {
                double newX = this.x + this.moveVelocity.x() * tickDeltaTime;
                double newZ = this.z + this.moveVelocity.z() * tickDeltaTime;

                // need to check for collisions first

                this.x = newX;
                this.z = newZ;

                double newYaw = (this.yaw + angVelocity * tickDeltaTime) % 360;
                if (newYaw < 0) {
                    newYaw += 360;
                }
                this.yaw = newYaw;

                ProgressionManager.Instance.onSubmarinePositionChange(
                    UnitConvert.fromWorldZToMapX(this.x),
                    UnitConvert.fromWorldXToMapY(this.z),
                    this.yaw
                );
            }

            if (moveState != MoveState.NONE) {
                inputHeldTicks++;
            }
        });
    }

    private void preloadOceanInstance(int size) {
        List<CompletableFuture<Chunk>> futures = new ArrayList<>();

        for (int x = 0; x <= size; x++) {
            for (int z = 0; z <= size; z++) {
                futures.add(oceanInstance.loadChunk(x, z));
            }
        }

        // blocks execution until all chunks are loaded
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    public void takePhoto() {
        camera.takePhoto(new Pos(z, 0, x, -(float)yaw, 0));
    }

    public void setMoveState(MoveState state) {
        inputHeldTicks = 0;
        moveState = state;
    }

    public double getX() {
        return x;
    }
    public double getMapX() {
        return (x - 5) * 4;
    }
    public double getZ() {
        return z;
    }
    public double getMapY() {
        return (z - 4) * 4;
    }
    public double getYaw() {
        return yaw;
    }
    public SubmarineCamera getCamera() {
        return camera;
    }

    public double getMaxSpeed() {
        return maxMoveAccel / moveDrag;
    }
    public double getMaxAngSpeed() {
        return maxAngAccel / angDrag;
    }

    private double getConvertedAngularSpeed() {
        double conversion = getMaxSpeed() / getMaxAngSpeed();
        return Math.abs(angVelocity) * conversion;
    }

    public double getTotalSpeed() {
        return moveVelocity.length() + getConvertedAngularSpeed();
    }

    public void teleport(double mapX, double mapY, double newYaw) {
        moveVelocity = Vec.ZERO;
        angVelocity = 0;
        this.x = UnitConvert.fromMapXToWorldZ(mapX);
        this.z = UnitConvert.fromMapYToWorldX(mapY);
        this.yaw = newYaw;
        SoundManager.play(SoundManager.TELEPORT);
        Main.player.addEffect(new Potion(PotionEffect.BLINDNESS, 255, 25));
        new Screenshake(5);
        camera.clearPrintingTask();
        camera.disableAndClearCameraMap();
    }
}
