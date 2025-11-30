package org.example;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.ArrayList;
import java.util.List;

public class SpriteObjectScanner {

    public static List<SpriteObject> activeSprites = new ArrayList<>();

    public static void scanWorld(Instance oceanInstance) {
        System.out.println("Scanning world for sprites...");

        SpriteTexture seaweedTexture =  SpriteTexture.testPattern(10);

        for (int x = 0; x <= 250; x++) {
            for (int z = 0; z <= 250; z++) {
                Block b = oceanInstance.getBlock(x, 0, z);

                if (b.compare(Block.GREEN_WOOL)) {
                    SpriteObject sprite = new SpriteObject(
                        new Pos(x + 0.5, 0.5, z + 0.5),
                        seaweedTexture,
                        1.0, 1.0
                    );
                    activeSprites.add(sprite);
                    oceanInstance.setBlock(x, 0, z, Block.AIR);
                }
            }
        }
        System.out.println("Found " + activeSprites.size() + " sprites.");
    }
}
