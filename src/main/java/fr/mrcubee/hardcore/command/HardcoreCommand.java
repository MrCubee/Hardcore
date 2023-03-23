package fr.mrcubee.hardcore.command;

import fr.mrcubee.hardcore.command.subcommand.BanSubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;

public class HardcoreCommand implements HDCommand {

    private final Map<String, HDCommand> subCommands;

    public HardcoreCommand() {
        this.subCommands = new HashMap<String, HDCommand>(2);
        this.subCommands.put("ban", new BanSubCommand());
    }

    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String label, final String[] args) {
        final HDCommand hdCommand;

        if (args.length < 1)
            return false;
        hdCommand = this.subCommands.get(args[0].toLowerCase());
        if (hdCommand == null)
            return false;
        return hdCommand.onCommand(commandSender, command, label, Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public List<String> onTabComplete(final CommandSender commandSender, final Command command, final String label, final String[] args) {
        final List<String> subCommands;
        final String current = args[args.length - 1].toLowerCase();
        final HDCommand hdCommand;

        if (args.length < 2) {
            subCommands = new ArrayList<String>(this.subCommands.keySet());
            subCommands.removeIf(element -> !element.toLowerCase().startsWith(current));
            return subCommands;
        }
        hdCommand = this.subCommands.get(args[0].toLowerCase());
        if (hdCommand == null)
            return Collections.emptyList();
        return hdCommand.onTabComplete(commandSender, command, label, Arrays.copyOfRange(args, 1, args.length));
    }

}
