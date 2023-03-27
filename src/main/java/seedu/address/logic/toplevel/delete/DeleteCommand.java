package seedu.address.logic.toplevel.delete;


import seedu.address.logic.core.Command;
import seedu.address.logic.core.CommandResult;
import seedu.address.logic.core.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.exception.IndexOutOfBoundException;
import seedu.address.model.item.Item;

/**
 * The command that deletes an item.
 */
public class DeleteCommand<T extends Item> implements Command {
    /**
     * The item.
     */
    private final int itemIndex;

    /**
     * Delete function that deletes the item.
     */
    private final DeleteFunction<T> deleteFunction;

    /**
     * The function that gets the manager.
     */
    private final GetManagerFunction<T> getManagerFunction;

    /**
     * The command that deletes an item.
     *
     * @param itemIndex          the index of the item
     * @param getManagerFunction the function for getting the manager.
     * @param deleteFunction     the function for deleting the item.
     */
    public DeleteCommand(
            int itemIndex,
            GetManagerFunction<T> getManagerFunction,
            DeleteFunction<T> deleteFunction
    ) {
        this.itemIndex = itemIndex;
        this.deleteFunction = deleteFunction;
        this.getManagerFunction = getManagerFunction;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        T toDelete;
        try {
            toDelete = getManagerFunction.get(model).getItem(itemIndex);
        } catch (IndexOutOfBoundException e) {
            throw new CommandException(String.format(
                    "Please enter a valid index: %s.",
                    e.getMessage()
            ));
        }
        deleteFunction.delete(model, toDelete);
        return new CommandResult(String.format(
                "Deleted item: %s.",
                toDelete.toString()
        ));
    }
}
