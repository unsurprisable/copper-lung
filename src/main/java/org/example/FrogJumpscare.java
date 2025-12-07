package org.example;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;

public class FrogJumpscare {

    private final Entity display;
    private final Vec spawnPos = new Vec(0.5, 3.25, 4);
    private final Vec endPos = new Vec(0.5, 3, -3);
    public static final int TIME_TO_MOVE = 30;

    public FrogJumpscare() {
        display = new Entity(EntityType.ITEM_DISPLAY);
        ItemDisplayMeta meta = (ItemDisplayMeta) display.getEntityMeta();

        meta.setItemStack(ItemStack.of(Material.GLASS_PANE));
        meta.setScale(new Vec(3.25, 3.25, 1));
        meta.setHasNoGravity(true);
        meta.setBrightness(11, 0);
        meta.setPosRotInterpolationDuration(TIME_TO_MOVE);

        display.setInstance(Cockpit.Instance.getInstance(), spawnPos);
        SoundManager.play(SoundManager.FROG_JUMPSCARE, spawnPos);
        new Screenshake(5, 0.1f, 0.09f, 0.11f);
        Main.player.addEffect(new Potion(PotionEffect.BLINDNESS, 255, 30));
        Main.player.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.075f);
        CollisionScanner.Instance.getCollisionScannerDirection(0).setForceDetecting();
        CollisionScanner.Instance.getCollisionScannerDirection(1).setForceDetecting();
        CollisionScanner.Instance.getCollisionScannerDirection(2).setForceDetecting();
        CollisionScanner.Instance.getCollisionScannerDirection(3).setForceDetecting();

        Cockpit.Instance.getInstance().eventNode().addListener(InstanceTickEvent.class, event -> {
           display.teleport(endPos.asPos());
        });
    }
}
