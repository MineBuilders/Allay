package cn.allay.server.utils.palette;

import org.cloudburstmc.nbt.NbtMap;

/**
 * Author: JukeboxMC | daoge_cmd <br>
 * Date: 2023/4/14 <br>
 * Allay Project <br>
 */
public interface PersistentDataDeserializer<V> {
    V deserialize(NbtMap nbtMap);
}
