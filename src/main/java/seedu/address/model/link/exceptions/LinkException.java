package seedu.address.model.link.exceptions;

/**
 * The exception that happens with a link.
 */
public class LinkException extends Exception {
    /**
     * Creates a link exception.
     *
     * @param message the message of the link exception.
     */
    public LinkException(String message) {
        super(message);
    }

    /**
     * Creates a link exception that is formatted.
     *
     * @param message the message of the link exception.
     * @param args    the arguments to be formatted into the message.
     * @return the link exception.
     */
    public static LinkException formatted(String message, Object... args) {
        return new LinkException(String.format(message, args));
    }
}
