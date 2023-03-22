package fr.mrcubee.hardcore;

import org.bukkit.OfflinePlayer;

import java.util.UUID;

public interface HardcoreService {

    private static String formatNumber(final long value) {
        if (value < 10)
            return "0" + value;
        return String.valueOf(value);
    }

    public static String formatTime(final long time) {
        final StringBuilder strBuilder = new StringBuilder();

        strBuilder.append(formatNumber((time / 3600000) % 60));
        strBuilder.append(':');
        strBuilder.append(formatNumber((time / 60000) % 60));
        strBuilder.append(':');
        strBuilder.append(formatNumber((time / 1000) % 60));
        return strBuilder.toString();
    }

    public long getBanTime(final UUID playerId);

    default public <T extends OfflinePlayer> long getBanTime(final T player) {
        if (player == null)
            return 0;
        return getBanTime(player.getUniqueId());
    }

    public long getBanRemainingTime(final UUID playerId);

    default public <T extends OfflinePlayer> long getBanRemainingTime(final T player) {
        if (player == null)
            return 0;
        return getBanRemainingTime(player.getUniqueId());
    }

    public void setBanTime(final UUID playerId, long time);

    default public <T extends OfflinePlayer> void setBanTime(final T player, final long time) {
        if (player == null)
            return;
        setBanTime(player.getUniqueId(), time);
    }
    public void revokeBan(final UUID playerId);
    default public <T extends OfflinePlayer> void revokeBan(final T player) {
        if (player == null)
            return;
        revokeBan(player.getUniqueId());
    }



}
