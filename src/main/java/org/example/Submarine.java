package org.example;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.anvil.AnvilLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Submarine {

    private final InstanceContainer oceanInstance;
    private final SubmarineCamera camera;


    private double x;
    private double z;
    private double yaw;
    private int moveDirection = 0;
    private int rotateDirection = 0;

    private final double moveAccel = 3.5;
    private final double angAccel = 150;
    private final double moveDrag = 2.8;
    private final double angDrag = 8;

    private Vec moveVelocity =  new Vec(0,0);
    private double angVelocity = 0;

    public Submarine(InstanceManager instanceManager, double x, double z, double yaw) {
        this.oceanInstance = instanceManager.createInstanceContainer();
        oceanInstance.setChunkLoader(new AnvilLoader("worlds/ocean_world"));
        oceanInstance.enableAutoChunkLoad(false);

        long timeBeforeChunkLoading = System.currentTimeMillis();
        preloadOceanInstance(16);
        System.out.printf("Chunk loading completed in %d milliseconds\n", System.currentTimeMillis() - timeBeforeChunkLoading);

        this.camera = new SubmarineCamera(this.oceanInstance);

        this.x = x;
        this.z = z;
        this.yaw = yaw;

        this.oceanInstance.eventNode().addListener(InstanceTickEvent.class, event -> {
            double deltaTime = (double)event.getDuration() / 1000d;

            // ACCELERATION

            final Vec moveDragForce = this.moveVelocity.mul(-moveDrag);
            final Vec moveInputForce = new Vec(Math.cos(Math.toRadians(this.yaw)), Math.sin(Math.toRadians(this.yaw)))
                .mul(moveDirection * moveAccel);
            final Vec netMoveForce = moveInputForce.add(moveDragForce);

            this.moveVelocity = this.moveVelocity.add(netMoveForce.mul(deltaTime));

            final double angDragForce = this.angVelocity * -angDrag;
            final double angInputForce = this.rotateDirection * this.angAccel;
            final double netAngForce =  angDragForce + angInputForce;

            this.angVelocity += netAngForce * deltaTime;

            // VELOCITY
            double newX = this.x + this.moveVelocity.x() * deltaTime;
            double newZ = this.z + this.moveVelocity.z() * deltaTime;

            // need to check for collisions first

            this.x = newX;
            this.z = newZ;

            double newYaw = (this.yaw + angVelocity * deltaTime) % 360;
            if (newYaw < 0) {
                newYaw += 360;
            }
            this.yaw = newYaw;

            moveDirection = 0;
            rotateDirection = 0;
        });
    }

    /**
     * @param direction is either 1 or -1
     */
    public void move(int direction) {
        moveDirection = direction;
    }

    /**
     * @param direction is either 1 or -1
     */
    public void rotate(int direction) {
        rotateDirection = direction;
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

    // IMPORTANT! display coordinates are multiplied by 4 to make the world seem bigger
    public String getPositionText() {
        return String.format("X: %.2f  |  Y: %.2f  |  A: %.2f", x*4, z*4, yaw);
    }
    public String getDebugPositionText() {
        return String.format("X: %.2f  |  Y: %.2f  |  A: %.2f   [(%.2f, %.2f), %.2f]", x, z, yaw, moveVelocity.x(), moveVelocity.z(), angVelocity);
    }


    public SubmarineCamera getCamera() {
        return camera;
    }
}
