package BusinessLayer.Users;

import BusinessLayer.Enum.RefereeTraining;

public class Referee extends SignedUser {

    private int id;
    private RefereeTraining refereeTraining;

    // ====== Constructor ============
    public Referee(String userName, String hashPassword, int id, String fName, String lName, String email, RefereeTraining refereeTraining) {
        super(email, hashPassword, fName, lName, email);
        this.id = id;
        this.email = email;
        this.refereeTraining = refereeTraining;
    }

    // ======== Getters and Setters ============

    public RefereeTraining getRefereeTraining() {
        return refereeTraining;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    // ============ to String ===========
    @Override
    public String toString() {
        String string = super.toString();
        string += refereeTraining.toString();
        return string;
    }

    @Override
    //TODO save the data
    public boolean deleteUser() throws Exception {
//        long today = System.currentTimeMillis();
//        for (RefereeRole role : games.keySet()) {
//            for (Game game : games.get(role)) {
//                if (game.getGameDate() >= today) {
//                        SystemController.logger.error("Deletion | Can't Delete User; User ID: " + this.get_id());
//                        return false;
//
//                }
//            }
//        }
//        SystemController.removeUserFromActiveList(userName);
//        seasons.forEach(season -> season.removeReferee(refereeTraining, this));
//        this.changeStatus(UserStatus.NotActive);
        return true;
    }
}
