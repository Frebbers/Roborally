package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.config.AppConfig;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.model.ApiType;
import dk.dtu.compute.se.pisd.roborally.service.ApiServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private ApiServices apiServices;
    private GridPane boardSelection;
    private Text connectToServerFeedback;
    private Button startButton;
    private String lobbyName;
    private Integer numberOfPlayers;
    private Integer boardId = 1;

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(1, 2, 3, 4, 5, 6);

    public CreateLobbyView(AppController appController){
        super(appController);
        this.appController = appController;
        this.apiServices = appController.getApiServices();
    }

    /**
     * Initializes the CreateLobbyView by setting up the UI components and their event handlers.
     */
    @Override
    public void initialize() {
        Text title = new Text("Create a new lobby");
        title.setId("titleText");
        connectToServerFeedback = new Text();

        boardSelection = new GridPane();
        boardSelection.setHgap(10);
        boardSelection.setVgap(10);

        // Setup for lobby type selection
        Text lobbyTypeText = new Text("Lobby Type: ");
        lobbyTypeText.setId("lobbyTypeText");
        ComboBox<String> connectionTypeDropdown = new ComboBox<>();
        connectionTypeDropdown.setId("connectionTypeDropdown");
        connectionTypeDropdown.getItems().addAll("Local", "Server");
        connectionTypeDropdown.setValue("Server");

        // Adding listener to connectionTypeDropdown
        connectionTypeDropdown.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            initializeConnection(newValue);
        });

        HBox connectToServerBox = new HBox(10, connectionTypeDropdown, connectToServerFeedback);
        connectToServerBox.setAlignment(Pos.CENTER_LEFT);

        // for setting lobby name
        Text lobbyNameText = new Text("Lobby Name: ");
        lobbyNameText.setId("lobbyNameText");
        TextField lobbyNameField = new TextField();
        lobbyNameField.setPromptText("Enter lobby name");
        lobbyNameField.setId("lobbyNameField");
        lobbyNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            lobbyName = newValue;
        });
        lobbyNameField.setText(AppConfig.getProperty("local.player.name") + "'s lobby");

        //for selecting number of players
        Text numberOfPlayersText = new Text("Players: ");
        numberOfPlayersText.setId("numberOfPlayersText");
        ComboBox<String> comboBox = new ComboBox<>();
        ObservableList<String> options = FXCollections.observableArrayList(
                PLAYER_NUMBER_OPTIONS.stream().map(Object::toString).collect(Collectors.toList())
        );
        comboBox.setItems(options);
        comboBox.getSelectionModel().select("2");
        numberOfPlayers = Integer.valueOf(comboBox.getValue());
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                numberOfPlayers = Integer.valueOf(newValue);
            }
        });

        // Load the pictures of the boards
        Text boardText = new Text("Select a Board: ");
        boardText.setId("boardText");
        loadBoardPictures();

        startButton = new Button("Create");
        startButton.setId("startButton");
        startButton.setOnAction(event -> {
            appController.createLobby(lobbyName, boardId, numberOfPlayers);
        });

        // Add a button to go back
        Button backButton = new Button("Back");
        backButton.setId("backButton");
        backButton.setOnAction(event -> appController.getRoboRally().createStartView(appController));

        // Spacer to push buttons to each side
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Add the buttons and spacer to a container
        HBox buttonContainer = new HBox(backButton, spacer, startButton);
        buttonContainer.setAlignment(Pos.CENTER);

        // Highlight the first selected board
        highlightSelected(boardId);

        // Set the initial connection type on startup
        initializeConnection(connectionTypeDropdown.getValue());

        VBox mainLayout = new VBox(20, title, lobbyTypeText, connectToServerBox, lobbyNameText, lobbyNameField, numberOfPlayersText, comboBox, boardText, boardSelection, buttonContainer);
        getChildren().addAll(mainLayout);

    }

    /**
     * Loads the pictures of the available game boards and sets up the selection buttons.
     */
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

    /**
     * Initializes the connection based on the selected connection type.
     *
     * @param connectionType the type of connection ("Local" or "Server").
     */
    private void initializeConnection(String connectionType) {
        boolean isConnected;
        if ("Server".equals(connectionType)) {
            apiServices.setApiType(ApiType.SERVER);
            isConnected = apiServices.testConnection(apiServices.getServerIP());
        } else {
            apiServices.setApiType(ApiType.LOCAL);
            isConnected = apiServices.testConnection("localhost");
        }

        updateConnectionFeedback(isConnected, connectionType);
    }

    /**
     * Updates the feedback text for the connection status.
     *
     * @param isConnected    whether the connection was successful.
     * @param connectionType the type of connection ("Local" or "Server").
     */
    private void updateConnectionFeedback(boolean isConnected, String connectionType) {
        if (isConnected) {
            connectToServerFeedback.setFill(Color.GREEN);
            connectToServerFeedback.setText("Connected successfully to " + connectionType);
            startButton.setDisable(false);
        } else {
            connectToServerFeedback.setFill(Color.RED);
            connectToServerFeedback.setText("Failed to connect to " + connectionType);
            startButton.setDisable(true);
        }
    }

    /**
     * Sets the selected board ID.
     *
     * @param id the ID of the selected board.
     */
    private void selectBoard(int id) {
        boardId = id;
    }

    /**
     * Highlights the selected board button.
     *
     * @param id the ID of the selected board.
     */
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
