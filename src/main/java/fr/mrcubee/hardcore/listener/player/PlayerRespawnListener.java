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
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServiceRegisterEvent;

public class PlayerRespawnListener implements Listener {

    private HardcoreService banService;
    private final Server server;

    public PlayerRespawnListener() {
        this.banService = Bukkit.getServicesManager().load(HardcoreService.class);
        this.server = Bukkit.getServer();
    }

    @EventHandler
    public void updateBanServiceEvent(final ServiceRegisterEvent event) {
        if (event.getProvider().getService().equals(HardcoreService.class))
            this.banService = Bukkit.getServicesManager().load(HardcoreService.class);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void event(final PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        final String remainingTimeStr;

        if (!server.isHardcore())
            return;
        player.teleport(player.getWorld().getSpawnLocation());
        player.setGameMode(GameMode.SURVIVAL);
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setFoodLevel(20);
        remainingTimeStr = HardcoreService.formatTime(this.banService.getBanRemainingTime(player));
        player.kickPlayer(Lang.getMessage(player, "ban.message",
                "&cLANG ERROR: ban.message", true, remainingTimeStr));
    }

}
