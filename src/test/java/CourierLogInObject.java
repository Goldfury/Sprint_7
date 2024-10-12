public class CourierLogInObject {
    private String login;
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(final String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public CourierLogInObject(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
