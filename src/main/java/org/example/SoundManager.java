package org.example;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.sound.SoundEvent;

public class SoundManager {

    public static SoundManager Instance;

    private final SubmarineMoveSFX subMoveSFX;

    public SoundManager() {
        if (Instance == null) {
            Instance = this;
        }

        subMoveSFX = new SubmarineMoveSFX(Cockpit.Instance.getInstance());

        MinecraftServer.getGlobalEventHandler().addListener(InstanceTickEvent.class, event -> {
            subMoveSFX.update(event.getDuration());
        });
    }


    // --- SOUNDTRACKS ---
    public static Sound DARK_BRAMBLE = Sound.sound(
        Key.key("custom:dark_bramble"),
        Sound.Source.MASTER,
        1.0f, 1.0f
    );


    // --- SOUND EFFECTS ---
    public static Sound OPEN_MAP = Sound.sound(
        Key.key("custom:open_map"),
        Sound.Source.MASTER,
        0.25f, 1.15f
    );
    public static Sound TAKE_PHOTO = Sound.sound(
        Key.key("custom:take_photo"),
        Sound.Source.MASTER,
        1.5f,
        1.0f
    );
    public static Sound SUCCESSFUL_PHOTOGRAPH = Sound.sound(
        Key.key("custom:successful_photograph"),
        Sound.Source.MASTER,
        0.15f, 0.8f
    );
    public static Sound TELEPORT = Sound.sound(
        Key.key("custom:teleport"),
        Sound.Source.MASTER,
        1.0f, 1.0f
    );
    public static Sound METAL_BANG = Sound.sound(
        Key.key("custom:metal_bang"),
        Sound.Source.MASTER,
        1.0f, 1.0f
    );
    public static Sound PROXIMITY = Sound.sound(
        Key.key("custom:proximity"),
        Sound.Source.MASTER,
        0.9f, 1.0f
    );


    public static void play(Sound sound) {
        if (Main.player == null) return;
        Main.player.playSound(sound);
    }
    public static void play(Sound sound, Point point) {
        if (Main.player == null) return;
        Main.player.playSound(sound, point);
    }
    public static void play(Sound sound, Sound.Emitter emitter) {
        if (Main.player == null) return;
        Main.player.playSound(sound, emitter);
    }
}
