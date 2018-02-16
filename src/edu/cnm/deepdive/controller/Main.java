package edu.cnm.deepdive.controller;

import edu.cnm.deepdive.util.Resources;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
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

  /** Location of FXML layout file supported by this controller class. */
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


  /**
   * Initialize the controller for the main view (layout). This includes loading
   * the set of tiles, each of which is a {@link ToggleButton} instance.
   * However, these tiles are not shuffled at this point; that should be done at
   * the start of every game.
   *
   * @throws URISyntaxException if the path to the tile images is invalid.
   * @throws IOException        if a tile image file can't be read.
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


  public void handle(ActionEvent event) {
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
          gameSet++;
          allGuess += guess;
          //  winSound();
        }
        updateDisplay();
      } else {
        button1.setDisable(false);
      }
//      two.setSelected(false);
//      one.setSelected(false);
      count = 1;
    }
  }

  @FXML
  private void newGame(ActionEvent event) {
    guess = 0;
    match = 0;
    count = 1;
    button1 = null;
    button2 = null;
    tileTable.getChildren().clear();
    Collections.shuffle(tiles);
    for (ToggleButton tile : tiles) {
      tile.setSelected(false);
      tile.setDisable(false);
      tileTable.getChildren().add(tile);
      updateDisplay();
    }
  }


  private void updateDisplay() {
    double avgGuess = (gameSet > 0) ? (double) allGuess / gameSet : 0;
    currentStatus.setText(String.format(currentStatusFormat, guess, match));
    totalStatus.setText(String.format(totalStatusFormat, gameSet, avgGuess));
  }

    public void quit() {
      Platform.exit();
      System.exit(0);
    }

  //  public void winSound() {
//      // Open an audio input stream.
//      File soundFile = new File("src/resources/win-effect.mp3");
//      // Get a sound clip resource.
//      win = new MediaPlayer(new Media(soundFile.toURI().toString()));
//      // Open audio clip and load samples from the audio input stream.
//      win.play();
//  }

}
