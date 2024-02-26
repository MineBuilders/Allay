package org.allaymc.server.command.selector.args;

import org.allaymc.api.command.CommandSender;
import org.allaymc.server.command.selector.ParseUtils;
import org.allaymc.api.command.selector.SelectorType;
import org.allaymc.api.command.selector.args.CachedSimpleSelectorArgument;
import org.allaymc.api.entity.Entity;
import org.allaymc.api.math.location.Location3fc;

import java.util.ArrayList;
import java.util.function.Predicate;


public class Name extends CachedSimpleSelectorArgument {
    @Override
    protected Predicate<Entity> cache(SelectorType selectorType, CommandSender sender, Location3fc basePos, String... arguments) {
        final var have = new ArrayList<String>();
        final var dontHave = new ArrayList<String>();
        for (var name : arguments) {
            boolean reversed = ParseUtils.checkReversed(name);
            if (reversed) {
                name = name.substring(1);
                dontHave.add(name);
            } else have.add(name);
        }
        return entity -> have.stream().allMatch(name -> entity.getCommandSenderName().equals(name)) && dontHave.stream().noneMatch(name -> entity.getCommandSenderName().equals(name));
    }

    @Override
    public String getKeyName() {
        return "name";
    }

    @Override
    public int getPriority() {
        return 4;
    }
}
