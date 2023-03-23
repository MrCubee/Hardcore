package fr.mrcubee.hardcore;

import fr.mrcubee.hardcore.listener.ListenerRegister;
import fr.mrcubee.langlib.Lang;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
        final FileConfiguration config;
        final long banTime;

        this.playerBanTimeFile = new File(getDataFolder(), "bans.yml");
        saveDefaultConfig();
        config = getConfig();
        Lang.setDefaultLang(config.getString("lang", "EN_us"));
        this.defaultService = new DefaultHardcoreService();
        getServer().getServicesManager().register(HardcoreService.class, defaultService, this, ServicePriority.Lowest);
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
    }

}
