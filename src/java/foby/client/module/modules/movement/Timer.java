package foby.client.module.modules.movement;

import foby.client.event.EventHandler;
import foby.client.module.Module;
import foby.client.module.utils.Category;
import foby.client.module.utils.ModuleAnnotation;
import foby.client.ui.clickgui.setting.settings.NumberSetting;
import java.lang.reflect.Field;

@ModuleAnnotation(name = "Timer", desc = "Ускоряет вас", category = Category.MOVEMENT)
public class Timer extends Module {
    private final NumberSetting speed = new NumberSetting("Speed", "123", 1.2, 0.1, 3, 0.1);
    private Field msPerTickField;

    public Timer() {
        addSettings(speed);
        try {
            msPerTickField = mc.timer.getClass().getDeclaredField("msPerTick");
            msPerTickField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onUpdate() {
        if (!isEnabled() || msPerTickField == null) return;
        try {
            msPerTickField.setFloat(mc.timer, (float)(50.0 / speed.getValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        if (msPerTickField == null) return;
        try {
            msPerTickField.setFloat(mc.timer, 50.0f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
