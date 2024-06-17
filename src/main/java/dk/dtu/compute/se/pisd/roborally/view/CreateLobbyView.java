package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.config.AppConfig;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CreateLobbyView extends BaseView {
    private AppController appController;
    private GridPane boardSelection;
    private Integer numberOfPlayers = 1;
    private Integer boardId = 0;

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(1, 2, 3, 4, 5, 6);

    public CreateLobbyView(AppController appController){
        this.appController = appController;
    }

    @Override
    public void initialize() {
        Text title = new Text("Create a new lobby");
        Text errorMessage = new Text("You need to choose a board and number of players");
        //errorMessage.setStyle( Color.rgb());

        boardSelection = new GridPane();
        boardSelection.setHgap(10);
        boardSelection.setVgap(10);
        loadGamePictures();

        //for selecting number of players
        Text numberOfPlayersInfoText = new Text("Select the size of the lobby");
        Text numberOfPlayersText = new Text("Players: ");
        ComboBox<String> comboBox = new ComboBox<>();
        ObservableList<String> options = FXCollections.observableArrayList(
                PLAYER_NUMBER_OPTIONS.stream().map(Object::toString).collect(Collectors.toList())
        );
        comboBox.setItems(options);
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                numberOfPlayers = Integer.valueOf(newValue);
            }
        });

        Button startButton = new Button("Create");
        startButton.setOnAction(event -> {
            appController.createLobby(boardId, numberOfPlayers);
        });

        VBox mainLayout = new VBox(20, title, boardSelection, numberOfPlayersInfoText, numberOfPlayersText, comboBox, startButton);
        getChildren().addAll(mainLayout);
    }


    private void loadGamePictures() {
        try {
            URL resourceUrl = getClass().getClassLoader().getResource("images/boards/");
            File dir = new File(resourceUrl.toURI());
            File[] files = dir.listFiles((d, name) -> name.endsWith(".png"));
            for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
                Image boardImage = new Image(files[i].toURI().toString());
                ImageView boardView = new ImageView(boardImage);
                boardView.setFitHeight(150);
                boardView.setFitWidth(150);
                Button boardButton = new Button();
                boardButton.setGraphic(boardView);
                int imageId = i + 1;
                boardButton.setOnAction(event -> {
                    selectBoard(imageId);
                    highlightSelected(boardButton);
                });
                boardSelection.add(boardButton, i % 3, i / 3);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle errors (e.g., directory not found, no permission, etc.)
        }
    }

    private void selectBoard(int id) {
        boardId = id;
    }

    private void highlightSelected(Button selectedButton) {
        // Iterating through all children of the robotsSelection GridPane
        for (Node child : boardSelection.getChildren()) {
            if (child instanceof Button) {
                child.setStyle(""); // Reset style for all buttons
            }
        }
        // Apply style to highlight the selected button
        selectedButton.setStyle("-fx-border-color: blue; -fx-border-width: 2; -fx-border-style: solid;");
    }
}
