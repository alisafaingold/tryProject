package BusinessLayer.Enum;

public enum RefereeTraining {
    Expert (1),
    Medium (2),
    Begginer(3);

    private int numTraining;

    RefereeTraining(int numVal) {
        this.numTraining = numVal;
    }

    public int getNumVal() {
        return numTraining;
    }

}
