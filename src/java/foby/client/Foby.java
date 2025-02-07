package foby.client;

import foby.client.command.Command;
import foby.client.event.EventHandler;
import foby.client.module.Module;
import foby.client.module.modules.ModuleManager;
import foby.client.command.CommandRegister;
import foby.client.themes.ThemesUtil;
import foby.client.utils.fonts.FontRenderers;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.ChatScreen;
import java.util.List;

@Getter
public class Foby {
    public static Minecraft mc = Minecraft.getInstance();
    @Getter
    public static Foby instance;
    @Getter
    public static ModuleManager moduleManager;
    @Getter
    public static FontRenderers font;
    @Getter
    public static ThemesUtil themesUtil;
    @Getter
    public static CommandRegister commandRegister;

    public Foby() {
        instance = this;
        moduleManager = new ModuleManager();
        font = new FontRenderers();
        themesUtil = new ThemesUtil();
        themesUtil.init();
        commandRegister = new CommandRegister();
    }

    public static void keyPress(int key) {
        if(mc.screen != null) return;
        for (Module m : moduleManager.getFunctions()) {
            if (m.bind == key) {
                m.toggle();
            }
        }
    }

    @EventHandler
    public static void handleChatMessage(String message) {
        if (message.startsWith(CommandRegister.PREFIX)) {
            String[] args = message.substring(1).split(" ");
            String command = args[0].toLowerCase();

            if (command.equals("help")) {
                mc.player.sendSystemMessage(Component.literal("§7=== Available Commands ==="));
                for (Command cmd : commandRegister.getCommands()) {
                    mc.player.sendSystemMessage(Component.literal("§8" + CommandRegister.PREFIX + cmd.getName() + " §7- " + cmd.getDescription()));
                }
                return;
            }

            commandRegister.executeCommand(message);
            mc.player.sendSystemMessage(Component.literal("§7Command executed: " + message));
        }
    }
}
