package BusinessLayer.SeasonPolicies;


public class ScoreComputingPolicy1 implements ScoreComputingPolicy {
    String name ="1";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getWinPoints() {
        return 3;
    }

    @Override
    public int getTiePoints() {
        return 1;
    }

    @Override
    public int getLosePoints() {
        return 0;
    }
}
