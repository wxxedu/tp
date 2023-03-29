package seedu.address.model.link;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.model.ReadOnlyItemManager;
import seedu.address.model.item.Item;
import seedu.address.model.link.exceptions.LinkException;
import seedu.address.model.link.exceptions.LinkFullException;
import seedu.address.model.link.exceptions.LinkNotFoundException;

/**
 * A manager for bidirectional links.
 *
 * @param <S> the first type of item
 * @param <T> the second type of item
 */
public class BiLinkManager<S extends Item, T extends Item> {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LogsCenter.getLogger(BiLinkManager.class);
    /**
     * The manager for the first type of item.
     */
    private final ReadOnlyItemManager<S> sManager;
    /**
     * The manager for the second type of item.
     */
    private final ReadOnlyItemManager<T> tManager;
    /**
     * The links from the first type of item to the second type of item.
     */
    private final Map<String, Map<String, LinkEdge>> sToTLinks;
    /**
     * The links from the second type of item to the first type of item.
     */
    private final Map<String, Map<String, LinkEdge>> tToSLinks;

    /**
     * Creates a new link manager.
     *
     * @param sManager  the manager for the first type of item.
     * @param tManager  the manager for the second type of item.
     * @param sToTLinks the links from the first type of item to the second
     *                  type of item.
     * @param tToSLinks the links from the second type of item to the first
     *                  type of item.
     */
    public BiLinkManager(
            ReadOnlyItemManager<S> sManager,
            ReadOnlyItemManager<T> tManager,
            Map<String, Map<String, LinkEdge>> sToTLinks,
            Map<String, Map<String, LinkEdge>> tToSLinks
    ) {
        this.sManager = sManager;
        this.tManager = tManager;
        this.sToTLinks = sToTLinks;
        this.tToSLinks = tToSLinks;
    }

    /**
     * Creates a new link manager.
     *
     * @param sManager the manager for the first type of item.
     * @param tManager the manager for the second type of item.
     */
    public BiLinkManager(
            ReadOnlyItemManager<S> sManager,
            ReadOnlyItemManager<T> tManager
    ) {
        this(sManager, tManager, new HashMap<>(), new HashMap<>());
    }

    /**
     * Creates a link between the two items. If the link already exists, then
     * it will be overwritten.
     *
     * @param sToTLinkParam the link from the first item to the second item.
     * @param tToSLinkParam the link from the second item to the first item.
     */
    public void createLink(
            LinkParam sToTLinkParam,
            LinkParam tToSLinkParam
    ) {
        LinkEdge sToTEdge = new LinkEdge(sToTLinkParam.getCapacity());
        LinkEdge tToSEdge = new LinkEdge(tToSLinkParam.getCapacity());
        sToTLinks.computeIfAbsent(sToTLinkParam.getName(), k -> new HashMap<>())
                 .put(tToSLinkParam.getName(), sToTEdge);
        tToSLinks.computeIfAbsent(tToSLinkParam.getName(), k -> new HashMap<>())
                 .put(sToTLinkParam.getName(), tToSEdge);
    }

    /**
     * Creates a link between the two items.
     *
     * @param linkParam the link between the two items.
     */
    public void createSymmetricLink(LinkParam linkParam) {
        createLink(linkParam, linkParam);
    }

    /**
     * Checks whether if there is a link starting from the first item to the
     * second item.
     *
     * @param sId the name of the first item.
     * @param tId the name of the second item.
     * @return true if there is a link, false otherwise.
     */
    private boolean hasLinkDirectional(String sId, String tId) {
        return sToTLinks.containsKey(sId) &&
                       sToTLinks.get(sId).containsKey(tId);
    }

    /**
     * Checks whether if there is a link between the two items.
     *
     * @param sId the name of the first item.
     * @param tId the name of the second item.
     * @return true if there is a link, false otherwise.
     */
    public boolean hasLink(String sId, String tId) {
        return hasLinkDirectional(sId, tId) || hasLinkDirectional(tId, sId);
    }

    /**
     * This is a higher order function that returns the function that
     * maps one id to the item using the {@code manager}. If the item is not
     * found, then the id is added to the {@code invalidIds} list. So this
     * function is not pure. We shall remove the invalid ids later.
     * <p>
     * If the item is found, but the reverse link back to this first item does
     * not exist, then we mark this link as invalid as well. This may not be
     * flexible as it requires the declaration of links from both directions,
     * i.e. if {@code ClassA} has a link to {@code ClassB}, then we must
     * declare 2 links, one from {@code ClassA} to {@code ClassB} and one
     * from {@code ClassB} to {@code ClassA}.
     * <p>
     * If the reverse link does exist, but does not contain this item. Then
     * this method will try to put this item to the reverse link. If such
     * operation fails, then this link is marked as invalid.
     *
     * @param thisId           the id of the source item, i.e. the item that
     *                         contains the link.
     * @param linkName         the name of the link.
     * @param invalidIds       the list of invalid ids. This will not be
     *                         read, but would only be written to.
     * @param manager          the manager for the item type.
     * @param otherToThisLinks the links from the other item type to this item
     *                         type. This is used to check if the reverse
     *                         link exists.
     * @param <K>              the type of the item.
     * @return the function that maps the id to the item.
     */
    private static <K extends Item> Function<String, Optional<K>> getLinkHandler(
            String thisId,
            String linkName,
            List<String> invalidIds,
            ReadOnlyItemManager<K> manager,
            Map<String, Map<String, LinkEdge>> otherToThisLinks
    ) {
        return (id) -> {
            Optional<K> item = manager.getItemOptional(id);
            // if we cannot find the item, then the link must be invalid.
            if (item.isEmpty()) {
                invalidIds.add(id);
                return Optional.empty();
            }
            // if the reverse link does not exist, we mark this link as
            // invalid, which will then be removed.
            if (!otherToThisLinks.containsKey(id)) {
                invalidIds.add(id);
                return Optional.empty();
            }
            // if the reverse link does not contain the link name, we mark
            // the link as invalid, which will then be removed.
            Map<String, LinkEdge> linkMap = otherToThisLinks.get(id);
            if (!linkMap.containsKey(linkName)) {
                invalidIds.add(id);
                return Optional.empty();
            }
            try {
                // At this stage, if there are any inconsistencies in the
                // data structure of the reverse link, we no longer care. We
                // just put the item to the link to make sure that the link
                // contains the item.
                linkMap.get(linkName).put(thisId);
            } catch (LinkFullException e) {
                // However, the previous step may fail because the link may
                // be full. Under this circumstance, we mark the link as
                // invalid, which will then be removed.
                invalidIds.add(id);
                return Optional.empty();
            }
            return item;
        };
    }

    /**
     * Removes the invalid ids from the link.
     *
     * @param invalidIds the list of invalid ids.
     * @param edge       the link edge.
     */
    private static void handleInvalidIds(
            List<String> invalidIds,
            LinkEdge edge
    ) {
        if (!invalidIds.isEmpty()) {
            logger.warning(String.format(
                    "Removing invalid ids from link: %s",
                    String.join(", ", invalidIds)
            ));
            invalidIds.forEach(edge::remove);
        }
    }

    /**
     * Gets the link edge from a doubly-nested map. If the link edge does not
     * exist, then throws the exception.
     *
     * @param thisId              the id of the source item.
     * @param thisToOtherLinkName the name of the link from the source item.
     * @param otherToThisLinkName the name of the link from the target item.
     * @param thisToOtherLinks    the doubly-nested map.
     * @return the link edge.
     * @throws LinkNotFoundException if the link edge does not exist.
     */
    private static LinkEdge getLinkEdgeOrThrow(
            String thisId,
            String thisToOtherLinkName,
            String otherToThisLinkName,
            Map<String, Map<String, LinkEdge>> thisToOtherLinks
    ) throws LinkNotFoundException {
        // validation -> make sure that the item exists in the link manager.
        if (!thisToOtherLinks.containsKey(thisId)) {
            throw LinkNotFoundException.formatted(
                    "Link (%s - %s) for item with id %s not found.",
                    thisToOtherLinkName,
                    otherToThisLinkName,
                    thisId
            );
        }
        // validation -> make sure that the link name exists in the link of
        // the item.
        Map<String, LinkEdge> linkMap = thisToOtherLinks.get(thisId);
        if (!linkMap.containsKey(thisToOtherLinkName)) {
            throw LinkNotFoundException.formatted(
                    "Link name (%s) for link (%s - %s) for item with "
                            + "id %s not found.",
                    thisToOtherLinkName,
                    thisToOtherLinkName,
                    otherToThisLinkName,
                    thisId
            );
        }
        return linkMap.get(thisToOtherLinkName);
    }

    private static <K extends Item> List<K> getLinkedItemsCore(
            LinkEdge edge,
            String thisId,
            String otherToThisLinkName,
            ReadOnlyItemManager<K> manager,
            Map<String, Map<String, LinkEdge>> otherToThisLinks,
            List<String> invalidIds
    ) throws LinkException {
        return edge
                       .getIdList()
                       .stream()
                       .map(getLinkHandler(
                               thisId,
                               otherToThisLinkName,
                               invalidIds,
                               manager,
                               otherToThisLinks
                       ))
                       .filter(Optional::isPresent)
                       .map(Optional::get)
                       .collect(Collectors.toList());
    }

    private static <K extends Item> List<K> getLinkedItems(
            String thisId,
            String thisToOtherLinkName,
            String otherToThisLinkName,
            ReadOnlyItemManager<K> manager,
            Map<String, Map<String, LinkEdge>> thisToOtherLinks,
            Map<String, Map<String, LinkEdge>> otherToThisLinks
    ) throws LinkException {
        List<String> invalidIds = new ArrayList<>();
        LinkEdge edge = getLinkEdgeOrThrow(
                thisId,
                thisToOtherLinkName,
                otherToThisLinkName,
                thisToOtherLinks
        );
        List<K> result = getLinkedItemsCore(
                edge,
                thisId,
                otherToThisLinkName,
                manager,
                otherToThisLinks,
                invalidIds
        );
        handleInvalidIds(invalidIds, edge);
        return result;
    }

    /**
     * An edge in the link.
     */
    private static class LinkEdge {
        /**
         * The size of the link. If it is empty, then the link can contain
         * infinitely many items.
         */
        private final Optional<Integer> linkCapacity;
        /**
         * The list of ids of the items that are linked to the item
         */
        private final List<String> idList;
        /**
         * The items of this link as a hash set, this is used for faster
         * queries.
         */
        private final Set<String> idSet;

        /**
         * Creates an edge of a link.
         *
         * @param linkCapacity the capacity of the link.
         * @param ids          the list of ids of the items that are linked
         *                     to the item.
         */
        public LinkEdge(Optional<Integer> linkCapacity, List<String> ids) {
            this.linkCapacity = linkCapacity;
            this.idList = ids;
            this.idSet = new HashSet<>(ids);
        }

        /**
         * Creates an edge of a link.
         *
         * @param linkCapacity the capacity of the link.
         */
        public LinkEdge(Optional<Integer> linkCapacity) {
            this(linkCapacity, new ArrayList<>());
        }

        /**
         * Creates an edge of a link that can contain only one item.
         *
         * @return the link edge.
         */
        public static LinkEdge single() {
            return new LinkEdge(Optional.of(1), List.of());
        }

        /**
         * Creates an edge of a link that can contain only one item.
         *
         * @param id the id of the item that is to be linked.
         * @return the link edge.
         */
        public static LinkEdge single(String id) {
            return new LinkEdge(Optional.of(1), List.of(id));
        }

        /**
         * Creates an edge of a link that can contain multiple items.
         *
         * @param size the number of items that can be linked.
         * @return the link edge.
         */
        public static LinkEdge multiple(int size) {
            return new LinkEdge(Optional.of(size), List.of());
        }

        /**
         * Creates an edge of a link that can contain infinitely many items.
         *
         * @return the link edge.
         */
        public static LinkEdge infinite() {
            return new LinkEdge(Optional.empty(), List.of());
        }

        /**
         * Checks if the link edge has a capacity. If it does not, then it
         * can contain infinitely many items.
         *
         * @return true if the link edge has a size.
         */
        public boolean hasCapacity() {
            return linkCapacity.isPresent();
        }

        /**
         * Gets the capacity of the link edge. If the link edge does not have
         * a capacity, i.e. if it is infinite, then it will return
         * {@link Integer#MAX_VALUE}
         *
         * @return the size of the link edge.
         */
        public int capacity() {
            return linkCapacity.orElse(Integer.MAX_VALUE);
        }

        /**
         * Gets the size of the link edge.
         *
         * @return the size of the link edge.
         */
        public int size() {
            return idList.size();
        }

        /**
         * Checks if the link edge is full.
         *
         * @return true if the link edge is full.
         */
        public boolean isFull() {
            return hasCapacity() && size() >= capacity();
        }

        /**
         * Checks if the link edge is empty.
         *
         * @return true if the link edge is empty.
         */
        public boolean isEmpty() {
            return idList.isEmpty();
        }

        /**
         * Gets the list of ids of the items that are linked to the item.
         *
         * @return the list of ids.
         */
        public List<String> getIdList() {
            return Collections.unmodifiableList(idList);
        }

        /**
         * Gets the set of ids of the items that are linked to the item.
         *
         * @return the set of ids.
         */
        public Set<String> getIdSet() {
            return Collections.unmodifiableSet(idSet);
        }

        /**
         * Puts the given id to the link edge.
         *
         * @param id the id to be put.
         */
        public void put(String id) throws LinkFullException {
            if (idSet.contains(id)) {
                return;
            }
            if (isFull()) {
                throw new LinkFullException("Link is full");
            }
            idList.add(id);
            idSet.add(id);
        }

        /**
         * Removes the given id from the link edge.
         *
         * @param id the id to be removed.
         */
        public void remove(String id) {
            if (!idSet.contains(id)) {
                return;
            }
            idList.remove(id);
            idSet.remove(id);
        }

        /**
         * Clears the link edge.
         */
        public void clear() {
            idList.clear();
            idSet.clear();
        }
    }
}
