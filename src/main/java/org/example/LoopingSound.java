package org.example;

import net.kyori.adventure.sound.Sound;

public class LoopingSound {
    private final Sound sound;
    private Sound.Emitter emitter;
    private final double duration;
    private double durationLeft = 0;
    private boolean isPlaying;

    public LoopingSound(Sound sound, double duration, boolean startPlaying) {
        this.sound = sound;
        this.duration = duration;
        this.isPlaying = startPlaying;
    }

    public void update(int deltaTimeMillis) {
        if (!isPlaying) return;

        double deltaTime = deltaTimeMillis / 1000.0;
        durationLeft -= deltaTime;
        if (durationLeft <= 0) {
            playSound();
            durationLeft = duration;
        }
    }

    private void playSound() {
        if (emitter != null) {
            SoundManager.play(sound, emitter);
        } else {
            SoundManager.play(sound);
        }
    }

    public void setEmitter(Sound.Emitter emitter) {
        this.emitter = emitter;
    }

    public void play() {
        isPlaying = true;
    }
    public void stop() {
        SoundManager.stop(sound);
        isPlaying = false;
        durationLeft = 0;
    }
}
