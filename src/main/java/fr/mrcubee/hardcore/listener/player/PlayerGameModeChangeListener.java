package fr.mrcubee.hardcore.listener.player;

import fr.mrcubee.hardcore.service.HardcoreService;
import fr.mrcubee.langlib.Lang;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.server.ServiceRegisterEvent;

public class PlayerGameModeChangeListener implements Listener {

    private HardcoreService banService;
    private final Server server;

    public PlayerGameModeChangeListener() {
        this.banService = Bukkit.getServicesManager().load(HardcoreService.class);
        this.server = Bukkit.getServer();
    }

    @EventHandler
    public void updateBanServiceEvent(final ServiceRegisterEvent event) {
        if (event.getProvider().getService().equals(HardcoreService.class))
            this.banService = Bukkit.getServicesManager().load(HardcoreService.class);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void event(final PlayerGameModeChangeEvent event) {
        final Player player = event.getPlayer();
        final long remainingTime;
        final String remainingTimeStr;

        if (!server.isHardcore() || event.getNewGameMode() != GameMode.SPECTATOR)
            return;
        remainingTime = this.banService.getBanRemainingTime(player);
        if (remainingTime == 0)
            return;
        event.setCancelled(true);
        remainingTimeStr = HardcoreService.formatTime(remainingTime);
        player.kickPlayer(Lang.getMessage(player, "ban.message",
                "&cLANG ERROR: ban.message", true, remainingTimeStr));
    }

}