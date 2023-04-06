package wingman.logic.pilot.linkflight;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import wingman.commons.fp.Lazy;
import wingman.commons.util.GetUtil;
import wingman.logic.core.Command;
import wingman.logic.core.CommandParam;
import wingman.logic.core.exceptions.ParseException;
import wingman.logic.toplevel.link.LinkFactoryBase;
import wingman.logic.toplevel.link.LinkFunction;
import wingman.model.Model;
import wingman.model.ReadOnlyItemManager;
import wingman.model.exception.IndexOutOfBoundException;
import wingman.model.flight.Flight;
import wingman.model.pilot.FlightPilotType;
import wingman.model.pilot.Pilot;

/**
 * The factory that's responsible for creating a new
 * <code>CommandFactory</code>.
 */
public class PilotFlightLinkCommandFactory<T extends Command>
        extends LinkFactoryBase<T, Flight, Pilot, FlightPilotType> {
    private static final String COMMAND_WORD = "linkflight";
    private static final String FLIGHT_PREFIX = "/fl";
    private static final String PILOT_FLYING_PREFIX = "/pf";
    private static final String PILOT_MONITORING_PREFIX = "/pm";
    private static final String NO_PILOT_MESSAGE =
            "No pilot has been entered.\n"
                    + "Please enter at least 1 of the following:\n"
                    + "     /pm for the Pilot Monitoring, "
                    + "/pf for the Pilot Flying.";

    private final PilotFlightLinkFunction<T> linkFunction;

    /**
     * Creates a new link command factory with the model registered.
     */
    public PilotFlightLinkCommandFactory(
            PilotFlightLinkFunction<T> linkFunction
    ) {
        this(GetUtil.getLazy(Model.class), linkFunction);
    }

    /**
     * Creates a new link command factory with the given modelLazy.
     *
     * @param modelLazy the modelLazy used for the creation of the link command factory.
     */
    public PilotFlightLinkCommandFactory(
            Lazy<Model> modelLazy,
            PilotFlightLinkFunction<T> linkFunction
    ) {
        this(
                modelLazy.map(Model::getFlightManager),
                modelLazy.map(Model::getPilotManager),
                linkFunction
        );
    }

    /**
     * Creates a new link pilot command factory with the given pilot manager
     * lazy and the flight manager lazy.
     *
     * @param flightManagerLazy the lazy instance of the flight manager.
     * @param pilotManagerLazy  the lazy instance of the pilot manager.
     */
    public PilotFlightLinkCommandFactory(
            Lazy<ReadOnlyItemManager<Flight>> flightManagerLazy,
            Lazy<ReadOnlyItemManager<Pilot>> pilotManagerLazy,
            PilotFlightLinkFunction<T> linkFunction
    ) {
        super(flightManagerLazy, pilotManagerLazy);
        this.linkFunction = linkFunction;
    }

    /**
     * Creates a new link command factory with the given model.
     *
     * @return the new link command factory.
     */
    public static PilotFlightLinkCommandFactory<LinkPilotToFlightCommand> linkFactory() {
        return new PilotFlightLinkCommandFactory<>(LinkPilotToFlightCommand::new);
    }

    /**
     * Creates a new unlink command factory with the given model.
     *
     * @return the new unlink command factory.
     */
    public static PilotFlightLinkCommandFactory<UnlinkPilotToFlightCommand> unlinkFactory() {
        return new PilotFlightLinkCommandFactory<>(UnlinkPilotToFlightCommand::new);
    }

    @Override
    public String getCommandWord() {
        return COMMAND_WORD;
    }

    @Override
    public Optional<Set<String>> getPrefixes() {
        return Optional.of(Set.of(
                FLIGHT_PREFIX,
                PILOT_FLYING_PREFIX,
                PILOT_MONITORING_PREFIX
        ));
    }

    @Override
    public T createCommand(CommandParam param)
            throws ParseException, IndexOutOfBoundException {
        Optional<String> pilotFlyingIdOptional =
                param.getNamedValues(PILOT_FLYING_PREFIX);
        Optional<String> pilotMonitoringIdOptional =
                param.getNamedValues(PILOT_MONITORING_PREFIX);

        Flight flight = getSourceOrThrow(param.getNamedValues(FLIGHT_PREFIX));

        Map<FlightPilotType, Pilot> pilots = new HashMap<>();

        boolean hasFoundPilot = addTarget(
                pilotFlyingIdOptional,
                FlightPilotType.PILOT_FLYING,
                pilots
        ) || addTarget(
                pilotMonitoringIdOptional,
                FlightPilotType.PILOT_MONITORING,
                pilots
        );

        if (!hasFoundPilot) {
            throw new ParseException(NO_PILOT_MESSAGE);
        }

        return linkFunction.apply(flight, pilots);
    }

    @FunctionalInterface
    public interface PilotFlightLinkFunction<T extends Command>
            extends LinkFunction<T, Flight, Pilot, FlightPilotType> {
    }
}
