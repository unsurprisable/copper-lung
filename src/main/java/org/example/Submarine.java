package org.example;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.anvil.AnvilLoader;

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

    private final double minMoveAccel = 0.8;
    private final double maxMoveAccel = 3.8;
    private final double moveDrag = 3.2;
    private final int moveAccelChargeTicks = 50;
    private final double minAngAccel = 0;
    private final double maxAngAccel = 80;
    private final double angDrag = 3;
    private final int angAccelChargeTicks = 100;

    private int inputHeldTicks = 0;
    final double tickDeltaTime = 1.0/20.0;

    private Vec moveVelocity =  new Vec(0,0);
    private double angVelocity = 0;

    public Submarine(Vec startPos) {
        this(UnitConvert.fromYToX(startPos.y()), UnitConvert.fromXToZ(startPos.x()), startPos.z());
    }

    public Submarine(double inGameX, double inGameZ, double yaw) {
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

        this.x = inGameZ;
        this.z = inGameX;
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

            easingRatio = Math.clamp((double)inputHeldTicks/ angAccelChargeTicks, 0.0, 1.0);
            double finalAngAccel = (maxAngAccel - minAngAccel) * Math.pow(easingRatio, 0.9) + minAngAccel;

            final double angDragForce = this.angVelocity * -angDrag;
            final double angInputForce = this.rotateDirection * finalAngAccel;
            final double netAngForce =  angDragForce + angInputForce;

            this.angVelocity += netAngForce * tickDeltaTime;

            // VELOCITY
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

            // MOVING SOUND EFFECT

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
    public double getZ() {
        return z;
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
}
