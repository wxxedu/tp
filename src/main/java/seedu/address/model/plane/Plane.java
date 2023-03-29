package seedu.address.model.plane;

import static seedu.address.commons.util.LinkUtils.flightLink;
import static seedu.address.commons.util.LinkUtils.mapLinksToItems;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.model.Model;
import seedu.address.model.flight.Flight;
import seedu.address.model.item.Item;
import seedu.address.model.link.Link;
import seedu.address.model.link.MultiLink;
import seedu.address.model.link.SingleLink;
import seedu.address.model.link.exceptions.LinkException;

/**
 * Represents a plane in the Wingman app.
 */
public class Plane implements Item {
    private static final String AGE_STRING = "Age";
    private static final String AVAILABILITY_STRING = "Status";
    private static Logger logger = Logger.getLogger("Plane");
    private final String id;
    private final String model;
    private final int age;
    private boolean isAvailable;
    private final MultiLink<Plane, Flight> flightLink;

    /**
     * Creates a plane with a random id.
     *
     * @param model       the model of the plane.
     * @param age         the age of the plane.
     * @param isAvailable the availability of the plane.
     * @param flightLink  the flight link of the plane.
     */
    public Plane(
            String model,
            int age,
            boolean isAvailable,
            MultiLink<Plane, Flight> flightLink
    ) {
        this(UUID.randomUUID().toString(), model, age, isAvailable,
                flightLink
        );
    }

    /**
     * Creates a plane with a specific given id.
     *
     * @param model the model of the plane.
     * @param age   the age of the plane.
     * @param id    the id of the plane.
     */
    public Plane(
            String id,
            String model,
            int age,
            boolean isAvailable,
            MultiLink<Plane, Flight> flightLink
    ) {
        this.model = model;
        this.age = age;
        this.id = id;
        this.isAvailable = isAvailable;
        this.flightLink = flightLink;
    }

    /**
     * Returns the id of the plane.
     *
     * @return the id of the plane.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Returns the model of the plane.
     *
     * @return the model of the plane.
     */
    public String getModel() {
        return this.model;
    }

    /**
     * Returns the age of the plane.
     *
     * @return the age of the plane.
     */
    public int getAge() {
        return this.age;
    }

    /**
     * Returns the availability of the plane.
     *
     * @return the availability of the plane.
     */
    public boolean isAvailable() {
        return this.isAvailable;
    }

    /**
     * Sets the availability of the plane to unavailable.
     */
    public void setUnavailable() {
        this.isAvailable = false;
    }

    /**
     * Sets the availability of the plane to available.
     */
    public void setAvailable() {
        this.isAvailable = true;
    }

    /**
     * Returns a String corresponding to the availability of the plane.
     *
     * @return the availability of the plane as a String
     */
    public String getAvailabilityString() {
        return (this.isAvailable)
                       ? "Available"
                       : "Unavailable";
    }

    public MultiLink<Plane, Flight> getFlightLink() {
        return this.flightLink;
    }

    public void addFlight(Flight flight) throws LinkException {
        this.flightLink.link(flight);
    }

    @Override
    public List<String> getDisplayList() {
        return List.of(
                String.format("%s", model),
                String.format("%s: %s", AGE_STRING, age),
                String.format(
                        "%s: %s",
                        AVAILABILITY_STRING,
                        getAvailabilityString()
                )
        );
    }

    @Override
    public String toString() {
        return String.format("%s (%s: %s)", model, AGE_STRING, age);
    }
}
