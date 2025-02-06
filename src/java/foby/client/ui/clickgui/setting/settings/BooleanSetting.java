package foby.client.ui.clickgui.setting.settings;

import foby.client.ui.clickgui.setting.Setting;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
public class BooleanSetting extends Setting {
    private boolean value;

    public BooleanSetting(String name, String desc, boolean value) {
        this.setName(name);
        this.setDesc(desc);
        this.value = value;
        this.setVisibility(() -> true);
    }

    public boolean isValue() {
        return value;
    }

    public boolean get() {
        return this.value;
    }
}

