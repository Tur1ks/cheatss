package foby.client.module;

import foby.client.Foby;
import foby.client.event.EventManager;
import foby.client.module.utils.Category;
import foby.client.module.utils.ModuleAnnotation;
import foby.client.ui.clickgui.setting.Setting;
import foby.client.utils.Mine;
import lombok.Getter;
import foby.client.event.EventHandler;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Module implements Mine {
    private final ModuleAnnotation info = this.getClass().getAnnotation(ModuleAnnotation.class);
    public ArrayList<Setting> settings;
    public String name;
    public String desc;
    public Category category;
    public int bind;
    @Getter
    public boolean state, util;

    private int key = -1; // Add this field

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public Module() {
        initializeProperties();
    }

    private void initializeProperties() {
        name = info.name();
        desc = info.desc();
        category = info.category();
        state = false;
        bind = info.key();
        settings = new ArrayList<>();
    }

    public void setState() {
        toggle();
    }

    public void toggle() {
        this.state = !state;
        if (!state) {
            onDisable();
            EventManager.unregister(this);
        } else {
            onEnable();
            EventManager.register(this);
        }
    }

    protected static <T extends Module> T link(final Class<T> clazz) {
        return (T) Foby.getModuleManager().get(String.valueOf(clazz));
    }

    public void onDisable() {
    }

    public void onEnable() {
    }

    public boolean isSetting() {
        return settings.size() > 0;
    }

    public final void addSettings(Setting... options) {
        settings.addAll(Arrays.asList(options));
    }

    public boolean isEnabled() {
        return this.state;
    }



}
