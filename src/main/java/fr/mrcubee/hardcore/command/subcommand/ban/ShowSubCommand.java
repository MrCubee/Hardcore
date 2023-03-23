package fr.mrcubee.hardcore.command.subcommand.ban;

import fr.mrcubee.hardcore.HardcoreService;
import fr.mrcubee.hardcore.command.HDCommand;
import fr.mrcubee.langlib.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShowSubCommand implements HDCommand {

    private final HardcoreService hardcoreService;

    public ShowSubCommand() {
        this.hardcoreService = Bukkit.getServicesManager().load(HardcoreService.class);
    }

    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String label, final String[] args) {
        final long remainingTime;

        if (args.length < 1)
            return false;
        remainingTime = this.hardcoreService.getBanRemainingTime(args[0]);
        commandSender.sendMessage(Lang.getMessage(commandSender, "ban.show",
                "&cLANG ERROR: ban.show", true, HardcoreService.formatTime(remainingTime)));
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender commandSender, final Command command, final String label, final String[] args) {
        if (args.length < 2)
            return new ArrayList<String>(this.hardcoreService.playerNameHasData());
        return Collections.emptyList();
    }

}
