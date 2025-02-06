package foby.client.themes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThemesUtil {
    public List<Theme> themes = new ArrayList<>();
    private Theme currentTheme = null;

    public void init() {
        themes.addAll(Arrays.asList(
                // Космическая тема
                new Theme("Космос",
                        HexColor.toColor("#2C3E50"),  // Основной тёмно-синий
                        HexColor.toColor("#ECF0F1"),  // Текст светлый
                        HexColor.toColor("#8E44AD"),  // Акцент фиолетовый
                        HexColor.toColor("#1A1A2E")), // Фон тёмный

                // Закатная тема
                new Theme("Закат",
                        HexColor.toColor("#E74C3C"),  // Основной красный
                        HexColor.toColor("#FFFFFF"),  // Текст белый
                        HexColor.toColor("#F39C12"),  // Акцент оранжевый
                        HexColor.toColor("#2C3E50")), // Фон тёмно-синий

                // Природная тема
                new Theme("Природа",
                        HexColor.toColor("#27AE60"),  // Основной зелёный
                        HexColor.toColor("#ECF0F1"),  // Текст светлый
                        HexColor.toColor("#F1C40F"),  // Акцент жёлтый
                        HexColor.toColor("#2E4053")), // Фон тёмно-серый

                // Океанская тема
                new Theme("Океан",
                        HexColor.toColor("#3498DB"),  // Основной голубой
                        HexColor.toColor("#FFFFFF"),  // Текст белый
                        HexColor.toColor("#1ABC9C"),  // Акцент бирюзовый
                        HexColor.toColor("#2C3E50")), // Фон тёмно-синий

                // Неоновая тема
                new Theme("Неон",
                        HexColor.toColor("#FF1E1E"),  // Основной неоново-красный
                        HexColor.toColor("#FFFFFF"),  // Текст белый
                        HexColor.toColor("#00FF00"),  // Акцент неоново-зелёный
                        HexColor.toColor("#1A1A1A"))  // Фон чёрный
        ));
        currentTheme = themes.get(0);
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
                return 1;
            }
        }
        return 0;
    }
}
