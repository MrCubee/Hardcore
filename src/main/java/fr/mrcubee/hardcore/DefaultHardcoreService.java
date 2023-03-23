package fr.mrcubee.hardcore;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultHardcoreService implements HardcoreService {

    public long banTime;
    private transient final Map<UUID, PlayerData> uuidPlayerData;

    public DefaultHardcoreService() {
        this.uuidPlayerData = new ConcurrentHashMap<UUID, PlayerData>();
    }

    protected static long getBanTimeFromSection(final ConfigurationSection config) {
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

    public void loadBans(final YamlConfiguration configuration) {
        UUID playerId;
        PlayerData playerData;

        if (configuration == null)
            return;
        for (final String key : configuration.getKeys(false)) {
            try {
                playerId = UUID.fromString(key);
                playerData = configuration.getObject(key, PlayerData.class);
                if (playerData != null)
                    this.uuidPlayerData.put(playerId, playerData);
            } catch (final Exception e) {};
        }
    }

    public YamlConfiguration saveBans() {
        final YamlConfiguration configuration;

        if (this.uuidPlayerData.isEmpty())
            return null;
        configuration = new YamlConfiguration();
        for (final Map.Entry<UUID, PlayerData> banTime : this.uuidPlayerData.entrySet())
            configuration.set(String.valueOf(banTime.getKey()), banTime.getValue());
        return configuration;
    }

    protected PlayerData getFromName(final String name) {
        final Player player;

        if (name == null)
            return null;
        player = Bukkit.getServer().getPlayer(name);
        if (player != null)
            return this.uuidPlayerData.get(player.getUniqueId());
        for (final PlayerData playerData : this.uuidPlayerData.values()) {
            if (playerData.name != null && playerData.name.equalsIgnoreCase("name"))
                return playerData;
        }
        return null;
    }

    @Override
    public Set<String> playerNameHasData() {
        final Set<String> playerNames = new HashSet<String>();

        for (final PlayerData playerData : this.uuidPlayerData.values()) {
            if (playerData.name != null)
                playerNames.add(playerData.name);
        }
        return playerNames;
    }

    @Override
    public long getLastDeathTime(final String playerName) {
        final PlayerData playerData = getFromName(playerName);

        if (playerData == null)
            return 0;
        return playerData.deathTime;
    }

    @Override
    public long getLastDeathTime(final UUID playerId) {
        final PlayerData playerData;
        final long deathTime;

        if (playerId == null)
            return 0;
        playerData = this.uuidPlayerData.get(playerId);
        if (playerData == null)
            return 0;
        deathTime = playerData.deathTime;
        if (System.currentTimeMillis() - deathTime >= this.banTime) {
            this.uuidPlayerData.remove(playerId);
            return 0;
        }
        return deathTime;
    }

    @Override
    public long getBanRemainingTime(final String playerName) {
        final PlayerData playerData = getFromName(playerName);
        final long result;

        if (playerData == null)
            return 0;
        result = this.banTime - System.currentTimeMillis() + playerData.deathTime;
        return result > 0 ? result : 0;
    }

    @Override
    public long getBanRemainingTime(final UUID playerId) {
        final long playerDeathTime = getLastDeathTime(playerId);
        final long result;

        if (playerDeathTime == 0)
            return 0;
        result = this.banTime - System.currentTimeMillis() + playerDeathTime;
        return result > 0 ? result : 0;
    }

    @Override
    public boolean setLastDeathTime(final String playerName, final long time) {
        final Player player;
        PlayerData playerData = null;

        if (time <= 0)
            return revokeBan(playerName);
        for (final PlayerData registeredPlayerData : this.uuidPlayerData.values()) {
            if (registeredPlayerData.name != null && registeredPlayerData.name.equalsIgnoreCase(playerName)) {
                playerData = registeredPlayerData;
                break;
            }
        }
        if (playerData == null) {
            player = Bukkit.getPlayer(playerName);
            if (player == null)
                return false;
            playerData = new PlayerData(player.getName());
            this.uuidPlayerData.put(player.getUniqueId(), playerData);
        }
        playerData.deathTime = time;
        return true;
    }

    @Override
    public boolean setLastDeathTime(final UUID playerId, final long time) {
        PlayerData playerData;
        final Player player;

        if (playerId == null)
            return false;
        if (time <= 0)
            return revokeBan(playerId);
        playerData = this.uuidPlayerData.get(playerId);
        if (playerData == null) {
            player = Bukkit.getServer().getPlayer(playerId);
            playerData = new PlayerData(player == null ? null : player.getName());
            this.uuidPlayerData.put(playerId, playerData);
        }
        playerData.deathTime = time;
        return true;
    }

    @Override
    public boolean revokeBan(final String playerName) {
        final PlayerData playerData = getFromName(playerName);

        if (playerData == null)
            return false;
        playerData.deathTime = 0;
        if (playerData.deathCount == 0)
            this.uuidPlayerData.values().removeIf(element -> element == playerData);
        return true;
    }

    @Override
    public boolean revokeBan(final UUID playerId) {
        final PlayerData playerData;

        if (playerId == null)
            return false;
        playerData = this.uuidPlayerData.get(playerId);
        if (playerData == null)
            return false;
        playerData.deathTime = 0;
        if (playerData.deathCount == 0)
            this.uuidPlayerData.remove(playerId);
        return true;
    }

}
