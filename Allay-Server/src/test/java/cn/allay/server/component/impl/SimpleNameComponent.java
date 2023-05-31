package cn.allay.server.component.impl;

import cn.allay.api.component.annotation.Impl;
import cn.allay.api.component.interfaces.ComponentImpl;
import cn.allay.api.identifier.Identifier;
import cn.allay.server.component.interfaces.NameComponent;

/**
 * Author: daoge_cmd <br>
 * Date: 2023/3/4 <br>
 * Allay Project <br>
 */
public class SimpleNameComponent implements NameComponent, ComponentImpl {

    private static final Identifier IDENTIFIER = new Identifier("minecraft:name_component");

    protected String name;

    public SimpleNameComponent(String name) {
        this.name = name;
    }

    @Override
    @Impl
    public String getName() {
        return name;
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }
}
