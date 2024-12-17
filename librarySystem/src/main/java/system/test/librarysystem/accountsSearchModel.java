package system.test.librarysystem;

public class accountsSearchModel {

    Integer ID;
    String username, account_type;

    public accountsSearchModel(Integer ID, String username, String account_type) {
        this.ID = ID;
        this.username = username;
        this.account_type = account_type;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }
}
