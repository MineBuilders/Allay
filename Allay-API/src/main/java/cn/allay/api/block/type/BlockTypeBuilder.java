package cn.allay.api.block.type;

import cn.allay.api.ApiInstanceHolder;
import cn.allay.api.block.BlockBehavior;
import cn.allay.api.block.component.BlockComponentImpl;
import cn.allay.api.block.component.impl.custom.CustomBlockComponentImpl;
import cn.allay.api.block.property.type.BlockPropertyType;
import cn.allay.api.component.interfaces.ComponentProvider;
import cn.allay.api.data.VanillaBlockId;
import cn.allay.api.identifier.Identifier;

import java.util.List;

/**
 * Allay Project 2023/3/19
 *
 * @author daoge_cmd
 */
public interface BlockTypeBuilder<T extends BlockBehavior> {

    ApiInstanceHolder<BlockTypeBuilderFactory> FACTORY = ApiInstanceHolder.of();

    static <T extends BlockBehavior> BlockTypeBuilder<T> builder(Class<T> clazz) {
        return FACTORY.get().create(clazz);
    }

    BlockTypeBuilder<T> identifier(Identifier identifier);

    BlockTypeBuilder<T> identifier(String identifier);

    BlockTypeBuilder<T> vanillaBlock(VanillaBlockId vanillaBlockId);

    BlockTypeBuilder<T> vanillaBlock(VanillaBlockId vanillaBlockId, boolean initVanillaBlockAttributeComponent);

    BlockTypeBuilder<T> withProperties(BlockPropertyType<?>... properties);

    BlockTypeBuilder<T> withProperties(List<BlockPropertyType<?>> properties);

    BlockTypeBuilder<T> setComponents(List<ComponentProvider<? extends BlockComponentImpl>> componentProviders);

    BlockTypeBuilder<T> addComponents(List<ComponentProvider<? extends BlockComponentImpl>> componentProviders);

    BlockTypeBuilder<T> addComponent(ComponentProvider<? extends BlockComponentImpl> componentProvider);

    BlockTypeBuilder<T> addBasicComponents();

    BlockTypeBuilder<T> addCustomBlockComponent(CustomBlockComponentImpl customBlockComponent);

    BlockType<T> build();

    interface BlockTypeBuilderFactory {
        <T extends BlockBehavior> BlockTypeBuilder<T> create(Class<T> clazz);
    }
}
