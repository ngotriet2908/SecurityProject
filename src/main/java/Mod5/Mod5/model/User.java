package Mod5.Mod5.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

@XmlRootElement
public class User {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    private Integer isPremium;
    private Date startDate;
    private Date endDate;

    // total stats
    private Integer totalRuns;
    private Integer totalTime;
    private Integer totalDistance;
    private Integer totalSteps;


    public User(String username, String password, String firstName, String lastName, String email) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.firstName = null;
        this.lastName = null;
        this.email = null;
    }

    public User() {
        this.username = null;
        this.password = null;
        this.firstName = null;
        this.lastName = null;
        this.email = null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
