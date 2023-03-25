package fr.mrcubee.hardcore.listener.player;

import fr.mrcubee.hardcore.PlayerData;
import fr.mrcubee.hardcore.service.HardcoreService;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.server.ServiceRegisterEvent;

public class PlayerDeathListener implements Listener {

    private HardcoreService banService;
    private final Server server;

    public PlayerDeathListener() {
        this.banService = Bukkit.getServicesManager().load(HardcoreService.class);
        this.server = Bukkit.getServer();
    }

    @EventHandler
    public void updateBanServiceEvent(final ServiceRegisterEvent event) {
        if (event.getProvider().getService().equals(HardcoreService.class))
            this.banService = Bukkit.getServicesManager().load(HardcoreService.class);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void priorityEvent(final PlayerDeathEvent event) {
        final Player player;
        final long lastDeathTime;
        final long currentTime;

        if (!this.server.isHardcore())
            return;
        player = event.getEntity();
        lastDeathTime = this.banService.getLastDeathTime(player);
        currentTime = System.currentTimeMillis();
        this.banService.setLastDeathTime(player, currentTime);
        if (lastDeathTime != 0)
            event.setDeathMessage(event.getDeathMessage() + " (" + HardcoreService.formatTime(currentTime - lastDeathTime) + ")");
    }

    @EventHandler(priority = EventPriority.LOW)
    public void event(final PlayerDeathEvent event) {
        final Player player;

        if (!this.server.isHardcore())
            return;
        player = event.getEntity();
        event.setKeepLevel(false);
        event.setKeepInventory(false);
        this.banService.setPlayerDeathCount(player, this.banService.getPlayerDeathCount(player) + 1);
        this.banService.setEndBanTime(player, this.banService.generateEndBanTime(player));
    }

}
