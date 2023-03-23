package fr.mrcubee.hardcore.command.subcommand;

import fr.mrcubee.hardcore.command.HDCommand;
import fr.mrcubee.hardcore.command.subcommand.ban.EditBanCommand;
import fr.mrcubee.hardcore.command.subcommand.ban.RevokeSubCommand;
import fr.mrcubee.hardcore.command.subcommand.ban.ShowSubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;

public class BanSubCommand implements HDCommand {

    private final Map<String, HDCommand> subCommands;

    public BanSubCommand() {
        this.subCommands = new HashMap<String, HDCommand>(3);
        this.subCommands.put("edit", new EditBanCommand());
        this.subCommands.put("revoke", new RevokeSubCommand());
        this.subCommands.put("show", new ShowSubCommand());
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
