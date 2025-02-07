package foby.client.ui.clickgui.setting.settings;

import foby.client.ui.clickgui.setting.Setting;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class NumberSetting extends Setting {
    private double value;
    private double minimum;
    private double maximum;
    private double increment;

    public NumberSetting(String name, String desc, double defaultValue, double minimum, double maximum, double increment) {
        this.setName(name);
        this.setDesc(desc);
        this.value = defaultValue;
        this.minimum = minimum;
        this.maximum = maximum;
        this.increment = increment;
        this.setVisibility(() -> true);
    }

    public void increment(boolean positive) {
        setValue(getValue() + (positive ? 1 : -1) * increment);
    }

    public void setValue(double value) {
        this.value = Math.max(minimum, Math.min(maximum, Math.round(value * (1.0 / increment)) / (1.0 / increment)));
    }

    public double getValue() {
        return value;
    }

    public int getIntValue() {
        return (int) value;
    }

    public float getFloatValue() {
        return (float) value;
    }

    public double getMinimum() {
        return minimum;
    }

    public double getMaximum() {
        return maximum;
    }
}