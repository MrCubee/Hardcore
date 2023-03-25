package fr.mrcubee.hardcore.service;

import fr.mrcubee.hardcore.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class DefaultHardcoreService implements HardcoreService {

    public long banTime;
    private transient BiFunction<PlayerData, Long, Long> endBanGeneratorFunction;
    private transient final Map<UUID, PlayerData> uuidPlayerData;

    public DefaultHardcoreService() {
        this.uuidPlayerData = new ConcurrentHashMap<UUID, PlayerData>();
        this.endBanGeneratorFunction = DefaultHardcoreService::defaultBanGenerator;
    }

    protected static Long defaultBanGenerator(final PlayerData playerData, final Long maxBanDuration) {
        final long lastDeathTime;
        final long deathCount;
        final long currentTime = System.currentTimeMillis();
        final long durationFromLastDeath;
        final long bonus;
        long result;

        if (playerData != null) {
            lastDeathTime = playerData.lastDeathTime;
            if (playerData.deathCount < 0)
                playerData.deathCount = 0;
            deathCount = playerData.deathCount;
        } else {
            lastDeathTime = 0;
            deathCount = 0;
        }
        if (lastDeathTime != 0) {
            durationFromLastDeath = currentTime - lastDeathTime;
            bonus = durationFromLastDeath / 14400000;
            playerData.deathCount = (int) Math.max(deathCount - bonus, 1);
            result = playerData.deathCount;
        } else
            result = 1;
        result = Math.min(3600000 * result, maxBanDuration);
        return currentTime + result;
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

    @Override
    public void setEndBanGeneratorFunction(final BiFunction<PlayerData, Long, Long> function) {
        if (function == null)
            this.endBanGeneratorFunction = DefaultHardcoreService::defaultBanGenerator;
        else
            this.endBanGeneratorFunction = function;
    }

    public void loadBanTime(final ConfigurationSection section) {
        if (section == null)
            return;
        this.banTime = getBanTimeFromSection(section);
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
        PlayerData playerData;

        if (this.uuidPlayerData.isEmpty())
            return null;
        configuration = new YamlConfiguration();
        for (final Map.Entry<UUID, PlayerData> playerDataEntry : this.uuidPlayerData.entrySet()) {
            playerData = playerDataEntry.getValue();
            if (playerData != null && !playerData.canDestroy())
                configuration.set(String.valueOf(playerDataEntry.getKey()), playerDataEntry.getValue());
        }
        return configuration;
    }

    private long generateEndBanTime(final PlayerData playerData) {
        final Long result = this.endBanGeneratorFunction.apply(playerData, this.banTime);

        if (result == null || result - System.currentTimeMillis() < 0)
            return 0;
        return result;
    }

    @Override
    public long generateEndBanTime(final String playerName) {
        return generateEndBanTime(getFromName(playerName));
    }

    @Override
    public long generateEndBanTime(final UUID playerId) {
        if (playerId == null)
            return 0;
        return generateEndBanTime(this.uuidPlayerData.get(playerId));
    }

    protected PlayerData getFromName(final String name) {
        final Player player;

        if (name == null)
            return null;
        player = Bukkit.getServer().getPlayer(name);
        if (player != null)
            return this.uuidPlayerData.get(player.getUniqueId());
        for (final PlayerData playerData : this.uuidPlayerData.values()) {
            if (playerData.name != null && playerData.name.equalsIgnoreCase(name))
                return playerData;
        }
        return null;
    }

    protected void removeIfUseless(final String key) {
        final PlayerData playerData;

        if (key == null)
            return;
        playerData = getFromName(key);
        if (playerData == null || !playerData.canDestroy())
            return;
        this.uuidPlayerData.values().removeIf(element -> element == playerData);
    }

    protected void removeIfUseless(final UUID key) {
        final PlayerData playerData;

        if (key == null)
            return;
        playerData = this.uuidPlayerData.get(key);
        if (playerData == null || !playerData.canDestroy())
            return;
        this.uuidPlayerData.remove(key);
    }

    protected PlayerData getOrCreate(final String playerName) {
        final Player player;
        PlayerData playerData;

        if (playerName == null)
            return null;
        playerData = getFromName(playerName);
        if (playerData != null)
            return playerData;
        player = Bukkit.getPlayerExact(playerName);
        if (player == null)
            return null;
        playerData = new PlayerData(player.getName());
        this.uuidPlayerData.put(player.getUniqueId(), playerData);
        return playerData;
    }

    protected PlayerData getOrCreate(final UUID playerId) {
        final Player player;
        PlayerData playerData;

        if (playerId == null)
            return null;
        playerData = this.uuidPlayerData.get(playerId);
        if (playerData != null)
            return playerData;
        player = Bukkit.getPlayer(playerId);
        if (player == null)
            return null;
        playerData = new PlayerData(player.getName());
        this.uuidPlayerData.put(player.getUniqueId(), playerData);
        return playerData;
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
    public PlayerData getPlayerData(final String playerName) {
        return getFromName(playerName);
    }

    @Override
    public PlayerData getPlayerData(final UUID playerId) {
        if (playerId == null)
            return null;
        return this.uuidPlayerData.get(playerId);
    }

    @Override
    public int getPlayerDeathCount(final String playerName) {
        final PlayerData playerData = getFromName(playerName);

        if (playerData == null)
            return 0;
        if (playerData.deathCount < 0)
            playerData.deathCount = 0;
        return playerData.deathCount;
    }

    @Override
    public int getPlayerDeathCount(final UUID playerId) {
        final PlayerData playerData;

        if (playerId == null)
            return 0;
        playerData = this.uuidPlayerData.get(playerId);
        if (playerData == null)
            return 0;
        if (playerData.deathCount < 0)
            playerData.deathCount = 0;
        return playerData.deathCount;
    }

    @Override
    public boolean setPlayerDeathCount(final String playerName, final int deathCount) {
        final PlayerData playerData = getOrCreate(playerName);

        if (playerData == null)
            return false;
        playerData.deathCount = Math.max(deathCount, 0);
        removeIfUseless(playerName);
        return true;
    }

    @Override
    public boolean setPlayerDeathCount(final UUID playerId, final int deathCount) {
        final PlayerData playerData = getOrCreate(playerId);

        if (playerData == null)
            return false;
        playerData.deathCount = Math.max(deathCount, 0);
        removeIfUseless(playerId);
        return true;
    }

    @Override
    public long getLastDeathTime(final String playerName) {
        final PlayerData playerData = getFromName(playerName);

        if (playerData == null)
            return 0;
        return playerData.lastDeathTime;
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
        deathTime = playerData.lastDeathTime;
        if (System.currentTimeMillis() - deathTime >= this.banTime) {
            this.uuidPlayerData.remove(playerId);
            return 0;
        }
        return deathTime;
    }

    protected long getBanRemainingTime(final PlayerData playerData) {
        final long result;

        if (playerData == null)
            return 0;
        if (playerData.endBanTime == 0)
            return 0;
        result = playerData.endBanTime - System.currentTimeMillis();
        return result > 0 ? result : 0;
    }

    @Override
    public long getBanRemainingTime(final String playerName) {
        if (playerName == null)
            return 0;
        return getBanRemainingTime(getFromName(playerName));
    }

    @Override
    public long getBanRemainingTime(final UUID playerId) {
        if (playerId == null)
            return 0;
        return getBanRemainingTime(this.uuidPlayerData.get(playerId));
    }

    @Override
    public boolean setEndBanTime(String playerName, long time) {
        final PlayerData playerData;

        if (time == 0 || time - System.currentTimeMillis() < 0)
            return revokeBan(playerName);
        playerData = getOrCreate(playerName);
        if (playerData == null)
            return false;
        playerData.endBanTime = time;
        removeIfUseless(playerName);
        return true;
    }

    @Override
    public boolean setEndBanTime(UUID playerId, long time) {
        final PlayerData playerData;

        if (time == 0 || time - System.currentTimeMillis() < 0)
            return revokeBan(playerId);
        playerData = getOrCreate(playerId);
        if (playerData == null)
            return false;
        playerData.endBanTime = time;
        removeIfUseless(playerId);
        return true;
    }

    @Override
    public boolean setLastDeathTime(final String playerName, final long time) {
        final PlayerData playerData = getOrCreate(playerName);

        if (playerData == null)
            return false;
        playerData.lastDeathTime = time;
        removeIfUseless(playerName);
        return true;
    }

    @Override
    public boolean setLastDeathTime(final UUID playerId, final long time) {
        final PlayerData playerData = getOrCreate(playerId);

        if (playerData == null)
            return false;
        playerData.lastDeathTime = time;
        removeIfUseless(playerId);
        return true;
    }

    @Override
    public boolean revokeBan(final String playerName) {
        final PlayerData playerData = getFromName(playerName);

        if (playerData == null)
            return false;
        playerData.endBanTime = 0;
        removeIfUseless(playerName);
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
        playerData.endBanTime = 0;
        removeIfUseless(playerId);
        return true;
    }

}
