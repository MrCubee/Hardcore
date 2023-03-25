package fr.mrcubee.hardcore.command.subcommand.ban;

import fr.mrcubee.hardcore.service.DefaultHardcoreService;
import fr.mrcubee.hardcore.service.HardcoreService;
import fr.mrcubee.hardcore.command.HDCommand;
import fr.mrcubee.langlib.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServiceRegisterEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditBanCommand implements HDCommand {

    private static List<String> OPERATIONS = Arrays.asList("+", "-");
    private static List<String> UNITS = Arrays.asList("d", "h", "m", "s");

    private HardcoreService hardcoreService;

    public EditBanCommand() {
        this.hardcoreService = Bukkit.getServicesManager().load(HardcoreService.class);
    }

    private static long parseEditCommand(final String str) throws CommandException {
        long unit;
        long defaultLong;

        switch (str.charAt(0)) {
            case '+':
                defaultLong = 1;
                break;
            case '-':
                defaultLong = -1;
                break;
            default:
                defaultLong = 0;
        }
        switch (str.charAt(str.length() - 1)) {
            case 'd':
                unit = 86400000;
                break;
            case 'h':
                unit = 3600000;
                break;
            case 'm':
                unit = 60000;
                break;
            case 's':
                unit = 1000;
                break;
            default:
                unit = 0;
        }
        try {
            defaultLong *= Long.parseLong(str.substring(1, str.length()  - 1));
        } catch (final NumberFormatException exception) {
            defaultLong = 0;
        }
        return defaultLong * unit;
    }

    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String label, final String[] args) {
        final String playerName;
        long banTime;

        if (args.length < 2)
            return false;
        playerName = args[0];
        banTime = this.hardcoreService.getLastDeathTime(playerName) + this.hardcoreService.getBanRemainingTime(playerName);
        for (int i = 1; i < args.length; i++)
            banTime += parseEditCommand(args[i]);
        if (!this.hardcoreService.setEndBanTime(playerName, banTime)) {
            commandSender.sendMessage(Lang.getMessage(commandSender, "ban.player.not_found",
                    "&cLANG ERROR: ban.player.not_found", true, playerName));
            return true;
        }
        commandSender.sendMessage(
                banTime > 0 ?
                        Lang.getMessage(commandSender, "ban.edit.success",
                                "&cLANG ERROR: ban.edit.success", true, playerName, HardcoreService.formatTime(this.hardcoreService.getBanRemainingTime(playerName)))
                        :
                        Lang.getMessage(commandSender, "ban.revoke.success",
                                "&cLANG ERROR: ban.revoke.success", true, playerName)
        );
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender commandSender, final Command command, final String label, final String[] args) {
        final List<String> staticList;
        final List<String> result;
        final String current = args[args.length - 1].toLowerCase();

        if (args.length < 2)
            result = new ArrayList<String>(this.hardcoreService.playerNameHasData());
        else {
            if (current.length() < 2)
                result = new ArrayList<String>(OPERATIONS);
            else {
                staticList = UNITS;
                result = new ArrayList<String>(staticList.size());
                for (final String element : staticList)
                    result.add(current + element);
            }
        }
        result.removeIf(element -> !element.toLowerCase().startsWith(current));
        return result;
    }

    @EventHandler
    public void updateBanServiceEvent(final ServiceRegisterEvent event) {
        if (event.getProvider().getService().equals(HardcoreService.class))
            this.hardcoreService = Bukkit.getServicesManager().load(HardcoreService.class);
    }

}
