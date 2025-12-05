package org.example;

public class UnitConvert {
    public static double fromMapXToWorldZ(double mapX) {
        return mapX / 4.0 + 5;
    }
    public static double fromMapYToWorldX(double mapY) {
        return mapY / 4.0 + 4;
    }
    public static double fromWorldXToMapY(double worldX) {
        return (worldX - 4) * 4.0;
    }
    public static double fromWorldZToMapX(double worldZ) {
        return (worldZ - 5) * 4.0;
    }
}
