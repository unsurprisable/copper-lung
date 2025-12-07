package org.example;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.block.Block;
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
    private boolean largeCavernSteam;
    private boolean hasTriggeredCavernMusic;
    private boolean objectiveSixSteam;
    private boolean objectiveSixTeleport;
    private boolean hasLeftObjective8;
    private boolean objectiveEightSteam;
    private boolean secondOxygenNotification;
    private boolean thirdOxygenNotification;
    private boolean hasEnteredSecondCavern;

    private boolean scriptedGrowlOne;
    private boolean scriptedGrowlTwo;
    private boolean scriptedGrowlThree;
    private boolean scriptedGrowlFour;
    private boolean scalyHasPassedFirst;
    private boolean scalyTriesAttack;
    private boolean scalyAttacksNextPhoto;
    private boolean scalyIsPhotographed;
    private boolean bloodStartedRising;
    private boolean triggeredFrogJumpscareZone;
    private boolean frogIsJumpscaring;
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

        if (!largeCavernSteam && mapX >= 368) {
            largeCavernSteam = true;
            new PipeSteam(
                new Vec(1.4, 2.35, 0.4),
                new Vec(-3, 0.5, -4).normalize()
            );
        }

        if (!hasTriggeredCavernMusic && mapX >= 412) {
            hasTriggeredCavernMusic = true;
            SoundManager.play(SoundManager.THREATENING_AMBIENCE);
            ScalySoundManager.Instance.setEnabled(false);
        }

        if (!scalyHasPassedFirst && mapX >= 479) {
            scalyHasPassedFirst = true;
            SoundManager.playTemporary(SoundManager.SCALY_PASS, 8, 90, 5);
            CollisionScanner.Instance.getCollisionScannerDirection(1).setForceDetecting();
            MinecraftServer.getSchedulerManager().buildTask(
                () -> CollisionScanner.Instance.getCollisionScannerDirection(1).stopForceDetecting()
                ).delay(TaskSchedule.tick(65)).schedule();
        }

        if (!objectiveSixSteam && MapManager.Instance.OBJECTIVE_6.getIsCompleted() && mapX <= 792) {
            objectiveSixSteam = true;
            new PipeSteam(
                new Vec(-0.4, 2.2, -2.4),
                new Vec(0.75, -0.8, -0.25).normalize()
            );
        }

        if (!objectiveSixTeleport && MapManager.Instance.OBJECTIVE_6.getIsCompleted() && mapX <= 747) {
            objectiveSixTeleport = true;
            submarine.teleport(513.74, 355.60, yaw);
        }

        if (!secondOxygenNotification && mapX <= 412 && mapY > 500) {
            secondOxygenNotification = true;
            SoundManager.play(SoundManager.OXYGEN_NOTIFICATION);
        }

        if (!hasEnteredSecondCavern && mapY > 650) {
            hasEnteredSecondCavern = true;
            ScalySoundManager.Instance.setAggressionLevel(2);
        }

        if (!objectiveEightSteam && mapX < 150 && mapY <= 681 && mapY > 600) {
            objectiveEightSteam = true;
            new PipeSteam(
                new Vec(-0.4, 2.85, 2.4),
                new Vec(4, -8, -3).normalize()
            );
        }

        if (!hasLeftObjective8 && MapManager.Instance.OBJECTIVE_8.getIsCompleted() && mapY >= 686) {
            hasLeftObjective8 = true;
            submarine.teleport(198.27, 674.04, yaw);
        }

        if (!scalyTriesAttack && mapX >= 436 && mapY > 650) {
            scalyTriesAttack = true;
            scalyAttacksNextPhoto = true;
            CollisionScanner.Instance.getCollisionScannerDirection(0).setForceDetecting();
        }

        if (!thirdOxygenNotification && mapX >= 496 && mapY > 650) {
            thirdOxygenNotification = true;
            SoundManager.play(SoundManager.OXYGEN_NOTIFICATION);
        }

        if (!bloodStartedRising && mapX > 500 && mapY >= 740) {
            bloodStartedRising = true;
            new RisingBlood();
        }

        if (!triggeredFrogJumpscareZone && mapX >= 720 && mapY >= 831) {
            triggeredFrogJumpscareZone = true;
            Cockpit.Instance.getInstance().setBlock(0, 1, 0, Block.BARRIER);
            if (Main.player.getPosition().z() > -0.2) {
                Main.player.teleport(Main.player.getPosition().withZ(-0.5));
            }
            MinecraftServer.getGlobalEventHandler().addListener(PlayerMoveEvent.class, event -> {
                if (event.getPlayer() != Main.player) {
                    System.out.println("wtf");
                    return;
                }
                Pos newPos = event.getNewPosition();
                if (!frogIsJumpscaring && Math.abs(newPos.yaw()) < 30) {
                    frogIsJumpscaring = true;
                    new FrogJumpscare();
                    MinecraftServer.getSchedulerManager().buildTask(() -> {
                        Main.player.kick(Component.text("COPPER LUNG").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
                        MinecraftServer.stopCleanly();
                    }).delay(TaskSchedule.tick(23)).schedule();
                }
            });
        }
    }



    public void onPrePhotoTaken(double mapX, double mapY, double yaw) {

        if (scalyAttacksNextPhoto && (yaw <= 45 || yaw >= 315)) {
            shouldPrintScalyEye = true;
            scalyIsPhotographed = true;
        }

    }



    public void onPostPhotoTaken(double mapX, double mapY, double yaw) {

        if (scalyAttacksNextPhoto && scalyIsPhotographed) {
            scalyAttacksNextPhoto = false;
            SoundManager.play(SoundManager.SCALY_ATTACK, new Vec(0.5, 1.5, -3.5));
            SoundManager.play(SoundManager.SCALY_POST_ATTACK);
            MinecraftServer.getSchedulerManager().buildTask(() -> {
                new Screenshake(10, 30f, 0.1f, 3f);
                CollisionScanner.Instance.getCollisionScannerDirection(0).stopForceDetecting();
                Submarine.Instance.getCamera().clearPrintingTask();
                Submarine.Instance.getCamera().disableAndClearCameraMap();
            }).delay(TaskSchedule.tick(40)).schedule();
        }

    }
}
