package org.example;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SpriteObjectScanner {

    private record SpriteTextureObjectData (SpriteTexture texture, double size, double verticalOffset){}

    public static List<SpriteObject> activeSprites = new ArrayList<>();

    private static final HashMap<Integer, SpriteTextureObjectData> blockSpriteMap = new HashMap<>();

    public static void scanWorld(Instance oceanInstance) {
        // DEBRIS
        blockSpriteMap.put(Block.GREEN_WOOL.id(), new SpriteTextureObjectData(
            SpriteLoader.load("debris_1"), 1.5, 0.75));

        // OBJECTIVES
        blockSpriteMap.put(Block.WHITE_GLAZED_TERRACOTTA.id(), new SpriteTextureObjectData(
            SpriteLoader.load("objective_1"), 3.0, 0.25));
        blockSpriteMap.put(Block.LIGHT_GRAY_GLAZED_TERRACOTTA.id(), new SpriteTextureObjectData(
            SpriteLoader.load("objective_2"), 3.0, 0.25));
        blockSpriteMap.put(Block.GRAY_GLAZED_TERRACOTTA.id(), new SpriteTextureObjectData(
            SpriteLoader.load("objective_3"), 3.0, 0.25));
        blockSpriteMap.put(Block.BLACK_GLAZED_TERRACOTTA.id(), new SpriteTextureObjectData(
            SpriteLoader.load("objective_4"), 3.0, 0.25));
        blockSpriteMap.put(Block.BROWN_GLAZED_TERRACOTTA.id(), new SpriteTextureObjectData(
            SpriteLoader.load("objective_5"), 3.0, 0.25));
        blockSpriteMap.put(Block.RED_GLAZED_TERRACOTTA.id(), new SpriteTextureObjectData(
            SpriteLoader.load("objective_6"), 3.0, 0.25));
        blockSpriteMap.put(Block.ORANGE_GLAZED_TERRACOTTA.id(), new SpriteTextureObjectData(
            SpriteLoader.load("objective_7"), 3.0, 0.25));
        blockSpriteMap.put(Block.YELLOW_GLAZED_TERRACOTTA.id(), new SpriteTextureObjectData(
            SpriteLoader.load("objective_8"), 3.0, 0.25));
        blockSpriteMap.put(Block.LIME_GLAZED_TERRACOTTA.id(), new SpriteTextureObjectData(
            SpriteLoader.load("objective_9"), 3.0, 0.25));
        blockSpriteMap.put(Block.GREEN_GLAZED_TERRACOTTA.id(), new SpriteTextureObjectData(
            SpriteLoader.load("objective_10"), 3.0, 0.25));

        System.out.println("Scanning world for sprites...");

        for (int x = 0; x <= 256; x++) {
            for (int z = 0; z <= 256; z++) {
                Block block = oceanInstance.getBlock(x, 0, z);

                if (blockSpriteMap.containsKey(block.id())) {
                    SpriteTextureObjectData data =  blockSpriteMap.get(block.id());
                    SpriteObject sprite = new SpriteObject(
                        new Pos(x + 0.5, 0.5, z + 0.5),
                        data.texture, data.size, data.verticalOffset
                    );
                    activeSprites.add(sprite);
                    oceanInstance.setBlock(x, 0, z, Block.AIR);
                }
            }
        }
        System.out.println("Found " + activeSprites.size() + " sprites.");
    }
}
