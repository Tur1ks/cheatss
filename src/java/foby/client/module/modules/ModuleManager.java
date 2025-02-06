package foby.client.module.modules;


import foby.client.module.Module;
import foby.client.module.modules.combat.Aura;
import foby.client.module.modules.movement.*;
import foby.client.module.modules.render.ESP;
import foby.client.module.modules.render.HUD;
import foby.client.module.modules.render.Test;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class ModuleManager {

    public static final List<Module> modules = new CopyOnWriteArrayList<>();

    public Test test;
    public Aura aura;
    public Speed speed;
    public HUD hud;
    public ESP esp;

    public ModuleManager() {
        modules.addAll(Arrays.asList(
                hud = new HUD(),
                test = new Test(),
                aura = new Aura(),
                speed = new Speed(),
                esp = new ESP()
        ));
    }


    public static List<Module> getModules() {
        return modules;
    }

    public List<Module> getFunctions() {
        return modules;
    }


    public static Module get(String name) {
        for (Module module : modules) {
            if (module != null && module.name.equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    public static Module getModule(Class<? extends Module> zClass) {
        for (Module module : modules) {
            if(module.getClass() == zClass) {
                return module;
            }
        }
        return null;
    }

}
