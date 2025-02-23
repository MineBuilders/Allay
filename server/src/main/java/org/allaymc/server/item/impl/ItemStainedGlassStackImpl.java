package org.allaymc.server.item.impl;

import java.util.List;
import org.allaymc.api.component.interfaces.Component;
import org.allaymc.api.item.initinfo.ItemStackInitInfo;
import org.allaymc.api.item.interfaces.ItemStainedGlassStack;
import org.allaymc.server.component.interfaces.ComponentProvider;

public class ItemStainedGlassStackImpl extends ItemStackImpl implements ItemStainedGlassStack {
    public ItemStainedGlassStackImpl(ItemStackInitInfo initInfo,
            List<ComponentProvider<? extends Component>> componentProviders) {
        super(initInfo, componentProviders);
    }
}
