package foby.client.themes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.Color;

public class ThemesUtil {
    public List<Theme> themes = new ArrayList<>();
    private Theme currentTheme = null;

    public void init() {
        themes.addAll(Arrays.asList(
                // Космическая тема
                new Theme("Main",
                        new Color(232, 42, 145).getRGB(),    // Основной тёмно-синий
                        new Color(237, 179, 229).getRGB(), // Текст светлый
                        new Color(167, 75, 204).getRGB(),  // Цвет шейдеров
                        new Color(60, 34, 55).getRGB()),   // Фон тёмный

                // Закатная тема
                new Theme("Fatality",
                        new Color(231, 76, 60).getRGB(),   // Основной красный
                        new Color(255, 255, 255).getRGB(), // Текст белый
                        new Color(243, 156, 18).getRGB(),  // Цвет шейдеров
                        new Color(44, 62, 80).getRGB()),   // Фон тёмно-синий

                // Природная тема
                new Theme("Summer",
                        new Color(39, 174, 96).getRGB(),   // Основной зелёный
                        new Color(236, 240, 241).getRGB(), // Текст светлый
                        new Color(241, 196, 15).getRGB(),  // Цвет шейдеров
                        new Color(46, 64, 83).getRGB()),   // Фон тёмно-серый

                // Океанская тема
                new Theme("Океан",
                        new Color(52, 152, 219).getRGB(),  // Основной голубой
                        new Color(255, 255, 255).getRGB(), // Текст белый
                        new Color(26, 188, 156).getRGB(),  // Цвет шейдеров
                        new Color(44, 62, 80).getRGB()),   // Фон тёмно-синий

                // Неоновая тема
                new Theme("NeEbu",
                        new Color(255, 30, 30).getRGB(),   // Основной неоново-красный
                        new Color(255, 255, 255).getRGB(), // Текст белый
                        new Color(0, 255, 0).getRGB(),     // Цвет шейдеров
                        new Color(26, 26, 26).getRGB())    // Фон чёрный
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
