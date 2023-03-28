//package seedu.address.logic.toplevel.link;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//import java.util.Set;
//
//import seedu.address.commons.fp.Lazy;
//import seedu.address.logic.core.CommandFactory;
//import seedu.address.logic.core.CommandParam;
//import seedu.address.logic.core.exceptions.ParseException;
//import seedu.address.model.ReadOnlyItemManager;
//import seedu.address.model.exception.IndexOutOfBoundException;
//import seedu.address.model.item.Item;
//
///**
// * The factory that creates link commands.
// *
// * @param <K> the type of the key.
// * @param <S> the type of the source.
// * @param <T> the type of the target.
// */
//public class LinkCommandFactory<K, S extends Item, T extends Item>
//        implements CommandFactory<LinkCommand<K, S, T>> {
//    /**
//     * The lazy instance of the source manager.
//     */
//    private final Lazy<ReadOnlyItemManager<S>> lazySourceManager;
//
//    /**
//     * The lazy instance of the target manager.
//     */
//    private final Lazy<ReadOnlyItemManager<T>> lazyTargetManager;
//
//    /**
//     * The command word.
//     */
//    private final String commandWord;
//
//    /**
//     * The prefixes for the source, i.e. items that were to be added to the
//     * target.
//     */
//    private final Optional<Map<K, String>> prefixesMap;
//
//    /**
//     * The prefix for the target, i.e. the item to which the source items are
//     * added.
//     */
//    private final String targetPrefix;
//
//    /**
//     * The link function that links the source to the target.
//     */
//    private final LinkFunction<K, S, T> linkFunction;
//
//    /**
//     * Creates a new link command factory.
//     *
//     * @param commandWord       the command word.
//     * @param targetPrefix      the prefix for the target.
//     * @param sourcePrefixes    the prefixes for the source.
//     * @param lazySourceManager the lazy instance of the source manager.
//     * @param lazyTargetManager the lazy instance of the target manager.
//     * @param linkFunction      the link function that links the source to
//     *                          the target.
//     */
//    public LinkCommandFactory(
//            String commandWord,
//            String targetPrefix,
//            Optional<Map<K, String>> sourcePrefixes,
//            Lazy<ReadOnlyItemManager<S>> lazySourceManager,
//            Lazy<ReadOnlyItemManager<T>> lazyTargetManager,
//            LinkFunction<K, S, T> linkFunction
//    ) {
//        this.lazySourceManager = lazySourceManager;
//        this.lazyTargetManager = lazyTargetManager;
//        this.commandWord = commandWord;
//        this.prefixesMap = sourcePrefixes;
//        this.targetPrefix = targetPrefix;
//        this.linkFunction = linkFunction;
//    }
//
//    /**
//     * Gets an item from the item manager or throws a parse exception.
//     *
//     * @param indexStr    the index of the item.
//     * @param itemManager the item manager.
//     * @param <T>         the type of the item.
//     * @return the item.
//     * @throws ParseException if the index is missing or not a number.
//     */
//    private static <T extends Item> T getItemOrThrow(
//            String indexStr,
//            ReadOnlyItemManager<T> itemManager
//    ) throws ParseException {
//        int index;
//        try {
//            index = Integer.parseInt(indexStr) - 1;
//        } catch (NumberFormatException e) {
//            throw ParseException.formatted(
//                    "Index %s is not a number: %s.",
//                    indexStr, e.getMessage()
//            );
//        }
//        try {
//            return itemManager.getItem(index);
//        } catch (IndexOutOfBoundException e) {
//            throw ParseException.formatted(
//                    "Index %d is out of bound: %s.",
//                    index, e.getMessage()
//            );
//        }
//    }
//
//    /**
//     * Adds the source object with the index to the map under the key.
//     *
//     * @param indexOptional the index of the source object.
//     * @param key           the key.
//     * @param map           the map.
//     * @throws ParseException if the index is missing or not a number.
//     */
//    private void addSourceOrThrow(
//            Optional<String> indexOptional, K key,
//            Map<K, S> map
//    ) throws ParseException {
//        S source;
//        if (indexOptional.isEmpty() || indexOptional.get().isE) {
//            return;
//        }
//        try {
//            source = getItemOrThrow(indexOptional, lazySourceManager.get());
//        } catch (ParseException e) {
//            return false;
//        }
//        map.put(key, source);
//        return true;
//    }
//
//    private T getTargetOrThrow(Optional<String> indexOptional)
//            throws ParseException {
//        return getItemOrThrow(indexOptional, lazyTargetManager.get());
//    }
//
//    @Override
//    public String getCommandWord() {
//        return commandWord;
//    }
//
//    @Override
//    public Optional<Set<String>> getPrefixes() {
//        return prefixesMap.map(Map::values).map(Set::copyOf);
//    }
//
//    @Override
//    public LinkCommand<K, S, T> createCommand(CommandParam param)
//            throws ParseException {
//        Optional<String> targetIndexOptional =
//                param.getNamedValues(targetPrefix);
//        T target = getTargetOrThrow(targetIndexOptional);
//        Map<K, S> sources = new HashMap<>();
//        Set<Map.Entry<K, String>> entrySet =
//                prefixesMap.orElse(Map.of()).entrySet();
//        for (Map.Entry<K, String> entry : entrySet) {
//            addSourceOrThrow(
//                    param.getNamedValues(entry.getValue()),
//                    entry.getKey(),
//                    sources
//            );
//        }
//        if (sources.size() == 0) {
//            throw new ParseException("No source is specified.");
//        }
//        return new LinkCommand<>(target, sources, linkFunction);
//    }
//}
