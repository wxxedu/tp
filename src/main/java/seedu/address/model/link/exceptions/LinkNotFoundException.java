package seedu.address.model.link.exceptions;

public class LinkNotFoundException extends LinkException {
    /**
     * Creates a link exception.
     *
     * @param message the message of the link exception.
     */
    public LinkNotFoundException(String message) {
        super(message);
    }

    public static LinkNotFoundException formatted(
            String message,
            Object... args
    ) {
        return new LinkNotFoundException(String.format(message, args));
    }
}
