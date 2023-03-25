package fr.mrcubee.hardcore;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class PlayerData implements ConfigurationSerializable {

    public String name;
    public int deathCount;
    public long endBanTime;
    public long lastDeathTime;

    public PlayerData(final String name) {
        this.name = name;
        this.deathCount = 0;
        this.endBanTime = 0;
        this.lastDeathTime = 0;
    }

    public boolean canDestroy() {
        return this.deathCount == 0 && this.endBanTime == 0 && this.lastDeathTime == 0;
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new HashMap<String, Object>(3);

        map.put("name", this.name);
        map.put("deathCount", this.deathCount);
        map.put("endBanTime", this.endBanTime);
        map.put("lastDeathTime", this.lastDeathTime);
        return map;
    }

    public static PlayerData deserialize(final Map<String, Object> map) {
        final Object nameObj = map.get("name");
        final Object deathCountObj = map.get("deathCount");
        final Object endBanTimeObj = map.get("endBanTime");
        final Object lastDeathTimeObj = map.get("lastDeathTime");
        final PlayerData result = new PlayerData(nameObj instanceof String ? (String) nameObj : null);

        result.deathCount = deathCountObj instanceof Integer ? (int) deathCountObj : 0;
        result.endBanTime = endBanTimeObj instanceof Long ? (long) endBanTimeObj : 0;
        result.lastDeathTime = lastDeathTimeObj instanceof Long ? (long) lastDeathTimeObj : 0;
        return result;
    }

}
