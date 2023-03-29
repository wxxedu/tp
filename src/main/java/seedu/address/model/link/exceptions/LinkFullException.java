package seedu.address.model.link.exceptions;

/**
 * The exception to be thrown when a link is full.
 */
public class LinkFullException extends LinkException {
    /**
     * Creates a link exception.
     *
     * @param message the message of the link exception.
     */
    public LinkFullException(String message) {
        super(message);
    }

    /**
     * Creates a link exception with a formatted message.
     *
     * @param message the message of the link exception.
     * @param args    the arguments to be formatted into the message.
     * @return the link exception.
     */
    public static LinkFullException formatted(String message, Object... args) {
        return new LinkFullException(String.format(message, args));
    }
}
