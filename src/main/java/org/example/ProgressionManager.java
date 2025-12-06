package org.example;

import net.kyori.adventure.text.Component;

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
    // --- END PROGRESSION FLAGS ---

    public void objectiveCollected() {
        objectiveAmount++;

        switch (objectiveAmount) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                SoundManager.play(SoundManager.DARK_BRAMBLE);
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                break;
            case 10:
                break;
        }
    }

    public void onSubmarinePositionChange(double mapX, double mapY, double yaw) {
        if (mapX > 375 && !hasEnteredLargeCavern) {
            SoundManager.play(SoundManager.METAL_BANG);
            hasEnteredLargeCavern = true;
        }
    }
}
