package seedu.address.logic.toplevel.link;

import java.util.Map;
import java.util.function.Function;

import seedu.address.logic.core.Command;
import seedu.address.logic.core.CommandResult;
import seedu.address.logic.core.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.item.Item;

/**
 * The link command that links a map of K to S to the target T.
 *
 * @param <K> the type of the key.
 * @param <S> the type of the source.
 * @param <T> the type of the target.
 */
public class LinkCommand<K, S extends Item, T extends Item> implements Command {
    /**
     * The target.
     */
    private final T target;
    /**
     * The data map.
     */
    private final Map<K, S> dataMap;
    /**
     * The link function.
     */
    private final LinkFunction<K, S, T> linkFunction;

    /**
     * Creates a new link command.
     *
     * @param target       the target to be linked to.
     * @param dataMap      the data map to be linked to the target.
     * @param linkFunction the link function that links the data map to the
     *                     target.
     */
    public LinkCommand(
            T target,
            Map<K, S> dataMap,
            LinkFunction<K, S, T> linkFunction
    ) {
        this.target = target;
        this.dataMap = dataMap;
        this.linkFunction = linkFunction;
    }

    /**
     * Formats a map entry to the format of "key: value".
     *
     * @param entry the entry.
     * @param <K>   the type of the key.
     * @param <S>   the type of the value.
     * @return the formatted string.
     */
    private static <K, S extends Item> String formatMapEntry(
            Map.Entry<K, S> entry
    ) {
        return String.format("\n-\t%s: %s", entry.getKey(), entry.getValue());
    }

    @Override
    public String toString() {
        final String items = String.join(
                ", ",
                dataMap
                        .entrySet()
                        .stream()
                        .map(LinkCommand::formatMapEntry).toArray(String[]::new)
        );
        return String.format("Linked %s\nto %s.", items, target.toString());
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        linkFunction.link(target, dataMap);
        return new CommandResult(this.toString());
    }
}