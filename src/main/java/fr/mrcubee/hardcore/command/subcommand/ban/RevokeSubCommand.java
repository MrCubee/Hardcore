package fr.mrcubee.hardcore.command.subcommand.ban;

import fr.mrcubee.hardcore.service.HardcoreService;
import fr.mrcubee.hardcore.command.HDCommand;
import fr.mrcubee.langlib.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServiceRegisterEvent;

import java.util.*;

public class RevokeSubCommand implements HDCommand {

    private HardcoreService hardcoreService;

    public RevokeSubCommand() {
        this.hardcoreService = Bukkit.getServicesManager().load(HardcoreService.class);
    }

    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String label, final String[] args) {
        final String playerName;

        if (args.length < 1)
            return false;
        playerName = args[0];
        if (!this.hardcoreService.revokeBan(playerName)) {
            commandSender.sendMessage(Lang.getMessage(commandSender, "ban.player.not_found",
                    "&cLANG ERROR: ban.player.not_found", true, playerName));
            return true;
        }
        commandSender.sendMessage(Lang.getMessage(commandSender, "ban.revoke.success",
                "&cLANG ERROR: ban.revoke.success", true, playerName));
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender commandSender, final Command command, final String label, final String[] args) {
        if (args.length < 2)
            return new ArrayList<String>(this.hardcoreService.playerNameHasData());
        return Collections.emptyList();
    }

    @EventHandler
    public void updateBanServiceEvent(final ServiceRegisterEvent event) {
        if (event.getProvider().getService().equals(HardcoreService.class))
            this.hardcoreService = Bukkit.getServicesManager().load(HardcoreService.class);
    }

}
