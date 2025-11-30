package org.example;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.player.PlayerSwapItemEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;


public class MapManager {

    private final Inventory mapMarkerInventory;

    public MapManager() {
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


        new MapMarkerItem(mapMarkerInventory, 6,  724, 835, 153); // objective_10
        new MapMarkerItem(mapMarkerInventory, 11, 305, 747, 157); // objective_8
        new MapMarkerItem(mapMarkerInventory, 18, 118, 622, 3); // objective_9
        new MapMarkerItem(mapMarkerInventory, 32, 598, 552, 57); // objective_7
        new MapMarkerItem(mapMarkerInventory, 37, 201, 433, 246); // objective_3
        new MapMarkerItem(Main.player.getInventory(), 11, 323, 282, 262); // objective_2
        new MapMarkerItem(Main.player.getInventory(), 13, 519, 261, 8); // objective_4
        new MapMarkerItem(Main.player.getInventory(), 17, 896, 247, 293); // objective 6
        new MapMarkerItem(Main.player.getInventory(), 20, 286, 181, 33); // objective_1
        new MapMarkerItem(Main.player.getInventory(), 23, 603, 176, 296); // objective_5
    }

    private void openMap() {
        Main.player.playSound(Sound.sound(
            Key.key("custom:open_map"),
            Sound.Source.MASTER,
            0.25f, 1.15f
        ));

        Main.player.openInventory(mapMarkerInventory);
    }
}
