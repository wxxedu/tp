package seedu.address.model.flight;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import seedu.address.commons.util.GetUtil;
import seedu.address.commons.util.LinkUtils;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyItemManager;
import seedu.address.model.crew.Crew;
import seedu.address.model.crew.FlightCrewType;
import seedu.address.model.item.Item;
import seedu.address.model.link.Link;
import seedu.address.model.link.SingleLink;
import seedu.address.model.link.exceptions.LinkException;
import seedu.address.model.location.FlightLocationType;
import seedu.address.model.location.Location;
import seedu.address.model.pilot.FlightPilotType;
import seedu.address.model.pilot.Pilot;
import seedu.address.model.plane.Plane;

/**
 * Represents a flight object in wingman
 */
public class Flight implements Item {
    public final Link<FlightPilotType, Pilot, ReadOnlyItemManager<Pilot>> pilotLink;
    public final Link<FlightCrewType, Crew, ReadOnlyItemManager<Crew>> crewLink;
    public final Link<FlightLocationType, Location, ReadOnlyItemManager<Location>> locationLink;
    private final String code;
    private final String id;
    private Optional<SingleLink<Plane>> planeLinkOptional;

    //TODO: Add exceptions to ensure departure and arrival locations are distinct

    /**
     * Creates a new flight
     *
     * @param id           the id of the  flight
     * @param code         the code
     * @param pilotLink    the link to the pilot
     * @param crewLink     the link to the crew
     * @param locationLink the link to the location
     */
    public Flight(
            String id,
            String code,
            Optional<SingleLink<Plane>> planeLink,
            Link<FlightPilotType, Pilot, ReadOnlyItemManager<Pilot>> pilotLink,
            Link<FlightCrewType, Crew, ReadOnlyItemManager<Crew>> crewLink,
            Link<FlightLocationType, Location, ReadOnlyItemManager<Location>> locationLink
    ) {
        this.id = id;
        this.code = code;
        this.planeLinkOptional = planeLink;
        this.pilotLink = pilotLink;
        this.crewLink = crewLink;
        this.locationLink = locationLink;
    }

    /**
     * Creates a flight with a random UUID as its id
     *
     * @param code the code of the flight
     */
    public Flight(String code) {
        this(UUID.randomUUID().toString(), code,
                Optional.empty(),
                new Link<>(
                        Pilot.SHAPE,
                        GetUtil.getLazy(Model.class).map(Model::getPilotManager)
                ), new Link<>(
                        Crew.SHAPE,
                        GetUtil.getLazy(Model.class).map(Model::getCrewManager)
                ), new Link<>(
                        Location.SHAPE,
                        GetUtil
                                .getLazy(Model.class)
                                .map(Model::getLocationManager)
                )
        );
    }

    public String getCode() {
        return this.code;
    }

    /**
     * Returns the pilot link of the flight.
     *
     * @return the link to pilots
     */
    public Link<FlightPilotType, Pilot, ReadOnlyItemManager<Pilot>> getPilotLink() {
        return pilotLink;
    }

    /**
     * Returns the crew link of the flight.
     *
     * @return the link to crews
     */
    public Link<FlightCrewType, Crew, ReadOnlyItemManager<Crew>> getCrewLink() {
        return crewLink;
    }

    /**
     * Returns the location link of the flight.
     *
     * @return the link to locations
     */
    public Link<FlightLocationType, Location, ReadOnlyItemManager<Location>> getLocationLink() {
        return locationLink;
    }

    public Optional<Plane> getPlane(
            Optional<Model> modelOptional,
            boolean recursive
    ) {
        if (planeLinkOptional.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(planeLinkOptional.get().get());
        } catch (LinkException e) {
            this.planeLinkOptional = Optional.empty();
            return Optional.empty();
        }
    }

    public void setPlane(Model model, Plane plane, boolean recursive) {
        this.planeLinkOptional = Optional.of(LinkUtils.planeLink(
                model,
                plane.getId()
        ));
        if (recursive) {
            plane.addFlight(model, this, false);
        }
    }

    @Override
    public List<String> getDisplayList() {
        return List.of(
                String.format("%s", code),
                String.format("%s: %s", "Pilots", pilotLink.toString()),
                String.format("%s: %s", "Crews", crewLink.toString()),
                String.format("%s: %s", "Plane", planeLinkOptional.toString()),
                String.format("%s: %s", "Locations", locationLink.toString())
        );
    }

    @Override
    public String getId() {
        return this.id;
    }
}
