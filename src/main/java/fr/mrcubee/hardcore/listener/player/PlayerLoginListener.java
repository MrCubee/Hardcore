package fr.mrcubee.hardcore.listener.player;

import fr.mrcubee.hardcore.HardcoreService;
import fr.mrcubee.langlib.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLoginListener implements Listener {

    private final HardcoreService banService;

    public PlayerLoginListener() {
        this.banService = Bukkit.getServicesManager().load(HardcoreService.class);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void event(final PlayerLoginEvent event) {
        final Player player = event.getPlayer();
        final long remainingTime = this.banService.getBanRemainingTime(player);
        final String remainingTimeStr;

        if (remainingTime <= 0)
            return;
        remainingTimeStr = HardcoreService.formatTime(remainingTime);
        event.setKickMessage(Lang.getMessage(player, "ban.message.remaining",
                "&cLANG ERROR: ban.message.remaining", true, remainingTimeStr));
        event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
    }

}
