package cn.allay.api.blockentity.init;

import cn.allay.api.blockentity.BlockEntity;
import cn.allay.api.blockentity.type.BlockEntityType;
import cn.allay.api.component.interfaces.ComponentInitInfo;
import cn.allay.api.math.position.Position3ic;
import org.cloudburstmc.nbt.NbtMap;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3ic;

/**
 * Allay Project 2023/9/15
 *
 * @author daoge_cmd
 */
public interface BlockEntityInitInfo<T extends BlockEntity> extends ComponentInitInfo {

    Position3ic position();

    NbtMap nbt();

    @Nullable
    String customName();

    BlockEntityType<T> getBlockEntityType();

    void setBlockEntityType(BlockEntityType<T> blockEntityType);
}
