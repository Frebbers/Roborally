package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

public abstract class BaseView extends VBox {

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

    public abstract void initialize();
}
