package seedu.address.model.link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import seedu.address.model.ReadOnlyItemManager;
import seedu.address.model.item.Item;
import seedu.address.model.link.exceptions.LinkException;

/**
 * This describes a link to the target. It is meant to replace the {@link Link}
 * class because we figure that the {@link Link} class should not contain the
 * shape of the link.
 *
 * @param <T> the type of the target.
 */
public class MultiLink<S extends Item, T extends Item> {
    /**
     * The owner of the link.
     */
    private final S owner;

    /**
     * The supplier of the target manager.
     */
    private final Supplier<ReadOnlyItemManager<T>> managerSupplier;
    /**
     * The ids of the target items that this link is pointing to.
     */
    private final Collection<String> targetIds;
    /**
     * The size of the link.
     */
    private final Optional<Integer> sizeOptional;

    public MultiLink(
            S owner,
            Supplier<ReadOnlyItemManager<T>> managerSupplier,
            Collection<String> targetIds,
            Optional<Integer> sizeOptional
    ) {
        this.owner = owner;
        this.managerSupplier = managerSupplier;
        this.targetIds = targetIds;
        this.sizeOptional = sizeOptional;
    }

    /**
     * Creates a new link that can only have one target.
     *
     * @param managerSupplier the supplier of the target manager.
     * @param <T>             the type of the target.
     * @return the link.
     */
    public static <S extends Item, T extends Item> MultiLink<S, T> singleLink(
            S owner,
            Supplier<ReadOnlyItemManager<T>> managerSupplier
    ) {
        return new MultiLink<>(owner, managerSupplier, new ArrayList<>(),
                Optional.of(1)
        );
    }

    /**
     * Creates a new link that can have multiple targets.
     *
     * @param managerSupplier the supplier of the target manager.
     * @param <T>             the type of the target.
     * @return the link.
     */
    public static <S extends Item, T extends Item> MultiLink<S, T> setLink(
            S owner,
            Supplier<ReadOnlyItemManager<T>> managerSupplier
    ) {
        return new MultiLink<>(owner, managerSupplier, new HashSet<>(),
                Optional.empty()
        );
    }

    /**
     * Creates a new link that can have multiple targets.
     *
     * @param managerSupplier the supplier of the target manager.
     * @param size            the size of the link.
     * @param <T>             the type of the target.
     * @return the link.
     */
    public static <S extends Item, T extends Item> MultiLink<S, T> setLink(
            S owner,
            Supplier<ReadOnlyItemManager<T>> managerSupplier,
            int size
    ) {
        return new MultiLink<>(owner, managerSupplier, new HashSet<>(),
                Optional.of(size)
        );
    }

    /**
     * Creates a new link that can have multiple targets.
     *
     * @param managerSupplier the supplier of the target manager.
     * @param size            the size of the link.
     * @param <T>             the type of the target.
     * @return the link.
     */
    public static <S extends Item, T extends Item> MultiLink<S, T> listLink(
            S owner,
            Supplier<ReadOnlyItemManager<T>> managerSupplier,
            int size
    ) {
        return new MultiLink<>(owner, managerSupplier, new ArrayList<>(),
                Optional.of(size)
        );
    }

    /**
     * Creates a new link that can have multiple targets.
     *
     * @param managerSupplier the supplier of the target manager.
     * @param <T>             the type of the target.
     * @return the link.
     */
    public static <S extends Item, T extends Item> MultiLink<S, T> listLink(
            S owner,
            Supplier<ReadOnlyItemManager<T>> managerSupplier
    ) {
        return new MultiLink<>(owner, managerSupplier, new ArrayList<>(),
                Optional.empty()
        );
    }

    private Optional<T> handleHasItem(
            String id,
            Collection<String> invalidIds,
            Optional<MultiLink<T, S>> reverseLinkOptional
    ) {
        if (reverseLinkOptional.isPresent()) {
            MultiLink<T, S> reverseLink = reverseLinkOptional.get();
            try {
                reverseLink.link(owner);
            } catch (LinkException e) {
                invalidIds.add(id);
                return Optional.<T>empty();
            }
        }
        return Optional.of(managerSupplier.get().getItem(id));
    }

    private Optional<T> handleHasNotItem(Collection<String> invalidIds) {
        invalidIds.add(owner.getId());
        return Optional.<T>empty();
    }

    private Function<String, Optional<T>> getStreamHelper(
            Collection<String> invalidIds,
            Optional<MultiLink<T, S>> reverseLinkOptional
    ) {
        return (id) -> {
            if (managerSupplier.get().hasItem(id)) {
                return handleHasItem(id, invalidIds, reverseLinkOptional);
            } else {
                return handleHasNotItem(invalidIds);
            }
        };
    }

    private Stream<T> getStream(
            Optional<MultiLink<T, S>> reverseLinkOptional
            , boolean removeInvalid
    ) {
        List<String> invalidIds = new ArrayList<>();
        Stream<T> result = targetIds.stream()
                                    .map(getStreamHelper(
                                            invalidIds,
                                            reverseLinkOptional
                                    ))
                                    .filter(Optional::isPresent)
                                    .map(Optional::get);
        if (removeInvalid) {
            targetIds.removeAll(invalidIds);
        }
        return result;
    }

    /**
     * Gets the stream of the targets.
     *
     * @return the stream.
     */
    public Stream<T> getStream() {
        return getStream(Optional.empty(), false);
    }

    /**
     * Gets the stream of the targets.
     *
     * @param removeInvalid whether to remove the invalid ids from the link.
     * @return the stream.
     */
    public Stream<T> getStream(boolean removeInvalid) {
        return getStream(Optional.empty(), removeInvalid);
    }

    /**
     * Gets the stream of the targets.
     *
     * @param reverseLink the reverse link.
     * @return the stream.
     */
    public Stream<T> getStream(MultiLink<T, S> reverseLink) {
        return getStream(Optional.of(reverseLink), false);
    }

    /**
     * Gets the stream of the targets.
     *
     * @param reverseLink   the reverse link.
     * @param removeInvalid whether to remove the invalid ids from the link.
     * @return the stream.
     */
    public Stream<T> getStream(
            MultiLink<T, S> reverseLink,
            boolean removeInvalid
    ) {
        return getStream(Optional.of(reverseLink), removeInvalid);
    }

    /**
     * Links the target to the owner.
     *
     * @param target              the target.
     * @param reverseLinkOptional the reverse link.
     */
    private void link(
            T target,
            Optional<MultiLink<T, S>> reverseLinkOptional
    ) throws LinkException {
        boolean alreadyLinked = targetIds.contains(target.getId());
        if (sizeOptional.isPresent()) {
            if (targetIds.size() >= sizeOptional.get() && !alreadyLinked) {
                throw new LinkException("The link is full.");
            }
        }
        if (!alreadyLinked) {
            targetIds.add(target.getId());
        }
        if (reverseLinkOptional.isPresent()) {
            reverseLinkOptional.get().link(
                    owner,
                    Optional.empty()
            );
        }
    }

    /**
     * Links the target to the owner.
     *
     * @param target the target.
     * @throws LinkException if the link is full.
     */
    public void link(T target) throws LinkException {
        link(target, Optional.empty());
    }

    /**
     * Links the target to the owner.
     *
     * @param target      the target.
     * @param reverseLink the reverse link.
     * @throws LinkException if the link is full.
     */
    public void link(
            T target,
            MultiLink<T, S> reverseLink
    ) throws LinkException {
        link(target, Optional.of(reverseLink));
    }

    /**
     * Unlinks the target from the owner.
     *
     * @param target              the target.
     * @param reverseLinkOptional the reverse link.
     */
    private void unlink(
            T target,
            Optional<MultiLink<T, S>> reverseLinkOptional
    ) {
        targetIds.remove(target.getId());
        reverseLinkOptional.ifPresent(tsMultiLink -> tsMultiLink.unlink(
                owner,
                Optional.empty()
        ));
    }

    /**
     * Unlinks the target from the owner.
     *
     * @param target the target.
     */
    public void unlink(T target) {
        unlink(target, Optional.empty());
    }

    /**
     * Unlinks the target from the owner, while also unlinking the owner from
     * the reverseLink.
     *
     * @param target      the target.
     * @param reverseLink the reverse link.
     */
    public void unlink(T target, MultiLink<T, S> reverseLink) {
        unlink(target, Optional.of(reverseLink));
    }

    /**
     * Clears the link.
     */
    public void clear() {
        targetIds.clear();
    }
}
