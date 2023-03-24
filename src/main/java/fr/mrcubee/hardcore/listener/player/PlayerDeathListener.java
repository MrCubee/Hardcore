package fr.mrcubee.hardcore.listener.player;

import fr.mrcubee.hardcore.service.HardcoreService;
import org.bukkit.Bukkit;
import org.bukkit.Server;
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

    @EventHandler(priority = EventPriority.LOW)
    public void event(final PlayerDeathEvent event) {
        if (!this.server.isHardcore())
            return;
        this.banService.setBanTime(event.getEntity(), System.currentTimeMillis());
    }

}
