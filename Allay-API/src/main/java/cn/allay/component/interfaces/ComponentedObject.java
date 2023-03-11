package cn.allay.component.interfaces;

import java.util.List;

/**
 * Author: daoge_cmd <br>
 * Date: 2023/3/4 <br>
 * Allay Project <br>
 * <p>
 * The object which has been built by {@link ComponentInjector} <br/>
 * Any class spawned from {@link ComponentInjector} will implement this interface and can be used for judgment
 */
public interface ComponentedObject {
    /**
     * @return Components injected into this object
     */
    List<ComponentImpl> getComponents();
}
