package foby.client.themes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThemesUtil {
    public List<Theme> themes = new ArrayList<>();
    private Theme currentTheme = null;

    public void init() {
        themes.addAll(Arrays.asList(
                new Theme("Тема", HexColor.toColor("#1E90FF"), HexColor.toColor("#000000"))

                )
        );
        currentTheme = themes.get(1);
    }
    public void setCurrentStyle(Theme theme) {
        currentTheme = theme;
    }


    public Theme getCurrentStyle() {
        return currentTheme;
    }

    public int setCurrentThemeByName(String name) {
        for (Theme theme : themes) {
            if (theme.getName().equalsIgnoreCase(name)) {
                setCurrentStyle(theme);
            }
        }
        return 0;
    }
}
