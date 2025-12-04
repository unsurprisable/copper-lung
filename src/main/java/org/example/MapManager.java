package org.example;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.player.PlayerSwapItemEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;

import java.util.HashSet;
import java.util.Set;


public class MapManager {

    public static MapManager Instance;

    private final Inventory mapMarkerInventory;

    private final Set<MapMarkerItem> uncompletedMarkers;

    public MapManager() {
        if (Instance == null) {
            Instance = this;
        }

        MinecraftServer.getGlobalEventHandler().addListener(PlayerSwapItemEvent.class, event -> {
            openMap();
        });

        mapMarkerInventory = new Inventory(InventoryType.CHEST_5_ROW,
            Component.text("\uF801\uE001")
                .color(NamedTextColor.WHITE)
        );

        MinecraftServer.getGlobalEventHandler().addListener(InventoryPreClickEvent.class, event -> {
            if (event.getInventory() == mapMarkerInventory || event.getInventory() == Main.player.getInventory()) {
                event.setCancelled(true);
            }
        });

        uncompletedMarkers = new HashSet<>();
        uncompletedMarkers.add(new MapMarkerItem(mapMarkerInventory, 6,  724, 835, 153)); // objective_10
        uncompletedMarkers.add(new MapMarkerItem(mapMarkerInventory, 11, 305, 747, 157)); // objective_8
        uncompletedMarkers.add(new MapMarkerItem(mapMarkerInventory, 18, 118, 622, 3)); // objective_9
        uncompletedMarkers.add(new MapMarkerItem(mapMarkerInventory, 32, 598, 552, 57)); // objective_7
        uncompletedMarkers.add(new MapMarkerItem(mapMarkerInventory, 37, 201, 433, 246)); // objective_3
        uncompletedMarkers.add(new MapMarkerItem(Main.player.getInventory(), 11, 323, 282, 262)); // objective_2
        uncompletedMarkers.add(new MapMarkerItem(Main.player.getInventory(), 13, 519, 261, 8)); // objective_4
        uncompletedMarkers.add(new MapMarkerItem(Main.player.getInventory(), 17, 896, 247, 293)); // objective 6
        uncompletedMarkers.add(new MapMarkerItem(Main.player.getInventory(), 20, 286, 181, 33)); // objective_1
        uncompletedMarkers.add(new MapMarkerItem(Main.player.getInventory(), 23, 603, 176, 296)); // objective_5
    }

    private void openMap() {
        Main.player.playSound(SoundManager.OPEN_MAP);

        Main.player.openInventory(mapMarkerInventory);
    }

    public void checkIsPhotoValid(Vec origin) {

        final int bufX = 2;
        final int bufY = 2;
        final int bufYaw = 10;

//        Main.player.sendMessage(Component.text(String.format("Checking x:%.2f, y:%.2f, yaw:%.2f", origin.x(), origin.y(), origin.z())));

        for (MapMarkerItem uncompleted : uncompletedMarkers) {
//            Main.player.sendMessage(Component.text(String.format("%d %d %d", uncompleted.getX(), uncompleted.getY(), uncompleted.getYaw())));
            if (!(uncompleted.getX() - bufX < origin.x() && origin.x() < uncompleted.getX() + bufX)) continue;
            if (!(uncompleted.getY() - bufY < origin.y() && origin.y() < uncompleted.getY() + bufY)) continue;
            if (!(uncompleted.getYaw() - bufYaw < origin.z() && origin.z() < uncompleted.getYaw() + bufYaw)) continue;
            // the yaw buffer doesn't work properly for angles like A=003, where A=359 technically should be included

            // by now the photo is in this marker's zone
            uncompleted.setCompleted();
            uncompletedMarkers.remove(uncompleted);
            Main.player.playSound(SoundManager.SUCCESSFUL_PHOTOGRAPH);
            Main.player.sendActionBar(Component.text("Photograph captured ✓")
                .color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.ITALIC));
            break;
        }
    }
}
