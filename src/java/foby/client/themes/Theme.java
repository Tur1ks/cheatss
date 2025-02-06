package foby.client.themes;

import foby.client.utils.color.ColorUtils;
import lombok.Getter;

public class Theme {
    @Getter
    public String name;
    public int[] colors;

    public Theme(String name, int mainColor, int textColor, int accentColor, int backgroundColor) {
        this.name = name;
        this.colors = new int[]{mainColor, textColor, accentColor, backgroundColor};
    }

    public int getColor(int index) {
        return ColorUtils.gradient(25, index, colors);
    }

    public int getColorLowSpeed(int index) {
        return ColorUtils.gradient(50, index, colors);
    }
}
