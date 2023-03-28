package seedu.address.logic.toplevel.link;

import java.util.Map;

import seedu.address.logic.core.exceptions.CommandException;
import seedu.address.model.item.Item;

/**
 * The functional interface that links an item to the target.
 *
 * @param <K> the type of the key.
 * @param <S> the type of the source item that will be added to the targed.
 * @param <T> the type of the target item to which the source items are added.
 */
@FunctionalInterface
public interface LinkFunction<K, S extends Item, T extends Item> {
    /**
     * Links the items to the target.
     *
     * @param target the target.
     * @param items  a map of items to be added to the target.
     */
    void link(T target, Map<K, S> items) throws CommandException;
}
