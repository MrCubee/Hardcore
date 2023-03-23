package fr.mrcubee.hardcore;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class PlayerData implements ConfigurationSerializable {

    public String name;
    public int deathCount;
    public long deathTime;

    public PlayerData(final String name) {
        this.name = name;
        this.deathCount = 0;
        this.deathTime = 0;
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new HashMap<String, Object>(3);

        map.put("name", this.name);
        map.put("deathCount", this.deathCount);
        map.put("deathTime", this.deathTime);
        return map;
    }

    public static PlayerData deserialize(final Map<String, Object> map) {
        final Object nameObj = map.get("name");
        final Object deathCountObj = map.get("deathCount");
        final Object deathTimeObj = map.get("deathTime");
        final PlayerData result = new PlayerData(nameObj instanceof String ? (String) nameObj : null);

        result.deathCount = deathCountObj instanceof Integer ? (int) deathCountObj : 0;
        result.deathTime = deathTimeObj instanceof Long ? (long) deathTimeObj : 0;
        return result;
    }

}
