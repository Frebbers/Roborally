package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

import static java.awt.SystemColor.window;

public abstract class BaseView extends VBox  {

    // Constructor for BaseView
    public BaseView(AppController appController) {
        super();
        setPadding(new Insets(10));
        setSpacing(10);
        appController.getRoboRally().getStage().setOnCloseRequest(e -> {
            e.consume();
            appController.exit();
        });
    }
/**
 * Method to show an alert when the server is not connected
 * call this from any active view.
 * @author s224804
 */
    public void showNotConnectedAlert() {
        new Alert(Alert.AlertType.WARNING,
                "Error connecting to the server. Check your connection to the server.", ButtonType.OK).showAndWait();
    }
    /**
     * Method to show a confirmation alert when the user tries to exit the application
     * call this from any active view.
     * @return true if the user confirmed the exit
     * @author s224804
     */
    public boolean showExitConfirmationAlert() {
        // return true if the user presses OK, false otherwise
        return showAlert(Alert.AlertType.CONFIRMATION,
                "Exit RoboRally?", "Are you sure you want to exit RoboRally?");
    }

    /**
     * Method to show an alert with a given alert type, title and message
     * @param alertType The type of alert to show. Enums are AlertType.INFORMATION, AlertType.WARNING, AlertType.ERROR, AlertType.CONFIRMATION
     * @param title The title of the window
     * @param message The message to display to the user
     * @return Whether the user pressed OK or not
     */
    public boolean showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public abstract void initialize();
}
