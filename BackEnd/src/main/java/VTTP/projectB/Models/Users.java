package VTTP.projectB.Models;

public class Users {
    private String email;
    private String password;

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    @Override
    public String toString() {
        return "Users [email=" + email + ", password=" + password + "]";
    }

    

    
}
