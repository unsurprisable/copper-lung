package org.example;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import net.minestom.server.component.DataComponent;
import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.mojangAuth.MojangCrypt;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class Main {

    public static Submarine submarine;
    public static Cockpit cockpit;
    public static Player player;
    public static MapManager mapManager;

    static void main() {

        MinecraftServer server = MinecraftServer.init();
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();

        submarine = new Submarine(instanceManager,112.25, 55.25, 246);
        cockpit = new Cockpit(instanceManager, submarine);

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
//            if (player != null) {
//                event.getPlayer().kick(Component.text("Map has already been generated! Restart the server to rejoin.").color(NamedTextColor.RED));
//            }
            player = event.getPlayer();

            event.setSpawningInstance(cockpit.getInstance());
            player.setRespawnPoint(new Pos(.5, 1, .5, 180, 0));
            player.setGameMode(GameMode.ADVENTURE);
        });

        globalEventHandler.addListener(PlayerSpawnEvent.class, event -> {
            // initialize the camera display to be black when player joins
            submarine.getCamera().disableAndClearCameraMap();
            mapManager = new MapManager();

//            Main.player.playSound(Sound.sound(
//                Key.key("custom:dark_bramble"),
//                Sound.Source.MASTER,
//                10f, 1f
//            ));
        });

        server.start("localhost", 25565);
        System.out.println("Starting server on port 25565...");
    }
}
