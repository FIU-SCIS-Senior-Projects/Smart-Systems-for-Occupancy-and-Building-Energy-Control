package fiu.ssobec.Model;

/**
 * Created by Dalaidis on 2/7/2015.
 * Modified by Diana on 5/20/2015   Added usertype
 */
public class User {

    private int id;
    private String name;
    private String email;
    private int loggedIn;
    private String usertype;
    private int rewards;

    public User(String name, int id, String email, int loggedIn, String usertype, int rewards) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.loggedIn = loggedIn;
        this.usertype = usertype;
        this.rewards = rewards;
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

    public String getUsertype() { return usertype; }

    public void setUsertype(String usertype) { this.usertype = usertype; }

    public int getRewards() { return rewards; }

    public void setRewards(int rewards) { this.rewards = rewards; }

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
