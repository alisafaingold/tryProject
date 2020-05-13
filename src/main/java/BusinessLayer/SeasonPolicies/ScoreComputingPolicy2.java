package BusinessLayer.SeasonPolicies;


public class ScoreComputingPolicy2 implements ScoreComputingPolicy {
    String name ="2";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getWinPoints() {
        return 0;
    }

    @Override
    public int getTiePoints() {
        return 0;
    }

    @Override
    public int getLosePoints() {
        return 0;
    }
}
