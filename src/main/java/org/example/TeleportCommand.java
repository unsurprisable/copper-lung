package org.example;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;

public class TeleportCommand extends Command {
    public TeleportCommand() {
        super("tp");

        var objective = ArgumentType.Integer("objective");
        addSyntax((sender, context) -> {
            int objectiveNum = context.get(objective);

            MapMarkerItem obj = switch(objectiveNum) {
                case 1 -> MapManager.Instance.OBJECTIVE_1;
                case 2 -> MapManager.Instance.OBJECTIVE_2;
                case 3 -> MapManager.Instance.OBJECTIVE_3;
                case 4 -> MapManager.Instance.OBJECTIVE_4;
                case 5 -> MapManager.Instance.OBJECTIVE_5;
                case 6 -> MapManager.Instance.OBJECTIVE_6;
                case 7 -> MapManager.Instance.OBJECTIVE_7;
                case 8 -> MapManager.Instance.OBJECTIVE_8;
                case 9 -> MapManager.Instance.OBJECTIVE_9;
                case 10 -> MapManager.Instance.OBJECTIVE_10;
                default -> null;
            };
            System.out.println("objective");
            Submarine.Instance.teleport(obj.getMapX(), obj.getMapY(), obj.getYaw());
        }, objective);

        var xPos = ArgumentType.Double("xPos");
        var yPos = ArgumentType.Double("yPos");
        var yawPos = ArgumentType.Double("yaw");
        addSyntax((sender, context) -> {
           double x = context.get(xPos);
           double y = context.get(yPos);
           double yaw = context.get(yawPos);
           Submarine.Instance.teleport(x, y, yaw);
        }, xPos, yPos, yawPos);
    }
}
