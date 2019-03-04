/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pongfx;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 *
 * @author blj0011
 */
public class FXMLDocumentController implements Initializable
{

    @FXML
    private Button btnNewGame;

    @FXML
    private Pane paneGameBoard;

    Timeline gameLoop;
    Rectangle paddle1, paddle2;
    Circle ball;
    Set<KeyCode> input = new HashSet();
    double gameBoardHeight = 500;
    double gameBoardWidth = 600;
    double paddleWidth = 15;
    double paddleHeight = 100;

    @FXML
    private void handleBtnNewGame(ActionEvent event)
    {
        Platform.runLater(() -> paneGameBoard.requestFocus());
        gameLoop.play();
    }

    @FXML
    private void handleSceneKeyPress(KeyEvent event)
    {

    }

    @FXML
    private void handleSceneKeyRelease(KeyEvent event)
    {

    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        ball = new Circle(gameBoardWidth / 2.0, gameBoardHeight / 2.0, 15, Color.WHITE);

        paddle1 = new Rectangle(12, 100, Color.WHITE);

        paddle1.setX(0);
        paddle1.setY((gameBoardHeight - paddleHeight) / 2);

        paddle2 = new Rectangle(15, 100, Color.WHITE);
        paddle2.setX(gameBoardWidth - paddleWidth);
        paddle2.setY((gameBoardHeight - paddleHeight) / 2);

        paneGameBoard.getChildren().addAll(ball, paddle1, paddle2);

        paneGameBoard.setOnKeyPressed((event) -> {
            KeyCode code = event.getCode();
            System.out.println("press: " + input.toString());
            input.add(code);
        });

        paneGameBoard.setOnKeyReleased((event) -> {
            KeyCode code = event.getCode();
            System.out.println("release: " + input.toString());
            input.remove(code);
        });

        gameLoop = new Timeline(new KeyFrame(Duration.millis(16), (event) -> {
            movePaddle();
            checkBorderCollsion();
        }));
        gameLoop.setCycleCount(Timeline.INDEFINITE);

    }

    private void movePaddle()
    {
        if (input.contains(KeyCode.LEFT)) {
            paddle2.setY(paddle2.getY() - 10);
        }
        else if (input.contains(KeyCode.RIGHT)) {
            paddle2.setY(paddle2.getY() + 10);
        }

        if (input.contains(KeyCode.Z)) {
            paddle1.setY(paddle1.getY() - 10);
        }
        else if (input.contains(KeyCode.X)) {
            paddle1.setY(paddle1.getY() + 10);
        }
    }

    private void checkBorderCollsion()
    {
        if (paddle1.getBoundsInParent().getMaxY() >= paneGameBoard.getHeight()) {
            paddle1.setY(gameBoardHeight - paddleHeight);
        }
        else if (paddle1.getBoundsInParent().getMinY() <= 0) {
            paddle1.setY(0);
        }

        if (paddle2.getBoundsInParent().getMaxY() >= paneGameBoard.getHeight()) {
            paddle2.setY(gameBoardHeight - paddleHeight);
        }
        else if (paddle2.getBoundsInParent().getMinY() <= 0) {
            paddle2.setY(0);
        }
    }
}
