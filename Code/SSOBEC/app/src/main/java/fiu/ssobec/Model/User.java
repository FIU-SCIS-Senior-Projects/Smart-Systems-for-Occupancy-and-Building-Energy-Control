package fiu.ssobec.Model;

/**
 * Created by Dalaidis on 2/7/2015.
 */
public class User {

    private int id;
    private String name;
    private String email;
    private int loggedIn;

    public User(String name, int id, String email, int loggedIn) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.loggedIn = loggedIn;
    }

    public int getLoggedIn() { return loggedIn; }

    public void setLoggedIn(int loggedIn) {
        this.loggedIn = loggedIn;
    }


    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
