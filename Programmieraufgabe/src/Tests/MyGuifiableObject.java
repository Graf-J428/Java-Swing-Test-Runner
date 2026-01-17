package Tests;

import turban.utils.IGuifiable;

import java.awt.*;

public class MyGuifiableObject implements IGuifiable {
    private String str;
    private String status;
    private String output;

    public MyGuifiableObject(String str) {
        this.str = str;
        this.status = "ungetestet";
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toGuiString() {
        return str;
    }

    @Override
    public Image getGuiIcon() {
        return null;
    }
}
