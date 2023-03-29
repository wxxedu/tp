package seedu.address.model.link;

import java.util.Objects;
import java.util.Optional;

/**
 * The parameter passed in to create a link.
 */
public class LinkParam {
    /**
     * The name of the link param.
     */
    private String name;
    /**
     * The size of the link param. When it is empty, it means that the link
     * param is of infinite size.
     */
    private Optional<Integer> capacity;

    public LinkParam(String name, Optional<Integer> size) {
        this.name = name;
        this.capacity = size;
    }

    /**
     * Creates a single link param.
     *
     * @param name the name of the link param.
     * @return the link param.
     */
    public static LinkParam single(String name) {
        return new LinkParam(name, Optional.of(1));
    }

    /**
     * Creates a multiple link param.
     *
     * @param name the name of the link param.
     * @param size the size of the link param.
     * @return the link param.
     */
    public static LinkParam multiple(String name, int size) {
        return new LinkParam(name, Optional.of(size));
    }

    /**
     * Creates an infinite link param.
     *
     * @param id   the id of the link param.
     * @param name the name of the link param.
     * @return the link param.
     */
    public static LinkParam infinite(String id, String name) {
        return new LinkParam(name, Optional.empty());
    }

    public Optional<Integer> getCapacity() {
        return capacity;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof LinkParam)) {
            return false;
        }

        LinkParam other = (LinkParam) obj;
        return other.name.equals(name)
                       && other.capacity.equals(capacity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, capacity);
    }
}
