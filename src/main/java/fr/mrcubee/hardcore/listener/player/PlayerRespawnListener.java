package fr.mrcubee.hardcore.listener.player;

import fr.mrcubee.hardcore.HardcorePlugin;
import fr.mrcubee.hardcore.service.HardcoreService;
import fr.mrcubee.langlib.Lang;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class PlayerRespawnListener implements Listener {

    private final Plugin plugin;
    private final Server server;
    private HardcoreService hardcoreService;

    public PlayerRespawnListener() {
        this.plugin = JavaPlugin.getPlugin(HardcorePlugin.class);
        this.server = Bukkit.getServer();
        this.hardcoreService = Bukkit.getServicesManager().load(HardcoreService.class);
    }

    @EventHandler
    public void updateBanServiceEvent(final ServiceRegisterEvent event) {
        if (event.getProvider().getService().equals(HardcoreService.class))
            this.hardcoreService = Bukkit.getServicesManager().load(HardcoreService.class);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void event(final PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        final String remainingTimeStr;
        final List<World> worlds;

        if (!server.isHardcore() || !player.isDead())
            return;
        worlds = Bukkit.getWorlds();
        if (worlds.size() > 0)
            event.setRespawnLocation(worlds.get(0).getSpawnLocation());
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setFoodLevel(20);
        remainingTimeStr = HardcoreService.formatTime(this.hardcoreService.getBanRemainingTime(player));
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            player.setGameMode(GameMode.SURVIVAL);
            player.kickPlayer(Lang.getMessage(player, "ban.message",
                    "&cLANG ERROR: ban.message", true, remainingTimeStr));
        }, 0L);
    }

}
