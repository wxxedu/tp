package seedu.address.model.link;

import seedu.address.model.Model;
import seedu.address.model.item.Item;
import seedu.address.model.link.exceptions.LinkException;

/**
 * Describes a link to the target.
 *
 * @param <T> the type of the target.
 */
public abstract class LinkBase<T extends Item> {
    public abstract void link(Model model, T item) throws LinkException;
    public abstract void unlink(Model model, T item) throws LinkException;
}
