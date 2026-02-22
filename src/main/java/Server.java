import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class Server {
    private final ObservableList<TimetableSlot> timetableSlots = FXCollections.observableArrayList();

    public String clientRequest(String request) throws InvalidInputException {
        String[] fields = request.split("\\|", -1);
        String option = fields[0];
        String room = fields[1];
        String date = fields[2];
        String time = fields[3];
        String module = fields[4];

        String response = " ";

        if (option.equals("ADD")) {
            if (date == null || date.isEmpty() || time == null || time.isEmpty() || module == null || module.isEmpty() || room == null || room.isEmpty()) {
                throw new InvalidInputException("Data fields cannot be empty!");
            }

            boolean exists = false;
            for (TimetableSlot slot : timetableSlots) {
                if (slot.getDate().equals(date) && slot.getTime().equals(time) && slot.getRoom().equals(room)) {
                    throw new InvalidInputException("Clash: Timetable Slot already exists for that room, date, and time!");
                }
            }

            timetableSlots.add(new TimetableSlot(date, time, room, module));
            response = "Timetable Slot Added";
        } else if (option.equals("REMOVE")) {
            TimetableSlot match = null;
            if (timetableSlots.isEmpty()) {
                throw new InvalidInputException("Cannot remove timetable slot! No slots exist.");
            }
            for (TimetableSlot slot : timetableSlots) {
                if (slot.getDate().equals(date) && slot.getTime().equals(time) && slot.getRoom().equals(room) && slot.getModule().equals(module)) {
                    match = slot;
                    break;
                }
            }

            if (match == null) {
                throw new InvalidInputException("Cannot remove timetable slot! Matching slot not found.");
            } else {
                timetableSlots.remove(match);
                response = "Timetable Slot Removed";
            }
        } else if (option.equals("DISPLAY")) {
            if (timetableSlots == null || timetableSlots.isEmpty()) {
                throw new InvalidInputException("No timetable slots found!");
            } else {
                response = "Timetable Slot's Displayed";
            }
        }
        return response;
    }

    public ObservableList<TimetableSlot> getTimetableSlots() {
        return timetableSlots;
    }
}

