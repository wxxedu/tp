package wingman.logic.pilot.linkflight;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import wingman.logic.core.exceptions.CommandException;
import wingman.model.Model;
import wingman.model.flight.Flight;
import wingman.model.link.exceptions.LinkException;
import wingman.model.pilot.FlightPilotType;
import wingman.model.pilot.Pilot;

@ExtendWith(MockitoExtension.class)
public class LinkPilotToFlightCommandTest {
    @Mock
    Flight flight;

    @Mock
    Pilot pilot1;

    @Mock
    Pilot pilot2;

    @Mock
    Model model;

    @Test
    void execute_validState_doesNotThrow() throws LinkException {
        LinkPilotToFlightCommand command = new LinkPilotToFlightCommand(
                flight,
                Map.of(FlightPilotType.PILOT_FLYING, pilot1)
        );
        assertDoesNotThrow(() -> command.execute(model));
        Mockito.verify(flight, times(1)).setPilot(
                eq(FlightPilotType.PILOT_FLYING),
                eq(pilot1)
        );
    }

    @Test
    void execute_twoTypes_calledWithEach() throws LinkException {
        LinkPilotToFlightCommand command = new LinkPilotToFlightCommand(
                flight,
                Map.of(
                        FlightPilotType.PILOT_FLYING, pilot1,
                        FlightPilotType.PILOT_MONITORING, pilot2
                )
        );
        assertDoesNotThrow(() -> command.execute(model));
        Mockito.verify(flight, times(1)).setPilot(
                eq(FlightPilotType.PILOT_FLYING),
                eq(pilot1)
        );
        Mockito.verify(flight, times(1)).setPilot(
                eq(FlightPilotType.PILOT_MONITORING),
                eq(pilot2)
        );
    }

    @Test
    void execute_failedToSetPilot_throwsLinkException() throws LinkException {
        LinkPilotToFlightCommand command = new LinkPilotToFlightCommand(
                flight,
                Map.of(
                        FlightPilotType.PILOT_FLYING, pilot1,
                        FlightPilotType.PILOT_MONITORING, pilot2
                )
        );
        Mockito.doThrow(new LinkException("")).when(flight).setPilot(
                any(),
                any()
        );
        assertThrows(CommandException.class, () -> command.execute(model));
    }
}