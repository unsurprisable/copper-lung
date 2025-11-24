package org.example;

import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;

public class Submarine {

    final InstanceContainer oceanInstance;
    double x;
    double z;
    double yaw;

    final double moveSpeed = 0.1;
    final double rotateSpeed = 0.5;

    public Submarine(InstanceManager instanceManager, double x, double z, double yaw) {
        this.oceanInstance = instanceManager.createInstanceContainer();
        this.x = x;
        this.z = z;
        this.yaw = yaw;
    }

    /**
     * @param direction is either 1 or -1
     */
    public void move(int direction) {
        double newX = x + (Math.cos(Math.toRadians(yaw)) * moveSpeed) * direction;
        double newZ = z + (Math.sin(Math.toRadians(yaw)) * moveSpeed) * direction;

        this.x = newX;
        this.z = newZ;
    }

    /**
     * @param direction is either 1 or -1
     */
    public void rotate(int direction) {
        double newYaw = (yaw + (rotateSpeed * direction)) % 360;
        if (newYaw < 0) {
            newYaw += 360;
        }
        yaw = newYaw;
    }

    public String getPositionText() {
        return String.format("X: %.2f  |  Y: %.2f  |  A: %.2f", x, z, yaw);
    }
}
