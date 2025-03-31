package PasswordManagerProject.model;

public class Account {
    private String username;
    private String password;
    private String notes;
    private String website;

    public Account(String username, String password, String notes, String website) {
        this.username = username;
        this.password = password;
        this.notes = notes;
        this.website = website;
    }

    // Getters
    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public String getNotes() {return notes;}
    public String getWebsite() {return website;}

    // Setters
    public void setUsername(String username) {this.username = username;}
    public void setPassword(String password) {this.password = password;}
    public void setNotes(String notes) {this.notes = notes;}
    public void setWebsite(String website) {this.website = website;}
}
