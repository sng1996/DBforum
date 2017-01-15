package ru.mail.park.User;

/**
 * Created by sergeigavrilko on 04.11.16.
 */
public class UpdateUser {
    private String about;
    private String user;
    private String name;


    public UpdateUser() {
    }

    public UpdateUser(String about, String user, String name) {
        this.about = about;
        this.user = user;
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public String getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setName(String name) {
        this.name = name;
    }
}
