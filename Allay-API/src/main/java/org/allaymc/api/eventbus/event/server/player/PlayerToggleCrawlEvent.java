package org.allaymc.api.eventbus.event.server.player;

import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.eventbus.event.PlayerEvent;

/**
 * Allay Project 2024/8/3
 *
 * @author daoge_cmd
 */
public class PlayerToggleCrawlEvent extends PlayerEvent {
    protected boolean value;

    public PlayerToggleCrawlEvent(EntityPlayer player, boolean value) {
        super(player);
        this.value = value;
    }
}
