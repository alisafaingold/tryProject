package BusinessLayer.Users;

public class AssociationRepresentative extends SignedUser {

    public AssociationRepresentative(String username, String password, String fName, String lName, String email) {
        super(username, password, fName, lName, email);
    }

    @Override
    public boolean deleteUser() {
        return true;
    }
}
