package org.example;

import net.minestom.server.MinecraftServer;
import net.minestom.server.component.DataComponent;
import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.MapDataPacket;
import net.minestom.server.tag.Tag;

import java.util.List;

public class Main {

    public static Submarine submarine;
    public static Cockpit cockpit;
    public static Player player;

    static void main() {

        MinecraftServer server = MinecraftServer.init();
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            player = event.getPlayer();
            submarine = new Submarine(instanceManager, 47.5, 55.5, 90);
            cockpit = new Cockpit(instanceManager, submarine);

            event.setSpawningInstance(cockpit.getInstance());
            player.setRespawnPoint(new Pos(.5, 1, .5, 180, 0));
            player.setGameMode(GameMode.CREATIVE);
        });

//        globalEventHandler.addListener(PlayerSpawnEvent.class, event -> {
//
//            MinecraftServer.getSchedulerManager().scheduleNextTick(() -> {
//                MapDataPacket initPacket = new MapDataPacket(0, (byte) 4, true, false, List.of(), null);
//                Main.player.sendPacket(initPacket);
//            });
//
//            ItemStack filledMap = ItemStack.of(Material.FILLED_MAP)
//
//
//            player.getInventory().setItemStack(0, filledMap);
//
//            System.out.println(filledMap.toItemNBT());
//
//        });


        System.out.println("Starting server on port 25565...");
        server.start("localhost", 25565);

    }
}
