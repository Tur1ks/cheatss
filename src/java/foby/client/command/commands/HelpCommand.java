package foby.client.command.commands;

import foby.client.command.Command;
import foby.client.command.CommandRegister;
import net.minecraft.network.chat.Component;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help", "Shows all available commands", "h", "?");
    }

    @Override
    public void execute(String[] args) {
        mc.player.sendSystemMessage(Component.literal("§7Available commands:"));
        for (Command command : CommandRegister.getCommands()) {
            mc.player.sendSystemMessage(Component.literal("§8" + CommandRegister.PREFIX + command.getName() + " §7- " + command.getDescription()));
        }
    }
}
