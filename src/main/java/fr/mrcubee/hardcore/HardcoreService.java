package fr.mrcubee.hardcore;

import org.bukkit.OfflinePlayer;

import java.util.Set;
import java.util.UUID;

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

    public Set<String> playerNameHasData();

    public long getLastDeathTime(final String playerName);
    public long getLastDeathTime(final UUID playerId);

    default public <T extends OfflinePlayer> long getDeathTime(final T player) {
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

    public boolean setLastDeathTime(final String playerName, long time);

    public boolean setLastDeathTime(final UUID playerId, long time);

    default public <T extends OfflinePlayer> boolean setBanTime(final T player, final long time) {
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
