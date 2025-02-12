package foby.client.module.modules;


import foby.client.module.Module;
import foby.client.module.modules.combat.Aura;
import foby.client.module.modules.movement.*;
import foby.client.module.modules.render.*;
import foby.client.module.modules.util.AutoFish;
import foby.client.module.modules.util.ItemScroller;
import foby.client.module.modules.util.NoDelay;
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
    public Arrows arrows;
    public Sprint sprint;
    public Inventory inventory;
    public FullBright fullBright;
    public Crosshair crosshair;
    public AutoFish autoFish;
    public NoDelay noDelay;

    //Move
    public Timer timer;

    //render
    public ChinaHat chinaHat;

    //util
    public ItemScroller itemScroller;

    public ModuleManager() {
        modules.addAll(Arrays.asList(
                hud = new HUD(),
                test = new Test(),
                aura = new Aura(),
                speed = new Speed(),
                esp = new ESP(),
                arrows = new Arrows(),
                itemScroller = new ItemScroller(),
                timer = new Timer(),
                chinaHat = new ChinaHat(),
                sprint = new Sprint(),
                inventory = new Inventory(),
                fullBright = new FullBright(),
                crosshair = new Crosshair(),
                autoFish = new AutoFish(),
                noDelay = new NoDelay()
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
