package fr.mrcubee.hardcore.listener.player;

import fr.mrcubee.hardcore.service.HardcoreService;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServiceRegisterEvent;

public class PlayerRespawnListener implements Listener {
;
    private final Server server;

    public PlayerRespawnListener() {
        this.server = Bukkit.getServer();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void event(final PlayerRespawnEvent event) {
        final Player player = event.getPlayer();

        if (!server.isHardcore())
            return;
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setFoodLevel(20);
        event.setRespawnLocation(player.getWorld().getSpawnLocation());
    }

}
