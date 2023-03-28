package seedu.address.model.link;

import java.util.Optional;
import java.util.function.Supplier;

import seedu.address.model.ReadOnlyItemManager;
import seedu.address.model.item.Item;
import seedu.address.model.link.exceptions.LinkException;

/**
 * This describes a link to the target. It is meant to replace the
 * {@link Link} class because we figure that the {@link Link} class should
 * not contain the shape of the link.
 *
 * @param <T> the type of the target.
 */
public class SingleLink<T extends Item> {
    /**
     * The supplier of the target manager.
     */
    private final Supplier<ReadOnlyItemManager<T>> managerSupplier;

    /**
     * The id of the target item that this link is pointing to.
     */
    private String targetId;

    /**
     * Creates a new link.
     *
     * @param managerSupplier the supplier of the target manager.
     * @param targetId        the id of the target item.
     */
    public SingleLink(
            Supplier<ReadOnlyItemManager<T>> managerSupplier,
            String targetId
    ) {
        this.managerSupplier = managerSupplier;
        this.targetId = targetId;
    }

    /**
     * Gets the target item that this link is pointing to.
     *
     * @return the target item.
     */
    public Optional<T> getOptional() {
        return managerSupplier.get().getItemOptional(targetId);
    }

    /**
     * Gets the target item that this link is pointing to.
     *
     * @return the target item.
     * @throws LinkException if the item is not in the manager.
     */
    public T get() throws LinkException {
        return getOptional().orElseThrow(() -> LinkException.formatted(
                "Item not found: %s.", targetId));
    }

    /**
     * Sets the target item that this link is pointing to.
     *
     * @param item the item to be set.
     * @throws LinkException if the item is not in the manager.
     */
    public void set(T item) throws LinkException {
        if (!managerSupplier.get().hasItem(item)) {
            throw LinkException.formatted(
                    "%s is not found in the manager, maybe the data is "
                            + "corrupted or it has been deleted.",
                    item
            );
        }
        targetId = item.getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SingleLink<?>)) {
            return false;
        }
        SingleLink<?> other = (SingleLink<?>) obj;
        // we are assuming that the target id is unique across all types,
        // which is quite safe for UUIDs.
        return targetId.equals(other.targetId);
    }

    @Override
    public int hashCode() {
        return targetId.hashCode();
    }
}
