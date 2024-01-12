package org.allaymc.api.perm.tree;

import org.allaymc.api.ApiInstanceHolder;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Allay Project 2023/12/30
 *
 * @author daoge_cmd
 */
public interface PermTree {

    ApiInstanceHolder<PermTreeFactory> FACTORY = ApiInstanceHolder.of();

    static PermTree create() {
        return FACTORY.get().create();
    }

    PermTree registerPermListener(String perm, Consumer<PermChangeType> callback);

    @UnmodifiableView
    Map<String, Consumer<PermChangeType>> getPermListeners();

    void clear();

    enum PermChangeType {
        ADD,
        REMOVE
    }

    PermNode getRoot();

    PermTree addPerm(String perm, boolean callListener);

    default PermTree addPerm(String perm) {
        return addPerm(perm, true);
    }

    default PermTree setPerm(String perm, boolean value) {
        if (value) addPerm(perm);
        else removePerm(perm);
        return this;
    }

    boolean hasPerm(String perm);

    PermTree removePerm(String perm, boolean callListener);

    default PermTree removePerm(String perm) {
        return removePerm(perm, true);
    }

    PermTree extendFrom(PermTree parent);

    PermTree copyFrom(PermTree parent);

    List<PermNode> getLeaves();

    boolean isOp();

    PermTree setOp(boolean op);


    PermTree getParent();

    interface PermTreeFactory {
        PermTree create();
    }
}
