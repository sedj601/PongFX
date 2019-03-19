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
    double gameBoardHeight = 600;
    double gameBoardWidth = 900;
    double paddleWidth = 7;
    double paddleHeight = 100;
    double bounceAngle = 0;
    double maxBounceAngle = 5 * Math.PI / 12;
    int ballSpeedX = 13;
    int ballSpeedY = 13;
    PongAIPlayer player1, player2;

    @FXML
    private void handleBtnNewGame(ActionEvent event)
    {
        Platform.runLater(() -> paneGameBoard.requestFocus());
        switch (gameLoop.getStatus()) {
            case STOPPED:
                gameLoop.play();
                break;
            case RUNNING:
                gameLoop.stop();
                maxBounceAngle = 5 * Math.PI / 12;
                bounceAngle = 0;
                ballSpeedX = 12;
                ballSpeedY = 12;
                gameLoop.play();
                break;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {

        ball = new Circle(gameBoardWidth / 2.0, gameBoardHeight / 2.0, 7, Color.WHITE);

        paddle1 = new Rectangle(paddleWidth, paddleHeight, Color.WHITE);
        paddle1.setX(0);
        paddle1.setY((gameBoardHeight - paddleHeight) / 2);
        player1 = new PongAIPlayer(paddle1);

        paddle2 = new Rectangle(paddleWidth, paddleHeight, Color.WHITE);
        paddle2.setX(gameBoardWidth - paddleWidth);
        paddle2.setY((gameBoardHeight - paddleHeight) / 2);
        player2 = new PongAIPlayer(paddle2);

        paneGameBoard.getChildren().addAll(ball, paddle1, paddle2);

        paneGameBoard.setOnKeyPressed((event) -> {
            KeyCode code = event.getCode();
            input.add(code);
        });

        paneGameBoard.setOnKeyReleased((event) -> {
            KeyCode code = event.getCode();
            input.remove(code);
        });

        gameLoop = new Timeline(new KeyFrame(Duration.millis(16), (event) -> {
            moveBall();
            movePaddle();

            if (ballSpeedX < 0) {
                player1.MovePaddle(true, ball.getCenterX(), ball.getCenterY());
                player2.MovePaddle(false, ball.getCenterX(), ball.getCenterY());
            }
            else {
                player1.MovePaddle(false, ball.getCenterX(), ball.getCenterY());
                player2.MovePaddle(true, ball.getCenterX(), ball.getCenterY());
            }

            checkBorderCollision();
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

    private void checkBorderCollision()
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

    private void moveBall()
    {
        if (ball.getBoundsInParent().intersects(paddle1.getBoundsInParent())) {
            double intersectY = ball.getCenterY() - paddle1.getBoundsInParent().getMinY();
            double relativeIntersectY = intersectY - (paddleHeight / 2);
            double normalizedRelativeIntersectionY = relativeIntersectY / (paddleHeight / 2);
            bounceAngle = normalizedRelativeIntersectionY * maxBounceAngle;
            ballSpeedX *= -1;
            ball.setCenterX(ball.getCenterX() + ballSpeedX * Math.cos(bounceAngle));
            ball.setCenterY(ball.getCenterY() + ballSpeedY * -Math.sin(bounceAngle));
        }
        else if (ball.getBoundsInParent().intersects(paddle2.getBoundsInParent())) {
            double intersectY = ball.getCenterY() - paddle2.getBoundsInParent().getMinY();
            double relativeIntersectY = intersectY - (paddleHeight / 2);
            double normalizedRelativeIntersectionY = relativeIntersectY / (paddleHeight / 2);
            bounceAngle = normalizedRelativeIntersectionY * maxBounceAngle;
            ballSpeedX *= -1;
            ball.setCenterX(ball.getCenterX() + ballSpeedX * Math.cos(bounceAngle));
            ball.setCenterY(ball.getCenterY() + ballSpeedY * -Math.sin(bounceAngle));
        }
        else if (ball.getBoundsInParent().getMinY() <= 0) {
            ballSpeedY *= -1;
            ball.setCenterX(ball.getCenterX() + ballSpeedX * Math.cos(bounceAngle));
            ball.setCenterY(ball.getCenterY() + ballSpeedY * -Math.sin(bounceAngle));
        }
        else if (ball.getBoundsInParent().getMaxY() >= paneGameBoard.getHeight()) {
            ballSpeedY *= -1;
            ball.setCenterX(ball.getCenterX() + ballSpeedX * Math.cos(bounceAngle));
            ball.setCenterY(ball.getCenterY() + ballSpeedY * -Math.sin(bounceAngle));
        }
        else {
            ball.setCenterX(ball.getCenterX() + ballSpeedX * Math.cos(bounceAngle));
            ball.setCenterY(ball.getCenterY() + ballSpeedY * -Math.sin(bounceAngle));
        }
    }

}
