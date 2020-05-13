package BusinessLayer.Users;

import ServiceLayer.Controllers.ComplaintSystemController;
import DB.SystemController;
import BusinessLayer.Football.Game;
import BusinessLayer.SystemFeatures.Complaint;
import BusinessLayer.SystemFeatures.PersonalPage;

import java.util.HashMap;
import java.util.HashSet;

public class Fan extends SignedUser {
    private long signedUpDate;
    private HashMap<Long, String> mySearches;

//    private HashSet<PersonalPage> followedPersonalPages;
//    private HashSet<Complaint> myComplaints;
//    private HashSet<Game> observedGames;


    public Fan(String username, String password, String firstName, String lastName, String email) {
        super(username, password, firstName, lastName, email);
        signedUpDate = System.currentTimeMillis();
        mySearches = new HashMap<>();
    }

    //========== Get Notify ================
    public void update() {
        //TODO
    }

    public boolean addToMySearches(Long date, String search) {
        mySearches.put(date, search);
        return true;

    }

    //========== Getters and Setters ================
    public long getSignedUpDate() {
        return signedUpDate;
    }

    public HashMap<Long, String> getMySearches() {
        return mySearches;
    }

    //========== Delete ================
    @Override
    public boolean deleteUser() {
//        for (PersonalPage followedPersonalPage : followedPersonalPages) {
//            followedPersonalPage.removeFans(this);
//        }
//        for (Game observedGame : observedGames) {
//            observedGame.removeObserver(this);
//        }
//
//        for (Complaint myComplaint : myComplaints) {
//            ComplaintSystemController.moveToArchive(myComplaint);
//        }
//        SystemController.archiveUsers.put(this.getUserName(),this);
//        SystemController.userNameUser.remove(this);
        return true;
    }


}
