package org.example;

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
    private boolean hasLeftObjective6;
    private boolean hasLeftObjective8;
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

    public void onSubmarinePositionChange(Submarine submarine, double mapX, double mapY, double yaw) {

        if (!hasLeftObjective6 && MapManager.Instance.OBJECTIVE_6.getIsCompleted() && mapX <= 790) {
            hasLeftObjective6 = true;
            submarine.teleport(513.74, 355.60, 252.52);
        }

        if (!hasLeftObjective8 && MapManager.Instance.OBJECTIVE_8.getIsCompleted() && mapY >= 680) {
            hasLeftObjective8 = true;
            submarine.teleport(201.27, 720.04, 334.60);
        }
    }
}
