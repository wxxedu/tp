package wingman.logic.toplevel.add;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import wingman.logic.core.CommandResult;
import wingman.logic.core.exceptions.CommandException;
import wingman.model.Model;
import wingman.model.item.Item;
import wingman.model.item.exceptions.DuplicateItemException;

/**
 * Test cases for the class {@code AddCommand}.
 */
@ExtendWith(MockitoExtension.class)
public class AddCommandTest {
    @Mock
    AddFunction<Item> addFunction;
    @Mock
    Item item;
    @Mock
    Model model;

    @Test
    public void testExecute_doesNotThrow() throws CommandException {
        AddCommand<Item> addCommand = new AddCommand<>(
                addFunction,
                item,
                "Item"
        );
        doNothing().when(addFunction).add(eq(model), eq(item));

        CommandResult result = addCommand.execute(model);
        verify(addFunction).add(eq(model), eq(item));

        assertTrue(result.getFeedbackToUser().contains("Added"));
    }

    @Test
    public void testExecute_throwsDuplicateItemException() throws CommandException {
        AddCommand<Item> addCommand = new AddCommand<>(
                addFunction,
                item,
                "Item"
        );
        doThrow(new DuplicateItemException(Item.class))
                .when(addFunction)
                .add(eq(model), eq(item));

        CommandResult result = addCommand.execute(model);
        verify(addFunction).add(eq(model), eq(item));

        assertTrue(result.getFeedbackToUser()
                         .contains("Duplication not allowed."));
    }
}
