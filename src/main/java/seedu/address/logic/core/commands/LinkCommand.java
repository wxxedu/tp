package seedu.address.logic.core.commands;

import java.util.Map;

import seedu.address.model.item.Item;

/**
 * The base class for a link command, just so that we could reduce code
 * duplication.
 */
public abstract class LinkCommand<K, T extends Item> {
    private static final String DISPLAY_MESSAGE = "Linked %s to %s %s";
}
