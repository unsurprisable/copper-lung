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

        new MapMarkerItem(Main.player.getInventory(), 11, 0, 0, 0);
        new MapMarkerItem(Main.player.getInventory(), 13, 0, 0, 0);
        new MapMarkerItem(Main.player.getInventory(), 17, 0, 0, 0);
        new MapMarkerItem(Main.player.getInventory(), 20, 310, 277, 315);
        new MapMarkerItem(Main.player.getInventory(), 23, 0, 0, 0);
        new MapMarkerItem(mapMarkerInventory, 6, 0, 0, 0);
        new MapMarkerItem(mapMarkerInventory, 11, 0, 0, 0);
        new MapMarkerItem(mapMarkerInventory, 18, 0, 0, 0);
        new MapMarkerItem(mapMarkerInventory, 32, 0, 0, 0);
        new MapMarkerItem(mapMarkerInventory, 37, 0, 0, 0);
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
