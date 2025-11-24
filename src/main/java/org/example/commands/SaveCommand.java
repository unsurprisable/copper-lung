package org.example.commands;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandExecutor;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.Nullable;

public class SaveCommand extends Command {
    public SaveCommand() {
        super("save");

        setDefaultExecutor((sender, context) -> {
            Player player = (Player)sender;
            player.sendMessage("Saving world...");
            player.getInstance().saveChunksToStorage();
        });
    }
}
