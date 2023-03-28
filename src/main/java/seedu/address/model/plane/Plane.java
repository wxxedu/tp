package seedu.address.model.plane;

import static seedu.address.commons.util.LinkUtils.flightLink;
import static seedu.address.commons.util.LinkUtils.mapLinksToItems;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import seedu.address.model.Model;
import seedu.address.model.flight.Flight;
import seedu.address.model.item.Item;
import seedu.address.model.link.SingleLink;

/**
 * Represents a plane in the Wingman app.
 */
public class Plane implements Item {
    private static final String AGE_STRING = "Age";
    private static final String AVAILABILITY_STRING = "Status";
    private final String id;
    private final String model;
    private final int age;
    private boolean isAvailable;
    private final Set<SingleLink<Flight>> flightLinks;

    /**
     * Creates a plane with a random id.
     *
     * @param model       the model of the plane.
     * @param age         the age of the plane.
     * @param isAvailable the availability of the plane.
     * @param flightLinks the flight links of the plane.
     */
    public Plane(
            String model,
            int age,
            boolean isAvailable,
            Set<SingleLink<Flight>> flightLinks
    ) {
        this(UUID.randomUUID().toString(), model, age, isAvailable,
                flightLinks
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
            Set<SingleLink<Flight>> flightLinks
    ) {
        this.model = model;
        this.age = age;
        this.id = id;
        this.isAvailable = isAvailable;
        this.flightLinks = flightLinks;
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

    /**
     * Gets the flight links of the plane.
     *
     * @param modelOptional empty if the user does not want to update back
     *                      recursively. If this is not empty, then the
     *                      flights would be updated such that they contain
     *                      the information of this plane.
     * @param removeInvalid whether to remove invalid links.
     * @return the flight links of the plane.
     */
    public List<Flight> getFlights(
            Optional<Model> modelOptional,
            boolean removeInvalid
    ) {
        Set<SingleLink<Flight>> toBeRemoved = new HashSet<>();
        List<Flight> flights = mapLinksToItems(flightLinks, toBeRemoved)
                                       .collect(Collectors.toList());
        if (removeInvalid) {
            flightLinks.removeAll(toBeRemoved);
        }
        if (modelOptional.isPresent()) {
            Model model = modelOptional.get();
            flights.forEach(flight -> flight.setPlane(model, this, false));
        }
        return flights;
    }

    public List<Flight> getFlights(boolean removeInvalid) {
        return getFlights(Optional.empty(), removeInvalid);
    }

    public List<Flight> getFlights() {
        return getFlights(true);
    }

    public void addFlight(
            Model model,
            Flight flight,
            boolean recursive
    ) {
        SingleLink<Flight> link = flightLink(model, flight.getId());
        flightLinks.add(link);
        if (recursive) {
            flight.setPlane(model, this, false);
        }
    }

    /**
     * Adds this plane to a flight task and asks the flight to link back to
     * this plane.
     *
     * @param model  the model of the app.
     * @param flight the flight to link to.
     */
    public void addFlight(
            Model model,
            Flight flight
    ) {
        addFlight(model, flight, true);
    }

    /**
     * Removes this plane from a flight task.
     *
     * @param model     the model of the app.
     * @param flight    the flight to unlink from.
     * @param recursive whether to unlink back to this plane. If this is true,
     *                  then the plane will be removed from the flight.
     */
    public void removeFlight(
            Model model,
            Flight flight,
            boolean recursive
    ) {
        SingleLink<Flight> link = flightLink(model, flight.getId());
        flightLinks.remove(link);
        if (recursive) {
            flight.removePlane(model, this, false);
        }
    }

    /**
     * Removes this plane from a flight task and asks the flight to unlink
     * back to this plane.
     *
     * @param model  the model of the app.
     * @param flight the flight to unlink from.
     */
    public void removeFlight(
            Model model,
            Flight flight
    ) {
        removeFlight(model, flight, true);
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
