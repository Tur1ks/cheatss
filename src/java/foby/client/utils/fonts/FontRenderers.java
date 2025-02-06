package foby.client.utils.fonts;

import foby.client.misc.util.Utils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FontRenderers {

    private static final Map<String, FontRenderer> fontCache = new HashMap<>();

    public static final String msSemi = "Montserrat-SemiBold";

    public static FontRenderer info(String fontName, float size) {
        try {
            return createFont(size, fontName);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static @NotNull FontRenderer createFont(float size, String name) throws IOException, FontFormatException {
        String key = name + "_" + size;
        if (fontCache.containsKey(key)) {
            return fontCache.get(key);
        }
        Font font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(Utils.getCRStream("font/" + name + ".ttf")))
                .deriveFont(Font.PLAIN, size / 2f);
        FontRenderer fontRenderer = new FontRenderer(font, size / 2f);
        fontCache.put(key, fontRenderer);
        return fontRenderer;
    }
}
