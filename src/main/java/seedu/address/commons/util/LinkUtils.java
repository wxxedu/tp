package seedu.address.commons.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import seedu.address.model.Model;
import seedu.address.model.ReadOnlyItemManager;
import seedu.address.model.crew.Crew;
import seedu.address.model.flight.Flight;
import seedu.address.model.item.Item;
import seedu.address.model.link.SingleLink;
import seedu.address.model.location.Location;
import seedu.address.model.pilot.Pilot;
import seedu.address.model.plane.Plane;

/**
 * A utility class for link.
 */
public abstract class LinkUtils {

    /**
     * Maps a collection of links to a stream of items.
     *
     * @param links        the collection of links.
     * @param invalidLinks the collection of invalid links. This should be an
     *                     empty collection that will be filled with the invalid
     *                     links.
     * @param <T>          the type of the item.
     * @return the stream of items.
     */
    public static <T extends Item> Stream<T> mapLinksToItems(
            Collection<SingleLink<T>> links,
            Collection<SingleLink<T>> invalidLinks
    ) {
        return links
                       .stream()
                       .map((x) -> {
                           Optional<T> result = x.getOptional();
                           if (result.isEmpty()) {
                               invalidLinks.add(x);
                           }
                           return result;
                       })
                       .filter(Optional::isPresent)
                       .map(Optional::get);
    }

    /**
     * Maps a collection of links to a stream of items.
     *
     * @param links the collection of links.
     * @param <T>   the type of the item.
     * @return the stream of items.
     */
    public static <T extends Item> Stream<T> mapLinksToItems(
            Collection<SingleLink<T>> links
    ) {
        return mapLinksToItems(links, new ArrayList<>());
    }

    /**
     * Maps a collection of links to a list of items.
     *
     * @param managerSupplier the supplier of the target manager.
     * @param ids             the collection of ids.
     * @param <T>             the type of the item.
     * @return the list of items.
     */
    public static <T extends Item> Stream<SingleLink<T>> mapIdsToLinks(
            Supplier<ReadOnlyItemManager<T>> managerSupplier,
            Collection<String> ids
    ) {
        return ids
                       .stream()
                       .map(id -> new SingleLink<>(managerSupplier, id));
    }

    /**
     * Creates a link to a plane.
     *
     * @param model   the model.
     * @param planeId the id of the plane.
     * @return the link to the plane.
     */
    public static SingleLink<Plane> planeLink(Model model, String planeId) {
        return new SingleLink<>(model::getPlaneManager, planeId);
    }

    /**
     * Creates a link to a pilot.
     *
     * @param model   the model.
     * @param pilotId the id of the pilot.
     * @return the link to the pilot.
     */
    public static SingleLink<Pilot> pilotLink(Model model, String pilotId) {
        return new SingleLink<>(model::getPilotManager, pilotId);
    }

    /**
     * Creates a link to a crew.
     *
     * @param model  the model.
     * @param crewId the id of the crew.
     * @return the link to the crew.
     */
    public static SingleLink<Crew> crewLink(Model model, String crewId) {
        return new SingleLink<>(model::getCrewManager, crewId);
    }

    /**
     * Creates a link to a flight.
     *
     * @param model    the model.
     * @param flightId the id of the flight.
     * @return the link to the flight.
     */
    public static SingleLink<Flight> flightLink(Model model, String flightId) {
        return new SingleLink<>(model::getFlightManager, flightId);
    }

    /**
     * Creates a link to a location.
     *
     * @param model      the model.
     * @param locationId the id of the location.
     * @return the link to the location.
     */
    public static SingleLink<Location> locationLink(
            Model model,
            String locationId
    ) {
        return new SingleLink<>(model::getLocationManager, locationId);
    }

}
