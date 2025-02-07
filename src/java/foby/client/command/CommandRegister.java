package foby.client.command;

import foby.client.command.commands.HelpCommand;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandRegister {
    private static final List<Command> commands = new ArrayList<>();
    public static final String PREFIX = ".";

    public CommandRegister() {
        commands.add(new HelpCommand());
        // Add more commands here
    }

    public static List<Command> getCommands() {
        return commands;
    }

    public static void executeCommand(String message) {
        if (!message.startsWith(PREFIX)) return;

        message = message.substring(PREFIX.length());
        String[] args = message.split(" ");
        String commandName = args[0];
        args = Arrays.copyOfRange(args, 1, args.length);

        for (Command command : commands) {
            if (command.getName().equalsIgnoreCase(commandName) || Arrays.asList(command.getAliases()).contains(commandName.toLowerCase())) {
                command.execute(args);
                return;
            }
        }
    }

    public static List<String> getCompletions(String input) {
        List<String> completions = new ArrayList<>();
        if (!input.startsWith(PREFIX)) return completions;

        String[] args = input.substring(PREFIX.length()).split(" ");
        if (args.length <= 1) {
            String commandStart = args[0].toLowerCase();
            for (Command command : commands) {
                if (command.getName().toLowerCase().startsWith(commandStart)) {
                    completions.add(PREFIX + command.getName());
                }
                for (String alias : command.getAliases()) {
                    if (alias.toLowerCase().startsWith(commandStart)) {
                        completions.add(PREFIX + alias);
                    }
                }
            }
        }
        return completions;
    }
}
