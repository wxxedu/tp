package wingman.model.location;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import wingman.model.item.Item;

/**
 * Location is a unit place that the flight can travel to or
 * arrive at.
 */
public class Location implements Item {

    /**
     * The shape of the link between location and flight
     */
    public static final Map<FlightLocationType, Integer> SHAPE =
            Map.of(FlightLocationType.LOCATION_DEPARTURE, 1,
                    FlightLocationType.LOCATION_ARRIVAL, 1
            );

    private final String name;
    private final String id;

    /**
     * Creates a Location object with the given name.
     *
     * @param name name of the location
     */
    public Location(
            String name
    ) {
        this(UUID.randomUUID().toString(), name);
    }

    /**
     * Creates a Location object from the given id and name
     *
     * @param id   a unique id assigned to the location
     * @param name the name of the location
     */
    public Location(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Returns the name of the location in string.
     *
     * @return the name of the location
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<String> getDisplayList() {
        return List.of(String.format("%s", name));
    }

    /**
     * Returns true if both locations have the same name.
     * This defines a weaker notion of equality between two locations.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Location)) {
            return false;
        }

        Location other = (Location) obj;

        return other.getName().equals(getName());
    }
}
