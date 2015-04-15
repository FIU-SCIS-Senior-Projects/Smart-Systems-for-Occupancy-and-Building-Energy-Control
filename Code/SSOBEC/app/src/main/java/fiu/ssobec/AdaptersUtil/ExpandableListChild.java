package fiu.ssobec.AdaptersUtil;

/**
 * Created by Dalaidis on 3/20/2015.
 */
public class ExpandableListChild {

    private String name;
    private String text1;
    private String text2;
    private String appliance_calc_res;

    public String getTotal_res() {
        return total_res;
    }

    public void setTotal_res(String total_res) {
        this.total_res = total_res;
    }

    private String total_res;
    private boolean checked;

    public String getAppliance_calc_res() {
        return appliance_calc_res;
    }

    public void setAppliance_calc_res(String appliance_calc_res) {
        this.appliance_calc_res = appliance_calc_res;
    }

    private int editTextChildNumField;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getText1()
    {
        return text1;
    }

    public void setText1(String text1)
    {
        this.text1 = text1;
    }

    public String getText2()
    {
        return text2;
    }

    public void setText2(String text2)
    {
        this.text2 = text2;
    }

    public boolean isChecked()
    {
        return checked;
    }

    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }

    public int getEditTextChildNumField() {
        return editTextChildNumField;
    }

    public void setEditTextChildNumField(int editTextChildNumField) {
        this.editTextChildNumField = editTextChildNumField;
    }
}
