package fr.mrcubee.hardcore.service;

import fr.mrcubee.hardcore.PlayerData;
import org.bukkit.OfflinePlayer;

import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;

public interface HardcoreService {

    private static String formatNumber(final long value) {
        if (value < 10)
            return "0" + value;
        return String.valueOf(value);
    }

    public static String formatTime(final long time) {
        final StringBuilder strBuilder = new StringBuilder();

        strBuilder.append(formatNumber(time / 3600000));
        strBuilder.append(':');
        strBuilder.append(formatNumber((time / 60000) % 60));
        strBuilder.append(':');
        strBuilder.append(formatNumber((time / 1000) % 60));
        return strBuilder.toString();
    }

    public void setEndBanGeneratorFunction(BiFunction<PlayerData, Long, Long> function);

    public long generateEndBanTime(final String playerName);
    public long generateEndBanTime(final UUID playerId);

    default public <T extends OfflinePlayer> long generateEndBanTime(final T player) {
        if (player == null)
            return 0;
        return generateEndBanTime(player.getUniqueId());
    }


    public Set<String> playerNameHasData();

    public PlayerData getPlayerData(final String playerName);
    public PlayerData getPlayerData(final UUID playerId);

    default public <T extends OfflinePlayer> PlayerData getPlayerData(final T player) {
        if (player == null)
            return null;
        return getPlayerData(player.getUniqueId());
    }

    public int getPlayerDeathCount(final String playerName);
    public int getPlayerDeathCount(final UUID playerId);

    default public <T extends OfflinePlayer> int getPlayerDeathCount(final T player) {
        if (player == null)
            return 0;
        return getPlayerDeathCount(player.getUniqueId());
    }

    public boolean setPlayerDeathCount(final String playerName, final int deathCount);
    public boolean setPlayerDeathCount(final UUID playerId, final int deathCount);

    default public <T extends OfflinePlayer> boolean setPlayerDeathCount(final T player, final int deathCount) {
        if (player == null)
            return false;
        return setPlayerDeathCount(player.getUniqueId(), deathCount);
    }

    public long getLastDeathTime(final String playerName);
    public long getLastDeathTime(final UUID playerId);

    default public <T extends OfflinePlayer> long getLastDeathTime(final T player) {
        if (player == null)
            return 0;
        return getLastDeathTime(player.getUniqueId());
    }

    public long getBanRemainingTime(final String playerName);
    public long getBanRemainingTime(final UUID playerId);

    default public <T extends OfflinePlayer> long getBanRemainingTime(final T player) {
        if (player == null)
            return 0;
        return getBanRemainingTime(player.getUniqueId());
    }

    public boolean setEndBanTime(final String playerName, long time);

    public boolean setEndBanTime(final UUID playerId, long time);

    default public <T extends OfflinePlayer> boolean setEndBanTime(final T player, final long time) {
        if (player == null)
            return false;
        return setEndBanTime(player.getUniqueId(), time);
    }

    public boolean setLastDeathTime(final String playerName, long time);

    public boolean setLastDeathTime(final UUID playerId, long time);

    default public <T extends OfflinePlayer> boolean setLastDeathTime(final T player, final long time) {
        if (player == null)
            return false;
        return setLastDeathTime(player.getUniqueId(), time);
    }

    public boolean revokeBan(final String playerName);

    public boolean revokeBan(final UUID playerId);

    default public <T extends OfflinePlayer> boolean revokeBan(final T player) {
        if (player == null)
            return false;
        return revokeBan(player.getUniqueId());
    }

}
