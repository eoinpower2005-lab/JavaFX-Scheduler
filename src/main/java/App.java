import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.Event;

import java.awt.event.ActionListener;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;

import static javafx.application.Platform.exit;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Server server = new Server();

        ComboBox<String> box1 = new ComboBox(FXCollections.observableArrayList("ADD", "REMOVE", "DISPLAY"));
        box1.setPromptText("Choose an Option");
        box1.setMaxWidth(140);

        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setMaxWidth(140);

        ComboBox<String> box2 = new ComboBox(FXCollections.observableArrayList("09:00-10:00", "10:00-11:00", "11:00-12:00", "12:00-13:00", "13:00-14:00", "14:00-15:00", "15:00-16:00", "16:00-17:00", "17:00-18:00"));
        box2.setPromptText("Choose a Timeslot");
        box2.setMaxWidth(140);

        TextField roomID = new TextField();
        roomID.setPromptText("Enter Room ID");
        roomID.setMaxWidth(140);

        ComboBox<String> box3 = new ComboBox(FXCollections.observableArrayList("CS4076", "CS4006", "CS4115", "MA4413", "CS4815"));
        box3.setPromptText("Choose a Module");
        box3.setMaxWidth(140);

        Button b1 = new Button("Send Request");
        b1.setMaxWidth(140);

        Button b2 = new Button("STOP");
        b2.setMaxWidth(140);

        Button b3 = new Button("Clear");
        b3.setMaxWidth(140);

        Label statusLabel = new Label("Status: Connection Opened");

        TextArea ta = new TextArea();
        ta.setEditable(false);
        ta.setWrapText(true);

        TableView<TimetableSlot> tableView = new TableView();

        TableColumn<TimetableSlot, String> col1 = new TableColumn<>("Date");
        col1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));

        TableColumn<TimetableSlot, String> col2 = new TableColumn<>("Time");
        col2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTime().toString()));

        TableColumn<TimetableSlot, String> col3 = new TableColumn<>("Room");
        col3.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoom().toString()));

        TableColumn<TimetableSlot, String> col4 = new TableColumn<>("Module");
        col4.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getModule().toString()));

        tableView.getColumns().addAll(col1, col2, col3, col4);
        tableView.setEditable(false);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setItems(server.getTimetableSlots());

        EventHandler<ActionEvent> button1Event = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    String option = box1.getValue();
                    String room = roomID.getText();
                    String date = String.valueOf(datePicker.getValue());
                    String time = box2.getValue();
                    String module = box3.getValue();
                    if (option == null) {
                        throw new InvalidInputException("You must select an option!");
                    }

                    String request = " ";
                    if (option.equals("ADD") || option.equals("REMOVE")) {
                        request = option + "|" + date + "|" + time + "|" + room + "|" + module;
                    } else if (option.equals("DISPLAY")) {
                        request = option + "||||";
                    }

                    ta.appendText("CLIENT: " + request + "\n");
                    String response = server.clientRequest(request);
                    ta.appendText("SERVER: " + response + "\n");

                } catch (InvalidInputException e) {
                    displayError(e.getMessage());
                }
            }

            ;
        };

        EventHandler<ActionEvent> button2Event = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                b1.setDisable(true);
                ta.appendText("Status: Connection Closed. \n");
                statusLabel.setText("Status: Connection Closed");
                b2.setDisable(true);
            }
        };

        EventHandler<ActionEvent> button3Event = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                server.getTimetableSlots().clear();
                ta.appendText("Timetable Slots Cleared. \n");
            }
        };


        b1.setOnAction(button1Event);
        b2.setOnAction(button2Event);
        b3.setOnAction(button3Event);

        BorderPane pane = new BorderPane();
        VBox vbox = new VBox(box1, datePicker, box2, roomID, box3, b1, b2, b3, statusLabel);
        pane.setLeft(vbox);
        pane.setCenter(tableView);
        pane.setBottom(ta);
        vbox.setSpacing(3);


        ta.appendText("Status: Connection Opened. \n");
        Scene scene = new Scene(pane, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Lecture Scheduler");
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    private void displayError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

