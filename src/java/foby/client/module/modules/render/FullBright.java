package foby.client.module.modules.render;

import foby.client.module.Module;
import foby.client.module.utils.Category;
import foby.client.module.utils.ModuleAnnotation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

@ModuleAnnotation(name = "FullBright", desc = "Убирает темноту!", category = Category.RENDER)
public class FullBright extends Module {
    @Override
    public void onEnable() {
        super.onEnable();
        mc.player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 9999999, 9999999));
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.player.removeEffect(MobEffects.NIGHT_VISION);
    }
}
