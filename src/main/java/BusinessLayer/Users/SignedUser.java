package BusinessLayer.Users;


import BusinessLayer.Enum.UserStatus;

public abstract class SignedUser extends User {
    protected String userName;
    protected String password;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected UserStatus status;


    public SignedUser(String username, String password, String firstName, String lastName, String email) {
        this.userName = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    //========== Getters and Setters ================

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return userName;
    }

    public UserStatus getStatus() {
        return status;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }



    public void changeStatus(UserStatus status) {
        this.status = status;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    //========== Abstract Functions ================

    public abstract boolean deleteUser() throws Exception;

    public String toString(){
        String string = lastName +" " + firstName;
        return string;
    }
}
