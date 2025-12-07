package org.example;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.timer.TaskSchedule;

public class ProgressionManager {

    public static ProgressionManager Instance;
    private int objectiveAmount;

    public ProgressionManager() {
        if (Instance == null) {
            Instance = this;
        }
    }

    // ----- PROGRESSION FLAGS -----
    private boolean hasEnteredLargeCavern;
    private boolean scalyHasPassedFirst;
    private boolean hasLeftObjective6;
    private boolean hasLeftObjective8;

    private boolean scriptedGrowlOne;
    private boolean scriptedGrowlTwo;
    private boolean scriptedGrowlThree;
    private boolean scriptedGrowlFour;
    // --- END PROGRESSION FLAGS ---

    // ----- COMMUNICATION FLAGS -----
    public boolean shouldPrintCornerFish;
    public boolean shouldPrintScalyEye;
    // --- END COMMUNICATION FLAGS ---


    // NOTE: these trigger right when the photo button is pressed, not when it's finished printing
    public void objectiveCollected(MapObjective objective) {
        objectiveAmount++;

        if (objective == MapManager.Instance.OBJECTIVE_3) {
            ScalySoundManager.Instance.setAggressionLevel(0);
            ScalySoundManager.Instance.setEnabled(true);
        }

        if (objective == MapManager.Instance.OBJECTIVE_4) {
            SoundManager.play(SoundManager.DARK_BRAMBLE);
            ScalySoundManager.Instance.setAggressionLevel(1);
            ScalySoundManager.Instance.setEnabled(true);
            shouldPrintCornerFish = true;
        }
    }

    public void onSubmarinePositionChange(Submarine submarine, double mapX, double mapY, double yaw) {
        if (!scriptedGrowlOne && mapX >= 281 && mapY <= 196) {
            scriptedGrowlOne = true;
            SoundManager.playTemporary(SoundManager.SCALY_GROWL_1, 15, 90, 25);
        }

        if (!scriptedGrowlTwo && MapManager.Instance.OBJECTIVE_2.getIsCompleted() && mapX <= 319 && mapY >= 285) {
            scriptedGrowlTwo = true;
            SoundManager.playTemporary(SoundManager.SCALY_GROWL_2, 15, 120, 25);
        }

        if (!scriptedGrowlThree && MapManager.Instance.OBJECTIVE_2.getIsCompleted() && mapX <= 288 && mapY >= 305) {
            scriptedGrowlThree = true;
            SoundManager.playTemporary(SoundManager.SCALY_GROWL_4, 15, 45, 8);
        }

        if (!scriptedGrowlFour && MapManager.Instance.OBJECTIVE_2.getIsCompleted() && mapX <= 265 && mapY >= 335) {
            scriptedGrowlFour = true;
            SoundManager.playTemporary(SoundManager.SCALY_GROWL_1, 15, 25, 25);
            MinecraftServer.getSchedulerManager().buildTask(
                () -> SoundManager.play(SoundManager.OXYGEN_NOTIFICATION, new Vec(1.01, 2.5, -3.01)))
                .delay(TaskSchedule.tick(100)).schedule();
        }

        if (!hasEnteredLargeCavern && mapX >= 351) {
            hasEnteredLargeCavern = true;
            SoundManager.play(SoundManager.THREATENING_AMBIENCE);
            ScalySoundManager.Instance.setEnabled(false);
        }

        if (!scalyHasPassedFirst && mapX >= 424) {
            scalyHasPassedFirst = true;
            SoundManager.playTemporary(SoundManager.SCALY_PASS, 8, 90, 5);
            CollisionScanner.Instance.getCollisionScannerDirection(1).setForceDetecting();
            MinecraftServer.getSchedulerManager().buildTask(
                () -> CollisionScanner.Instance.getCollisionScannerDirection(1).stopForceDetecting()
                ).delay(TaskSchedule.tick(65)).schedule();
        }

        if (!hasLeftObjective6 && MapManager.Instance.OBJECTIVE_6.getIsCompleted() && mapX <= 792) {
            hasLeftObjective6 = true;
            submarine.teleport(513.74, 355.60, 252.52);
        }

        if (!hasLeftObjective8 && MapManager.Instance.OBJECTIVE_8.getIsCompleted() && mapY >= 686) {
            hasLeftObjective8 = true;
            submarine.teleport(201.27, 720.04, 334.60);
        }
    }
}
