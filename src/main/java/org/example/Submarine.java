package org.example;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;

public class Submarine {

    final InstanceContainer oceanInstance;
    double x;
    double z;
    double yaw;

    int moveDirection = 0;
    int rotateDirection = 0;

    final double moveAccel = 8.4;
    final double angAccel = 2;
    final double moveDrag = 2.4;
    final double angDrag = .45;

    Vec moveVelocity =  new Vec(0,0);
    double angVelocity = 0;

    public Submarine(InstanceManager instanceManager, double x, double z, double yaw) {
        this.oceanInstance = instanceManager.createInstanceContainer();
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

    public String getPositionText() {
        return String.format("X: %.2f  |  Y: %.2f  |  A: %.2f", x, z, yaw);
    }
    public String getDebugPositionText() {
        return String.format("X: %.2f  |  Y: %.2f  |  A: %.2f   [(%.2f, %.2f), %.2f]", x, z, yaw, moveVelocity.x(), moveVelocity.z(), angVelocity);
    }
}
