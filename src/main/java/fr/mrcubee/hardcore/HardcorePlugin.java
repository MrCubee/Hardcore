package fr.mrcubee.hardcore;

import fr.mrcubee.hardcore.listener.ListenerRegister;
import fr.mrcubee.langlib.Lang;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author MrCubee
 * @since 1.0
 * @version 1.0
 */
public class HardcorePlugin extends JavaPlugin implements HardcoreService {

    private File playerBanTimeFile;
    public long banTime;
    private transient Map<UUID, Long> banTimes;

    public static long getBanTimeFromSection(final ConfigurationSection config) {
        final long day;
        final long hour;
        final long minute;
        final long second;

        if (config == null)
            return 0;
        day = config.getLong("day", 0);
        hour = config.getLong("hour", 0);
        minute = config.getLong("minute", 0);
        second = config.getLong("second", 0);
        return day * 86400000 + hour * 3600000 + minute * 60000 + second * 1000;
    }

    public void loadBans() throws IOException, InvalidConfigurationException {
        final YamlConfiguration configuration;
        UUID playerId;
        long value;

        if (!this.playerBanTimeFile.exists())
            return;
        configuration = new YamlConfiguration();
        configuration.load(this.playerBanTimeFile);
        for (final String key : configuration.getKeys(false)) {
            try {
                playerId = UUID.fromString(key);
                value = configuration.getLong(key, 0);
                if (value > 0)
                    this.banTimes.put(playerId, value);
            } catch (final Exception e) {};
        }
    }

    public void saveBans() throws IOException {
        final YamlConfiguration configuration = new YamlConfiguration();

        for (final Map.Entry<UUID, Long> banTime : this.banTimes.entrySet())
            configuration.set(String.valueOf(banTime.getKey()), banTime.getValue());
        configuration.save(this.playerBanTimeFile);
    }

    @Override
    public void onEnable() {
        final FileConfiguration config;
        final long banTime;

        this.playerBanTimeFile = new File(getDataFolder(), "bans.yml");
        this.banTimes = new ConcurrentHashMap<UUID, Long>();
        saveDefaultConfig();
        config = getConfig();
        Lang.setDefaultLang(config.getString("lang", "EN_us"));
        banTime = getBanTimeFromSection(config.getConfigurationSection("ban.time"));
        this.banTime = banTime > 0 ? banTime : 0;
        getServer().getServicesManager().register(HardcoreService.class, this, this, ServicePriority.Lowest);
        try {
            loadBans();
        } catch (final IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
        ListenerRegister.register(this);
    }

    @Override
    public void onDisable() {
        try {
            saveBans();
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public long getBanTime(final UUID playerId) {
        final Long playerBanTime;

        if (playerId == null)
            return 0;
        playerBanTime = this.banTimes.get(playerId);
        if (playerBanTime == null)
            return 0;
        if (System.currentTimeMillis() - playerBanTime >= this.banTime) {
            this.banTimes.remove(playerId);
            return 0;
        }
        return playerBanTime;
    }

    @Override
    public long getBanRemainingTime(final UUID playerId) {
        final long playerBanTime = getBanTime(playerId);
        final long result;

        if (playerBanTime == 0)
            return 0;
        result = this.banTime - System.currentTimeMillis() + playerBanTime;
        return result;
    }

    @Override
    public void setBanTime(final UUID playerId, final long time) {
        if (playerId == null)
            return;
        if (time <= 0)
            revokeBan(playerId);
        else
            this.banTimes.put(playerId, time);
    }

    @Override
    public void revokeBan(final UUID playerId) {
        if (playerId == null)
            return;
        this.banTimes.remove(playerId);
    }

}
