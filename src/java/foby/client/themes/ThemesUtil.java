package foby.client.themes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThemesUtil {
    public List<Theme> themes = new ArrayList<>();
    private Theme currentTheme = null;

    public void init() {
        themes.addAll(Arrays.asList(
                new Theme("Оригинальная", HexColor.toColor("#1E90FF"), HexColor.toColor("#000000")),
                new Theme("Ночная", HexColor.toColor("#2980B9"), HexColor.toColor("#2C3E50")),
                new Theme("Лунный свет", HexColor.toColor("#000000"), HexColor.toColor("#C0C0C0")),
                new Theme("Закат", HexColor.toColor("#FF5733"), HexColor.toColor("#900C3F")),
                new Theme("Лесная чаща", HexColor.toColor("#145A32"), HexColor.toColor("#0B5345")),
                new Theme("Океанский бриз", HexColor.toColor("#1ABC9C"), HexColor.toColor("#16A085")),
                new Theme("Песчаная буря", HexColor.toColor("#D4AC0D"), HexColor.toColor("#B9770E")),
                new Theme("Полярное сияние", HexColor.toColor("#1F618D"), HexColor.toColor("#76D7C4")),
                new Theme("Вечная ночь", HexColor.toColor("#2C3E50"), HexColor.toColor("#17202A")),
                new Theme("Рассвет", HexColor.toColor("#F39C12"), HexColor.toColor("#E74C3C")),
                new Theme("Звездная пыль", HexColor.toColor("#8E44AD"), HexColor.toColor("#5B2C6F")),
                new Theme("Лазурный берег", HexColor.toColor("#3498DB"), HexColor.toColor("#2980B9")),
                new Theme("Кровавая луна", HexColor.toColor("#E74C3C"), HexColor.toColor("#C0392B"))
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
