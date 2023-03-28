package seedu.address.logic.plane.linkflight;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.fp.Lazy;
import seedu.address.commons.util.GetUtil;
import seedu.address.logic.core.CommandFactory;
import seedu.address.logic.core.CommandParam;
import seedu.address.logic.core.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyItemManager;
import seedu.address.model.exception.IndexOutOfBoundException;
import seedu.address.model.flight.Flight;
import seedu.address.model.plane.Plane;

/**
 * The factory that creates {@code LinkPlaneCommand}.
 */
public class LinkPlaneToFlightCommandFactory implements CommandFactory<LinkPlaneToFlightCommand> {
    private static final String COMMAND_WORD = "linkflight";
    private static final String FLIGHT_PREFIX = "/fl";
    private static final String PLANE_USING_PREFIX = "/pl";

    private static final String NO_FLIGHT_MESSAGE =
            "No flight has been entered. Please enter /fl for the flight.";
    private static final String NO_PLANE_MESSAGE =
            "No plane has been entered. Please enter /pu for the plane being used in this link.";

    private final Lazy<ReadOnlyItemManager<Flight>> flightManagerLazy;
    private final Lazy<ReadOnlyItemManager<Plane>> planeManagerLazy;

    /**
     * Creates a new link command factory with the model registered.
     */
    public LinkPlaneToFlightCommandFactory() {
        this(GetUtil.getLazy(Model.class));
    }

    /**
     * Creates a new link command factory with the given modelLazy.
     *
     * @param modelLazy the modelLazy used for the creation of the link command factory.
     */
    public LinkPlaneToFlightCommandFactory(Lazy<Model> modelLazy) {
        this(
                modelLazy.map(Model::getFlightManager),
                modelLazy.map(Model::getPlaneManager)
        );
    }

    /**
     * Creates a new link plane command factory with the given plane manager
     * lazy and the flight manager lazy.
     *
     * @param flightManagerLazy the lazy instance of the flight manager.
     * @param planeManagerLazy  the lazy instance of the plane manager.
     */
    public LinkPlaneToFlightCommandFactory(
            Lazy<ReadOnlyItemManager<Flight>> flightManagerLazy,
            Lazy<ReadOnlyItemManager<Plane>> planeManagerLazy
    ) {
        this.flightManagerLazy = flightManagerLazy;
        this.planeManagerLazy = planeManagerLazy;
    }

    /**
     * Creates a new link plane command factory with the given plane manager
     * and the flight manager.
     *
     * @param flightManager the flight manager.
     * @param planeManager  the plane manager.
     */
    public LinkPlaneToFlightCommandFactory(
            ReadOnlyItemManager<Flight> flightManager,
            ReadOnlyItemManager<Plane> planeManager
    ) {
        this(
                Lazy.of(flightManager),
                Lazy.of(planeManager));
    }

    @Override
    public String getCommandWord() {
        return COMMAND_WORD;
    }

    @Override
    public Optional<Set<String>> getPrefixes() {
        return Optional.of(Set.of(
                FLIGHT_PREFIX,
                PLANE_USING_PREFIX
        ));
    }


    private boolean addPlane(
            Optional<String> planeIdOptional,
            FlightPlaneType type,
            Map<FlightPlaneType, Plane> target
    ) {
        if (planeIdOptional.isEmpty()) {
            return false;
        }
        int indexOfPlane =
                Integer.parseInt(planeIdOptional.get());
        Optional<Plane> planeOptional =
                planeManagerLazy.get().getItemOptional(indexOfPlane);
        if (planeOptional.isEmpty()) {
            return false;
        }
        target.put(type, planeOptional.get());
        return true;
    }

    private Flight getFlightOrThrow(
            Optional<String> flightIdOptional
    ) throws ParseException {
        if (flightIdOptional.isEmpty()) {
            throw new ParseException(NO_FLIGHT_MESSAGE);
        }
        int indexOfFlight =
                Integer.parseInt(flightIdOptional.get());
        Optional<Flight> flightOptional =
                flightManagerLazy.get().getItemOptional(indexOfFlight);
        if (flightOptional.isEmpty()) {
            throw new ParseException(NO_FLIGHT_MESSAGE);
        }
        return flightOptional.get();
    }


    @Override
    public LinkPlaneToFlightCommand createCommand(CommandParam param) throws ParseException, IndexOutOfBoundException {
        Optional<String> planeUsingIdOptional =
                param.getNamedValues(PLANE_USING_PREFIX);

        Flight flight = getFlightOrThrow(param.getNamedValues(FLIGHT_PREFIX));
        Map<FlightPlaneType, Plane> planes = new HashMap<>();

        boolean hasFoundPlane = addPlane(
                planeUsingIdOptional,
                FlightPlaneType.PLANE_USING,
                planes
        );

        if (!hasFoundPlane) {
            throw new ParseException(NO_PLANE_MESSAGE);
        }

        return new LinkPlaneToFlightCommand(flight, planes);
    }
}
