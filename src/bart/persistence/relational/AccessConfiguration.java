package bart.persistence.relational;

public class AccessConfiguration implements Cloneable {

    private String driver;
    private String uri;
    private String login;
    private String password;
    private String schemaName = "public";

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getDatabaseName() {
        if (uri.lastIndexOf("/") != -1) {
            return uri.substring(uri.lastIndexOf("/") + 1);
        }
        return uri.substring(uri.lastIndexOf(":") + 1);
    }

    public void setDatabaseName(String name) {
        if (uri.lastIndexOf("/") != -1) {
            uri = uri.substring(0, uri.lastIndexOf("/")) + name;
        } else {
            uri = uri.substring(0, uri.lastIndexOf(":")) + name;
        }
    }

    @Override
    public AccessConfiguration clone() {
        try {
            return (AccessConfiguration) super.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public String toString() {
        return "Access Configuration\n"
                + "----------------------\n"
                + "Driver: " + this.driver + "\n"
                + "URI: " + this.uri + "\n"
                + "Schema: " + this.schemaName + "\n"
                + "Login: " + this.login + "\n"
                + "Password: " + this.password + "\n";
    }
}
