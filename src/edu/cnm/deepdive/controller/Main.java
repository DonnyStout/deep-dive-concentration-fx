package edu.cnm.deepdive.controller;

import edu.cnm.deepdive.util.Resources;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;

/**
 * This is the controller class for the view embodied by the
 * <code>main.fxml</code> layout file. To complete this class, the
 * implementations of the {@link #handle(ActionEvent)} and {@link
 * #newGame(ActionEvent)} methods must be completed. These methods (along with
 * any additional helper methods needed) will embody both the model of the
 * Concentration game, and the controller logic that connects the model to the
 * view.
 * <p>
 * A method must also be written to handle the Exit menu command.
 */
public class Main implements EventHandler<ActionEvent> {

  /**
   * Location of FXML layout file supported by this controller class.
   */
  public static final String LAYOUT_PATH = Resources.LAYOUT_DIRECTORY + "main.fxml";

  @FXML
  private FlowPane tileTable;
  @FXML
  private Text totalStatus;
  @FXML
  private Text currentStatus;


  private List<ToggleButton> tiles;
  private String totalStatusFormat;
  private String currentStatusFormat;
  private ToggleButton button1;
  private ToggleButton button2;
  private String urlValue1;
  private String urlValue2;
  private int count = 1;
  private int match = 0;
  private int guess = 0;
  private int gameSet = 0;
  private int allGuess = 0;
  private MediaPlayer win;
  private MediaPlayer hello;
  private MediaPlayer party;
  private MediaPlayer incorrect;
  private MediaPlayer goodPeople;
  private boolean twoPlayer = false;
  private int count2 = 0;


  /**
   * Initialize the controller for the main view (layout). This includes loading the set of tiles,
   * each of which is a {@link ToggleButton} instance. However, these tiles are not shuffled at this
   * point; that should be done at the start of every game.
   *
   * @throws URISyntaxException if the path to the tile images is invalid.
   * @throws IOException if a tile image file can't be read.
   */
  public void initialize() throws URISyntaxException, IOException {
    tiles = Tile.getTiles();
    for (ToggleButton tile : tiles) {
      tile.setOnAction(this);
    }
    currentStatusFormat = currentStatus.getText();
    totalStatusFormat = totalStatus.getText();
    newGame(null);
  }

  /**
   * State machine to handle the logic of the game. Starts the count with 1 to recognize the first
   * click using <code>event.getSource</code>. The first button pressed is disabled and when the
   * second button is pressed the button is casted to <code>ImageView</code> to check the url to
   * match the buttons to eachother to see if it is a match. If it is a match both buttons are
   * disabled and returned to the face down position. If they don't match then the buttons are
   * returned to the unchecked position, the first one is re-enabled and then the count for guess
   * and for match increase. Once all 15 matches have been found, the user will be asked if they
   * would like to play again. There are 3 sounds included, the first is if the game is won. the
   * other two are for if guesses reach 100 and a match is not found on click 100 and the second one
   * is if guesses reach 200 and match is still at 0.
   *
   * @param event When the togglebutton is pressed.
   */
  public void handle(ActionEvent event) {
    if (twoPlayer) {
      ToggleButton dis = ((ToggleButton) event.getSource());
      if (count == 1) {
        dis.setDisable(true);
        urlValue1 = ((ImageView) dis.getGraphic()).getImage().getUrl();
        button1 = dis;
        count = 2;
      }
      if (count2 == 1) {
        dis.setDisable(true);
        urlValue1 = ((ImageView) dis.getGraphic()).getImage().getUrl();
        button1 = dis;
        count = 2;
      }
      if (count == 2) {
        urlValue2 = ((ImageView)dis.getGraphic()).getImage().getUrl();
        if (urlValue1.equals(urlValue2)){
          dis.setDisable(true);
        }
        if (count2 == 2) {
          urlValue2 = ((ImageView)dis.getGraphic()).getImage().getUrl();
          if (urlValue1.equals(urlValue2)){
            dis.setDisable(true);
          } if (count == 2 && !urlValue1.equals(urlValue2)) {
          button1.setDisable(false);
        }
        dis.setSelected(false);
        button1.setSelected(false);
        count = 0;
        count2 = 1;
      }
        else {
          button1.setDisable(false);
        }
        dis.setSelected(false);
        button1.setSelected(false);
        count = 1;
        count2 = 0;
      }
    } else {
        ToggleButton dis = ((ToggleButton) event.getSource());
        if (count == 1) {
          if (button1 != null) {
            button1.setSelected(!button1.isSelected());
            button1 = null;
          }
          if (button2 != null) {
            button2.setSelected(!button2.isSelected());
            button2 = null;
          }
          dis.setDisable(true);
          urlValue1 = ((ImageView) dis.getGraphic()).getImage().getUrl();
          button1 = dis;
          count = 2;
        } else {
          urlValue2 = ((ImageView) dis.getGraphic()).getImage().getUrl();
          button2 = dis;
          guess++;
          updateDisplay();
          if (urlValue1.equals(urlValue2)) {
            button2.setDisable(true);
            match++;
            if (match >= tiles.size() / 2) {
              winSound();
              gameSet++;
              allGuess += guess;
              reset();
            }
            updateDisplay();
          } else {
            button1.setDisable(false);
            if (guess == 10) {
              goodPeopleSound();
            }
            if (guess == 20 && match == 0) {
              partySound();
            }
            if (!urlValue1.equals(urlValue2) && guess != 10 && guess != 20) {
              incorrectSound();
            }
          }
          count = 1;
        }
        updateDisplay();
      }
  }

    @FXML
    private void newGame (ActionEvent event){
      guess = 0;
      match = 0;
      count = 1;
      button1 = null;
      button2 = null;
      tileTable.getChildren().clear();
     // Collections.shuffle(tiles);
      two();
      helloSound();
      for (ToggleButton tile : tiles) {
        tile.setSelected(false);
        tile.setDisable(false);
        tileTable.getChildren().add(tile);
        updateDisplay();
      }
    }

    private void updateDisplay () {
      double avgGuess = (gameSet > 0) ? (double) allGuess / gameSet : 0;
      currentStatus.setText(String.format(currentStatusFormat, guess, match));
      totalStatus.setText(String.format(totalStatusFormat, gameSet, avgGuess));
    }

    public void quit () {
      Platform.exit();
      System.exit(0);
    }

    /**
     * An <code>Alert</code> box that pops up and asks the user if they would like to play a new game with
     * yes, no, and cancel as options. Uses if statements to check if yes <code>newGame</code> is invoked,
     * if no <code>quit</code> is invoked, and cancel closes the window.
     */
    public void reset () {
      Alert alert = new Alert(AlertType.CONFIRMATION);
      alert.setResizable(false);
      alert.setHeaderText("Would you like to play a new game?");
      alert.setContentText("Select Yes to play again or No to quit.");
      alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
      Optional<ButtonType> choice = alert.showAndWait();
      if (choice.get() == ButtonType.YES) {
        newGame(null);
      } else if (choice.get() == ButtonType.NO) {
        quit();
      } else {
        alert.close();
      }
    }

    /**
     * Plays a sound from the resources sound folder for when the player completes a game.
     */
    public void winSound () {
      // Open an audio input stream.
      File soundFile = new File("src/resources/sounds/well_done.mp3");
      // Get a sound clip resource.
      win = new MediaPlayer(new Media(soundFile.toURI().toString()));
      // Open audio clip and load samples from the audio input stream.
      win.play();
    }

    /**
     * Plays a sound from the resources sound folder for when the user starts a game.
     */
    public void helloSound () {
      // Open an audio input stream.
      File soundFile = new File("src/resources/sounds/hello.mp3");
      // Get a sound clip resource.
      hello = new MediaPlayer(new Media(soundFile.toURI().toString()));
      // Open audio clip and load samples from the audio input stream.
      hello.play();
    }

  public void incorrectSound() {
    // Open an audio input stream.
    File soundFile = new File("src/resources/sounds/how_does_that_feel.mp3");
    // Get a sound clip resource.
    incorrect = new MediaPlayer(new Media(soundFile.toURI().toString()));
    // Open audio clip and load samples from the audio input stream.
    incorrect.play();
  }

    /**
     * Plays a sound from the resources sound folder for when the player has 100 guesses in a game.
     */
    public void goodPeopleSound () {
      // Open an audio input stream.
      File soundFile = new File("src/resources/sounds/good_people.mp3");
      // Get a sound clip resource.
      goodPeople = new MediaPlayer(new Media(soundFile.toURI().toString()));
      // Open audio clip and load samples from the audio input stream.
      goodPeople.play();
    }

    /**
     * Plays a sound from the resources sound folder for when the player has 200 guesses in a game
     * and no matches.
     */
    public void partySound () {
      // Open an audio input stream.
      File soundFile = new File("src/resources/sounds/big_party.mp3");
      // Get a sound clip resource.
      party = new MediaPlayer(new Media(soundFile.toURI().toString()));
      // Open audio clip and load samples from the audio input stream.
      party.play();
    }

    public void two () {
      Alert alert = new Alert(AlertType.CONFIRMATION);
      alert.setResizable(false);
      alert.setHeaderText("1 or 2 player game??");
      alert.setContentText("Would you like a two player game??");
      alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
      Optional<ButtonType> choice = alert.showAndWait();
      if (choice.get() == ButtonType.YES) {
        twoPlayer = true;
      } else if (choice.get() == ButtonType.NO) {
        twoPlayer = false;
      }
    }
  }
