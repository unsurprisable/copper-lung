package org.example;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.instance.InstanceContainer;

import java.util.ArrayList;

public class SoundManager {

    public static SoundManager Instance;

    private final SubmarineMoveSFX subMoveSFX;
    private final ArrayList<MovingSound> movingSounds = new ArrayList<>();
    private final ArrayList<LoopingSound> loopingSounds = new ArrayList<>();

    public SoundManager() {
        if (Instance == null) {
            Instance = this;
        }

        InstanceContainer instance = Cockpit.Instance.getInstance();

        subMoveSFX = new SubmarineMoveSFX(instance);

        movingSounds.add(new MovingSound(
            instance, ARTERIES, 22.0,
            new Vec(110.5, 0, 54.5),
            16
        ));
        movingSounds.add(new MovingSound(
            instance, VOID, 35,
            new Vec(160.5, 0, 32.5),
            34.5
        ));

        loopingSounds.add(new LoopingSound(
            AMBIENT_NOISE, 39.8, true
        ));

        Cockpit.Instance.getInstance().eventNode().addListener(InstanceTickEvent.class, event -> {
            subMoveSFX.update(event.getDuration());
            movingSounds.forEach(movingSound -> movingSound.update(event.getDuration()));
            loopingSounds.forEach(loopingSound -> loopingSound.update(event.getDuration()));
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
        0.18f, 1.15f
    );
    public static Sound TAKE_PHOTO = Sound.sound(
        Key.key("custom:take_photo"),
        Sound.Source.MASTER,
        1.0f,
        1.0f
    );
    public static Sound SUCCESSFUL_PHOTOGRAPH = Sound.sound(
        Key.key("custom:successful_photograph"),
        Sound.Source.MASTER,
        0.13f, 0.8f
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
    public static Sound ARTERIES = Sound.sound(
        Key.key("custom:arteries"),
        Sound.Source.MASTER,
        1.0f, 1.0f
    );
    public static Sound VOID = Sound.sound(
        Key.key("custom:void"),
        Sound.Source.MASTER,
        1.0f, 1.0f
    );
    public static Sound AMBIENT_NOISE = Sound.sound(
        Key.key("custom:ambient_noise"),
        Sound.Source.MASTER,
        2.0f, 1.0f
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

    public static void stop(Sound sound) {
        if (Main.player == null) return;
        Main.player.stopSound(sound);
    }
}
