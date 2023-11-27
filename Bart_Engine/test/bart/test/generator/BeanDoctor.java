package bart.test.generator;

import speedy.model.database.NullValue;

public class BeanDoctor {

    //dnpi,dname,dspec,dhospital,dconf
    private String dnpi;
    private String dname;
    private String dspec;
    private String dhospital;
    private NullValue dconf;

    public String getDnpi() {
        return dnpi;
    }

    public void setDnpi(String dnpi) {
        this.dnpi = dnpi;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getDspec() {
        return dspec;
    }

    public void setDspec(String dspec) {
        this.dspec = dspec;
    }

    public String getDhospital() {
        return dhospital;
    }

    public void setDhospital(String dhospital) {
        this.dhospital = dhospital;
    }

    public NullValue getDconf() {
        return dconf;
    }

    public void setDconf(NullValue dconf) {
        this.dconf = dconf;
    }

    @Override
    public String toString() {
        return dnpi + "," + dname + "," + dspec + "," + dhospital + "," + dconf;
    }

}
