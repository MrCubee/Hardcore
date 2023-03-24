package fr.mrcubee.hardcore;

import fr.mrcubee.hardcore.command.HardcoreCommand;
import fr.mrcubee.hardcore.listener.ListenerRegister;
import fr.mrcubee.hardcore.service.DefaultHardcoreService;
import fr.mrcubee.hardcore.service.HardcoreService;
import fr.mrcubee.langlib.Lang;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * @author MrCubee
 * @since 1.0
 * @version 1.0
 */
public class HardcorePlugin extends JavaPlugin {

    private File playerBanTimeFile;
    private DefaultHardcoreService defaultService;

    @Override
    public void onEnable() {
        final PluginCommand pluginCommand;
        final HardcoreCommand hardcoreCommand;
        final FileConfiguration config;

        saveDefaultConfig();
        config = getConfig();
        this.defaultService = new DefaultHardcoreService();
        this.defaultService.loadBanTime(config.getConfigurationSection("ban.time"));
        ConfigurationSerialization.registerClass(PlayerData.class, "PlayerData");
        getServer().getServicesManager().register(HardcoreService.class, this.defaultService, this, ServicePriority.Lowest);
        pluginCommand = getCommand("hardcore");
        hardcoreCommand = new HardcoreCommand(this);
        if (pluginCommand != null) {
            pluginCommand.setExecutor(hardcoreCommand);
            pluginCommand.setTabCompleter(hardcoreCommand);
        }
        this.playerBanTimeFile = new File(getDataFolder(), "bans.yml");
        Lang.setDefaultLang(config.getString("lang", "EN_us"));
        ListenerRegister.register(this);
        if (this.playerBanTimeFile.exists())
            this.defaultService.loadBans(YamlConfiguration.loadConfiguration(this.playerBanTimeFile));
    }

    @Override
    public void onDisable() {
        final YamlConfiguration yamlConfiguration;

        try {
            yamlConfiguration = this.defaultService.saveBans();
            if (yamlConfiguration != null)
                yamlConfiguration.save(this.playerBanTimeFile);
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
        ConfigurationSerialization.unregisterClass(PlayerData.class);
    }

}
