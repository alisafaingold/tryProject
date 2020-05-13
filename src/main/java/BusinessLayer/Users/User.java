package BusinessLayer.Users;

public abstract class User {
    private String _id;

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_id() {
        return _id;
    }
}
