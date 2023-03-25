package fr.mrcubee.hardcore.listener;

import fr.mrcubee.hardcore.HardcorePlugin;
import fr.mrcubee.hardcore.listener.player.PlayerDeathListener;
import fr.mrcubee.hardcore.listener.player.PlayerGameModeChangeListener;
import fr.mrcubee.hardcore.listener.player.PlayerLoginListener;
import fr.mrcubee.hardcore.listener.player.PlayerRespawnListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class ListenerRegister {

    public static void register(final HardcorePlugin hardcorePlugin) {
        final PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new PlayerDeathListener(), hardcorePlugin);
        pluginManager.registerEvents(new PlayerGameModeChangeListener(), hardcorePlugin);
        pluginManager.registerEvents(new PlayerLoginListener(), hardcorePlugin);
        pluginManager.registerEvents(new PlayerRespawnListener(), hardcorePlugin);
    }

}
