package foby.client.ui.clickgui.setting;

import java.util.function.Supplier;

public class Setting {
    public String name;
    public String desc;
    private Supplier<Boolean> visibility;

    public void setVisibility(Supplier<Boolean> visibility) {
        this.visibility = visibility;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public Supplier<Boolean> getVisibility() {
        return visibility;
    }
}
