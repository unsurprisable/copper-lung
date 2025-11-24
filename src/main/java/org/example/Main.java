package org.example;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerPickBlockEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import org.example.commands.SaveCommand;

public class Main {
    static void main() {

        MinecraftServer server = MinecraftServer.init();

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        instanceContainer.setChunkSupplier(LightingChunk::new);
        instanceContainer.setChunkLoader(new AnvilLoader("worlds/playerWorld"));

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(.5, 1, .5, 180, 0));
            player.setGameMode(GameMode.CREATIVE);
        });

        globalEventHandler.addListener(PlayerPickBlockEvent.class, event -> {
            final Player player = event.getPlayer();
            player.setItemInHand(PlayerHand.MAIN, ItemStack.of(event.getBlock().registry().material()));
        });

        MinecraftServer.getCommandManager().register(new SaveCommand());

        System.out.println("Starting server on port 25565...");
        server.start("localhost", 25565);

    }
}
