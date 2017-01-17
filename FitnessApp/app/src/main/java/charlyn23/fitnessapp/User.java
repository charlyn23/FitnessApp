package charlyn23.fitnessapp;

/**
 * Created by charlynbuchanan on 1/1/17.
 */

public class User {
    String userName;
    String password;
    String email;
    String sex;

    public User() {

    }

    //User sex defaults to female unless user specifies
    public User (String name, String password) {
        this.userName = name;
        this.password = password;
        this.sex = "female";
    }


    public User (String name, String password, String sex) {
        this.userName = name;
        this.password = password;
        this.sex = sex;
    }



    public void setUserName(String userName) {
        this.userName =userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword(String userName) {
        return password;
    }

}
