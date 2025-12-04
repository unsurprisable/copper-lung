package org.example;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentDouble;

public class TeleportCommand extends Command {
    public TeleportCommand() {
        super("tp");

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
