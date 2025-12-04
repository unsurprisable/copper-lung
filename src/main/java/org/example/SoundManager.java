package org.example;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.instance.InstanceTickEvent;

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
}
