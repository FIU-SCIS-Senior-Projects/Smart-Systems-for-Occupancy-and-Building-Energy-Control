package fiu.ssobec;

/**
 * Created by Dalaidis on 3/20/2015.
 */
public class Child {

    private String name;
    private String text1;
    private String text2;
    private boolean checked;
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
