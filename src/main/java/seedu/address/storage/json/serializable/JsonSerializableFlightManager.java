package seedu.address.storage.json.serializable;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.model.ReadOnlyIdentifiableManager;
import seedu.address.model.flight.Flight;
import seedu.address.storage.json.JsonIdentifiableManager;
import seedu.address.storage.json.adapted.JsonAdaptedFlight;



/**
 * Represents a serializable manager of flights.
 */
@JsonRootName(value = "flightmanager")
public class JsonSerializableFlightManager extends JsonIdentifiableManager<Flight, JsonAdaptedFlight> {

    @JsonCreator
    public JsonSerializableFlightManager(
            @JsonProperty("items") List<JsonAdaptedFlight> flights) {
        this.items.addAll(flights);
    }

    /**
     * Creates a new JsonSerializableFlightManager from the given manager.
     *
     * @param manager the manager to create the JsonSerializableFlightManager
     *                from, it should be a ReadOnlyIdentifiableManager
     *                &lt;Flight&gt;
     * @return a new JsonSerializableFlightManager
     */
    public static JsonSerializableFlightManager from(
            ReadOnlyIdentifiableManager<Flight> manager) {
        final JsonSerializableFlightManager res =
                new JsonSerializableFlightManager(new ArrayList<>());
        res.readFromManager(manager);
        return res;
    }

    @Override
    protected JsonAdaptedFlight getJsonAdaptedModel(Flight item) {
        return new JsonAdaptedFlight(item);
    }
}
