package fiu.ssobec.AdaptersUtil;

/**
 * Created by Maria on 4/4/2015.
 */
public class PlugLoadListParent {

    private String name;
    private String status;
    private String energy_consumed;
    private String appl_type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEnergy_consumed() {
        return energy_consumed;
    }

    public void setEnergy_consumed(String energy_consumed) {
        this.energy_consumed = energy_consumed;
    }

    public String getAppl_type() {
        return appl_type;
    }

    public void setAppl_type(String appl_type) {
        this.appl_type = appl_type;
    }

    @Override
    public String toString() {
        return "PlugLoadListParent{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", energy_consumed='" + energy_consumed + '\'' +
                '}';
    }
}
