package org.example;

import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.util.Random;

public class Screenshake {
    private int count = 0;
    private Task rumbleTask;

    public Screenshake(int ticks) {
        Main.player.setFieldViewModifier(2f);
        Random rand = new Random();
        rumbleTask = MinecraftServer.getSchedulerManager().buildTask(() -> {
            Main.player.setFieldViewModifier(rand.nextFloat(0.1f, 1f));
            if (count == ticks) {
                Main.player.setFieldViewModifier(0.1f);
                this.rumbleTask.cancel();
            }
            count++;
        }).repeat(TaskSchedule.nextTick()).schedule();
    }
}
