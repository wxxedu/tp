package seedu.address.logic.core.exceptions;

import seedu.address.commons.exceptions.IllegalValueException;

/**
 * Represents a parse error encountered by a parser.
 */
public class ParseException extends IllegalValueException {

    /**
     * Creates a new parse exception.
     *
     * @param message the message base.
     * @param args    the arguments to be formatted into the message.
     * @return the parse exception.
     */
    public static ParseException formatted(String message, Object... args) {
        return new ParseException(String.format(message, args));
    }

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
