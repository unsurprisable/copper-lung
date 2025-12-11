package org.example;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.instance.InstanceTickEvent;

import java.util.Random;

public class RandomSoundManager {

    private final Vec leftRightOrigin = new Vec(2.5, 1.5, 0.5);
    private final double maxTime = 240.0;
    private final double minTime = 30.0;
    private double timeLeft = 85.0;

    public RandomSoundManager() {
        Random rand = new Random();

        Cockpit.Instance.getInstance().eventNode().addListener(InstanceTickEvent.class, event -> {
           double deltaTime = event.getDuration() / 1000.0;
           timeLeft -= deltaTime;
           if (timeLeft <= 0) {
               int dir = rand.nextInt(2) == 0 ? -1 : 1;
               SoundManager.play(SoundManager.SCRAPE, leftRightOrigin.withX(leftRightOrigin.x() * dir));
               timeLeft = rand.nextDouble(minTime, maxTime);
           }
        });
    }
}
