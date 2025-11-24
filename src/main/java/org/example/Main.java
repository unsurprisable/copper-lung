package org.example;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.InstanceManager;

public class Main {

    static Cockpit cockpit;

    static void main() {

        MinecraftServer server = MinecraftServer.init();
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();

        Submarine submarine = new Submarine(instanceManager, 200, 200, 0);
        cockpit = new Cockpit(instanceManager, submarine);

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(cockpit.getInstance());
            player.setRespawnPoint(new Pos(.5, 1, .5, 180, 0));
            player.setGameMode(GameMode.CREATIVE);
        });


        System.out.println("Starting server on port 25565...");
        server.start("localhost", 25565);

    }
}
