package foby.client;


import foby.client.module.Module;
import foby.client.module.modules.ModuleManager;
import foby.client.themes.ThemesUtil;
import foby.client.utils.fonts.FontRenderers;
import lombok.Getter;
import net.minecraft.client.Minecraft;

@Getter
public class Foby {
    public static Minecraft mc = Minecraft.getInstance();
    // ...
    @Getter
    public static Foby instance;
    @Getter
    public static ModuleManager moduleManager;
    @Getter
    public static FontRenderers font;
    @Getter
    public static ThemesUtil themesUtil;
    // ...

    public Foby() {
        instance = this;
        moduleManager = new ModuleManager();
        font = new FontRenderers();
        themesUtil = new ThemesUtil();
        themesUtil.init();
    }

    public static void keyPress(int key) {
        if(mc.screen != null) return;
        for (Module m : moduleManager.getFunctions()) {
            if (m.bind == key) {
                m.toggle();
            }
        }
    }
}
