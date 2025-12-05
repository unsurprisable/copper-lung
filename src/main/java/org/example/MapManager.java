package org.example;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
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

    public MapMarkerItem OBJECTIVE_1;
    public MapMarkerItem OBJECTIVE_2;
    public MapMarkerItem OBJECTIVE_3;
    public MapMarkerItem OBJECTIVE_4;
    public MapMarkerItem OBJECTIVE_5;
    public MapMarkerItem OBJECTIVE_6;
    public MapMarkerItem OBJECTIVE_7;
    public MapMarkerItem OBJECTIVE_8;
    public MapMarkerItem OBJECTIVE_9;
    public MapMarkerItem OBJECTIVE_10;

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

        OBJECTIVE_1 = new MapMarkerItem(Main.player.getInventory(), 20, 286, 181, 33);
        OBJECTIVE_2 = new MapMarkerItem(Main.player.getInventory(), 11, 323, 282, 262);
        OBJECTIVE_3 = new MapMarkerItem(mapMarkerInventory, 37, 201, 434, 251);
        OBJECTIVE_4 = new MapMarkerItem(Main.player.getInventory(), 13, 517, 261, 357);
        OBJECTIVE_5 = new MapMarkerItem(Main.player.getInventory(), 23, 604, 161, 72);
        OBJECTIVE_6 = new MapMarkerItem(Main.player.getInventory(), 17, 895, 241, 250);
        OBJECTIVE_7 = new MapMarkerItem(mapMarkerInventory, 32, 597, 552, 57);
        OBJECTIVE_8 = new MapMarkerItem(mapMarkerInventory, 18, 107, 621, 62);
        OBJECTIVE_9 = new MapMarkerItem(mapMarkerInventory, 11, 307, 747, 158);
        OBJECTIVE_10 = new MapMarkerItem(mapMarkerInventory, 6,  724, 835, 153);

        uncompletedMarkers = new HashSet<>();
        uncompletedMarkers.add(OBJECTIVE_1);
        uncompletedMarkers.add(OBJECTIVE_2);
        uncompletedMarkers.add(OBJECTIVE_3);
        uncompletedMarkers.add(OBJECTIVE_4);
        uncompletedMarkers.add(OBJECTIVE_5);
        uncompletedMarkers.add(OBJECTIVE_6);
        uncompletedMarkers.add(OBJECTIVE_7);
        uncompletedMarkers.add(OBJECTIVE_8);
        uncompletedMarkers.add(OBJECTIVE_9);
        uncompletedMarkers.add(OBJECTIVE_10);
    }

    private void openMap() {
        SoundManager.play(SoundManager.OPEN_MAP);

        Main.player.openInventory(mapMarkerInventory);
    }

    public void checkIsPhotoValid(Vec origin) {

        final int bufX = 1;   // within 2
        final int bufY = 1;   // within 2
        final int bufYaw = 5; // within 10

//        Main.player.sendMessage(Component.text(String.format("Checking x:%.2f, y:%.2f, yaw:%.2f", origin.x(), origin.y(), origin.z())));

        for (MapMarkerItem uncompleted : uncompletedMarkers) {
//            Main.player.sendMessage(Component.text(String.format("%d %d %d", uncompleted.getMapX(), uncompleted.getMapY(), uncompleted.getYaw())));
            if (!(uncompleted.getMapX() - bufX < origin.x() && origin.x() < uncompleted.getMapX() + bufX)) continue;
            if (!(uncompleted.getMapY() - bufY < origin.y() && origin.y() < uncompleted.getMapY() + bufY)) continue;

            double yawDiff = Math.abs(uncompleted.getYaw() - origin.z()) % 360;
            if (yawDiff > 180) yawDiff = 360 - yawDiff;
            if (yawDiff > bufYaw) continue;

            // by now the photo is in this marker's zone
            uncompleted.setCompleted();
            uncompletedMarkers.remove(uncompleted);
            SoundManager.play(SoundManager.SUCCESSFUL_PHOTOGRAPH);
            Main.player.sendActionBar(Component.text("Photograph captured ✓")
                .color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.ITALIC));
            ProgressionManager.Instance.objectiveCollected();

            break;
        }
    }
}
