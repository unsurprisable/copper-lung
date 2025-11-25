package org.example;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.MapDataPacket;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SubmarineCamera {

    private final InstanceContainer oceanInstance;

    public SubmarineCamera(InstanceContainer oceanInstance) {
        this.oceanInstance = oceanInstance;
    }

    public enum Shade { BRIGHT, LIGHT, MID_LIGHT, MEDIUM, MID_DARK, DARK, VERY_DARK, VOID }

    final byte[] grayscaleShades = {
        (byte) 34,  // White (255)      @ 100% (255)  0
        (byte) 33,  // White (255)      @ 86%  (219)  1
        (byte) 32,  // White (255)      @ 71%  (181)  2
        (byte) 26,  // Lt Gray (167)    @ 100% (167)  3
        (byte) 90,  // W.Terra (153)    @ 100% (153)  4
        (byte) 25,  // Lt Gray (167)    @ 86%  (144)  5
        (byte) 35,  // White (255)      @ 53%  (135)  6
        (byte) 89,  // W.Terra (153)    @ 86%  (131)  7
        (byte) 24,  // Lt Gray (167)    @ 71%  (119)  8
        (byte) 46,  // Gray (112)       @ 100% (112)  9
        (byte) 88,  // W.Terra (153)    @ 71%  (108)  10
        (byte) 238, // Deepslate (100)  @ 100% (100)  11
        (byte) 45,  // Gray (112)       @ 86%  (96)   12
        (byte) 27,  // Lt Gray (167)    @ 53%  (89)   13
        (byte) 237, // Deepslate (100)  @ 86%  (86)   14
        (byte) 91,  // W.Terra (153)    @ 53%  (81)   15
        (byte) 44,  // Gray (112)       @ 71%  (80)   16
        (byte) 86,  // G.Terra (76)     @ 100% (76)   17
        (byte) 236, // Deepslate (100)  @ 71%  (71)   18
        (byte) 85,  // G.Terra (76)     @ 86%  (65)   19
        (byte) 47,  // Gray (112)       @ 53%  (59)   20
        (byte) 84,  // G.Terra (76)     @ 71%  (54)   21
        (byte) 239, // Deepslate (100)  @ 53%  (53)   22
        (byte) 87,  // G.Terra (76)     @ 53%  (40)   23
        (byte) 118, // Black (25)       @ 100% (25)   24
        (byte) 117, // Black (25)       @ 86%  (21)   25
        (byte) 116, // Black (25)       @ 71%  (17)   26
        (byte) 119  // Black (25)       @ 53%  (13)   27
    };

    private final Random rand = new Random();

    private byte getFuzzyColor(double distance) {
        double ratio = distance / MAX_CAMERA_DISTANCE;
        ratio = Math.clamp(ratio, 0.0, 1.0);

        double fogCurve = .35;
        ratio = Math.pow(ratio, fogCurve);

        int minIndex = 1;
        int maxIndex = grayscaleShades.length - 1 - 1;
        double targetIndex = ratio * (maxIndex - minIndex) + minIndex;

        // widens the curve of the Gaussian to allow multiple shades to be picked
//        double fuzziness = 0.5;

         double fuzziness = 0.3 + ((1 - ratio) * 2);

        double noisyIndex = targetIndex + (rand.nextGaussian() * fuzziness);
        int finalIndex = (int) Math.round(noisyIndex);
        finalIndex = Math.clamp(finalIndex, 0, maxIndex);

        return grayscaleShades[finalIndex];
    }

    private byte getRandomColorByShade(Shade shade) {
        Random rand = new Random();

        return switch (shade) {
            case BRIGHT -> grayscaleShades[rand.nextInt(0, 4)];
            case LIGHT -> grayscaleShades[rand.nextInt(4, 8)];
            case MID_LIGHT -> grayscaleShades[rand.nextInt(8, 12)];
            case MEDIUM -> grayscaleShades[rand.nextInt(12, 15)];
            case MID_DARK -> grayscaleShades[rand.nextInt(15, 20)];
            case DARK -> grayscaleShades[rand.nextInt(20, 23)];
            case VERY_DARK -> grayscaleShades[23];
            case VOID -> grayscaleShades[rand.nextInt(24, 28)];
        };
    }

    private final double MAX_CAMERA_DISTANCE = 10;

    public void takePhoto(Pos origin) {
        byte[] map_pixels = new byte[128 * 128];

        for (int x = 0; x < 128; x++) {
            for (int y = 0; y < 128; y++) {
                int index = x + y*128;
                map_pixels[index] = getRandomColorByShade(Shade.VOID);
            }
        }

        double fov = 80;
        double startAngle = -fov / 2;
        double angleStep = fov / 128; // degrees per pixel column

        for (int x = 0; x < 128; x++) {
            double relativeAngle = startAngle + angleStep * x;

            Pos rayPos = origin.withYaw((float)(origin.yaw() + relativeAngle));

            double rawDistance = scan(rayPos);
            if (rawDistance == -1) continue; //nothing

            // fisheye curve correction
            double correctedDist = rawDistance * Math.cos(Math.toRadians(relativeAngle));

            final double wallHeightScale = 80;

            int wallHeight = (int) (wallHeightScale / correctedDist);
            wallHeight = Math.max(0, Math.min(128, wallHeight));

            int ceiling = 64 - (wallHeight / 2);
            int floor = 64 + (wallHeight / 2);

            for (int y = ceiling; y <= floor; y++) {
                if (y < 0 || y >= 128) continue;

                int index = x + (y * 128);

//                // hardcoded fog
//                if (correctedDist < MAX_CAMERA_DISTANCE * 0.1) {
//                    map_pixels[index] = getRandomColorByShade(Shade.LIGHT);
//                } else if (correctedDist < MAX_CAMERA_DISTANCE * 0.22) {
//                    map_pixels[index] = getRandomColorByShade(Shade.MID_LIGHT);
//                } else if (correctedDist < MAX_CAMERA_DISTANCE * 0.36) {
//                    map_pixels[index] = getRandomColorByShade(Shade.MEDIUM);
//                } else if (correctedDist < MAX_CAMERA_DISTANCE * 0.52) {
//                    map_pixels[index] = getRandomColorByShade(Shade.MID_DARK);
//                } else if (correctedDist < MAX_CAMERA_DISTANCE * 0.75) {
//                    map_pixels[index] = getRandomColorByShade(Shade.DARK);
//                } else {
//                    map_pixels[index] = getRandomColorByShade(Shade.VERY_DARK);
//                }

                map_pixels[index] = getFuzzyColor(correctedDist);
            }
        }

        // MAP PACKET
        MapDataPacket mapDataPacket = new MapDataPacket(0, (byte) 4, true, false, List.of(),
            new MapDataPacket.ColorContent((byte)128, (byte)128, (byte)0, (byte)0, map_pixels));
        Main.player.sendPacket(mapDataPacket);
    }

    private double scan(Pos origin) {
        final double stepSize = MAX_CAMERA_DISTANCE/1000;

        Vec direction = origin.direction();

        for (double dist = 0; dist < MAX_CAMERA_DISTANCE; dist += stepSize) {
            Pos checkPos = origin.add(direction.mul(dist));

            Block block = oceanInstance.getBlock(checkPos);

            if (block.isSolid()) {
                return dist;
            }
        }
        return -1;
    }
}
