package foby.client.module.modules.util;

import com.google.common.eventbus.Subscribe;
import foby.client.event.EventHandler;
import foby.client.event.events.impl.EventPacket;
import foby.client.event.events.impl.TickEvent;
import foby.client.module.Module;
import foby.client.module.utils.Category;
import foby.client.module.utils.ModuleAnnotation;
import foby.client.utils.math.TimerUtil;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.InteractionHand;

@ModuleAnnotation(name = "AutoFish", desc = "Автоматически достает рыбу когда это нужно!", category = Category.UTIL)
public class AutoFish extends Module {
    TimerUtil delay = new TimerUtil();
    private boolean isHooked = false;
    private boolean needToHook = false;

    @EventHandler
    private void onUpdate(TickEvent e) {
        if (delay.isReached(600) && isHooked) {
            mc.player.connection.send(new ServerboundUseItemPacket(InteractionHand.MAIN_HAND, 0, 0,0));
            isHooked = false;
            needToHook = true;
            delay.reset();
        }

        if (delay.isReached(300) && needToHook) {
            mc.player.connection.send(new ServerboundUseItemPacket(InteractionHand.MAIN_HAND, 0, 0,0));
            needToHook = false;
            delay.reset();
        }
    }

    @Subscribe
    private void onPacket(EventPacket e) {
        if (e.getPacket() instanceof ClientboundSoundPacket p) {
            if (p.getSound().value().getLocation().getPath().equals("entity.fishing_bobber.splash")) {
                isHooked = true;
                delay.reset();
            }
        }
    }
}
