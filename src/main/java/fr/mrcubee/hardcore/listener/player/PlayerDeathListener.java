package fr.mrcubee.hardcore.listener.player;

import fr.mrcubee.hardcore.HardcoreService;
import fr.mrcubee.langlib.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;

public class PlayerDeathListener implements Listener {

    private final HardcoreService banService;
    private final Server server;

    public PlayerDeathListener() {
        this.banService = Bukkit.getServicesManager().load(HardcoreService.class);
        this.server = Bukkit.getServer();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void event(final PlayerDeathEvent event) {
        if (!this.server.isHardcore())
            return;
        this.banService.setBanTime(event.getEntity(), System.currentTimeMillis());
    }

}
