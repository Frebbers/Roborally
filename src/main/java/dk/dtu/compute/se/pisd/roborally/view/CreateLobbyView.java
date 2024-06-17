package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.config.AppConfig;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
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
    private String lobbyName;
    private Integer numberOfPlayers = 1;
    private Integer boardId = 1;

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(1, 2, 3, 4, 5, 6);

    public CreateLobbyView(AppController appController){
        super(appController);
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

        // for setting lobby name
        Text lobbyNameText = new Text("Lobby Name: ");
        TextField lobbyNameField = new TextField();
        lobbyNameField.setPromptText("Enter lobby name");
        lobbyNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            lobbyName = newValue;
        });
        lobbyNameField.setText(AppConfig.getProperty("local.player.name") + "'s lobby");

        //for selecting number of players
        Text numberOfPlayersText = new Text("Players: ");
        ComboBox<String> comboBox = new ComboBox<>();
        ObservableList<String> options = FXCollections.observableArrayList(
                PLAYER_NUMBER_OPTIONS.stream().map(Object::toString).collect(Collectors.toList())
        );
        comboBox.setItems(options);
        comboBox.getSelectionModel().select(0);
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                numberOfPlayers = Integer.valueOf(newValue);
            }
        });

        // Load the pictures of the boards
        Text boardText = new Text("Select a Board: ");
        loadBoardPictures();

        Button startButton = new Button("Create");
        startButton.setOnAction(event -> {
            appController.createLobby(lobbyName, boardId, numberOfPlayers);
        });

        // Add a button to go back
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> appController.getRoboRally().createStartView(appController));

        // Spacer to push buttons to each side
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Add the buttons and spacer to a container
        HBox buttonContainer = new HBox(backButton, spacer, startButton);
        buttonContainer.setAlignment(Pos.CENTER);

        VBox mainLayout = new VBox(20, title, lobbyNameText, lobbyNameField, numberOfPlayersText, comboBox, boardText, boardSelection, buttonContainer);
        getChildren().addAll(mainLayout);

        // Highlight the first selected board
        highlightSelected(boardId);
    }


    private void loadBoardPictures() {
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
                    highlightSelected(imageId);
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

    private void highlightSelected(int id) {
        for (Node child : boardSelection.getChildren()) {
            if (child instanceof Button) {
                Button button = (Button) child;
                int buttonId = boardSelection.getRowIndex(button) * 3 + boardSelection.getColumnIndex(button) + 1;
                if (buttonId == id) {
                    button.setStyle("-fx-border-color: blue; -fx-border-width: 2; -fx-border-style: solid;");
                } else {
                    button.setStyle(""); // Reset style for other buttons
                }
            }
        }
    }
}
