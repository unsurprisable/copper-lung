package org.example;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.InstanceManager;

public class Main {

    public static Player player;

    static void main() {

        MinecraftServer server = MinecraftServer.init();
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();

        new Submarine(new Vec(286, 181, 0));
        new Cockpit();

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            player = event.getPlayer();

            event.setSpawningInstance(Cockpit.Instance.getInstance());
            player.setRespawnPoint(new Pos(.5, 1, .5, 180, 0));
            player.setGameMode(GameMode.ADVENTURE);
        });

        globalEventHandler.addListener(PlayerSpawnEvent.class, event -> {
            // initialize the camera display to be black when player joins
            Submarine.Instance.getCamera().disableAndClearCameraMap();

            new MapManager();
            new SoundManager();

            event.getPlayer().setSkin(PlayerSkin.fromUuid(event.getPlayer().getUuid().toString()));
        });

        server.start("localhost", 25565);
        System.out.println("Starting server on port 25565...");
    }
}
