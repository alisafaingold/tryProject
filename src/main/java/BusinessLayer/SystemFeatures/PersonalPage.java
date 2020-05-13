package BusinessLayer.SystemFeatures;

import BusinessLayer.Users.Fan;
import BusinessLayer.Users.SignedUser;

import java.util.HashSet;

public abstract class PersonalPage {

    private String pageOwnerID;
    private long openDate;
    private HashSet<String> fansID = new HashSet<>();
    protected String pageName;
    private String _id;

    public PersonalPage(SignedUser user) {
        pageOwnerID = user.get_id();
        this.openDate= System.currentTimeMillis();

    }

    // ============== Fans For Page ==========
    public boolean removeFans(Fan fan) {
        if(fansID.remove(fan.get_id())){
            return true;
        }
        return false;
    }

    public boolean addFan(Fan fan) {
        if(fansID.add(fan.get_id()))
            return true;
        return false;
    }

    @Override
    public String toString() {
        return pageOwnerID.toString() +" "+pageName +" ";
    }

    // ======== Getters ============
    public String getPageOwnerID() {
        return pageOwnerID;
    }

    public long getOpenDate() {
        return openDate;
    }

    public HashSet<String> getFansID() {
        return fansID;
    }

    public String getPageName() {
        return pageName;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

}
