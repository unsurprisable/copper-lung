package org.example;

import net.minestom.server.coordinate.Pos;

public class SpriteObject {
    public final Pos position;
    public final SpriteTexture texture;
    public final double width;
    public final double verticalOffset;

    public SpriteObject(Pos position, SpriteTexture texture) {
        this(position, texture, 1.0, 0.0);
    }

    public SpriteObject(Pos position, SpriteTexture texture, double width, double verticalOffset) {
        this.position = position;
        this.texture = texture;
        this.width = width;
        this.verticalOffset = verticalOffset;
    }
}
