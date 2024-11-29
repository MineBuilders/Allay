package org.allaymc.api.form.element;

import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a label element in a {@link org.allaymc.api.form.type.CustomForm}.
 *
 * @author daoge_cmd
 */
public final class Label extends CustomFormElement {

    @SuppressWarnings("unused")
    private final String type = "label";

    @Getter
    private final String text;

    /**
     * Create a new label.
     *
     * @param text the text of the label.
     */
    public Label(String text) {
        this.text = text;
    }

    @Override
    @ApiStatus.Internal
    public void syncDefaultValueToResponse(String response) {
        // Do nothing
    }
}
