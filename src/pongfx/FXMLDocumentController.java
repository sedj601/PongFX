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
    double paddleWidth = 15;
    double paddleHeight = 100;
    double bounceAngle = 0;
    double maxBounceAngle = 5 * Math.PI / 12;
    int ballSpeedX = 10;
    int ballSpeedY = 10;

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
                bounceAngle = 0;
                maxBounceAngle = 5 * Math.PI / 12;
                ballSpeedX = 10;
                ballSpeedY = 10;
                gameLoop.play();
                break;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        ball = new Circle(gameBoardWidth / 2.0, gameBoardHeight / 2.0, 7, Color.WHITE);

        paddle1 = new Rectangle(12, 100, Color.WHITE);

        paddle1.setX(0);
        paddle1.setY((gameBoardHeight - paddleHeight) / 2);

        paddle2 = new Rectangle(15, 100, Color.WHITE);
        paddle2.setX(gameBoardWidth - paddleWidth);
        paddle2.setY((gameBoardHeight - paddleHeight) / 2);

        paneGameBoard.getChildren().addAll(ball, paddle1, paddle2);

        paneGameBoard.setOnKeyPressed((event) -> {
            KeyCode code = event.getCode();
//            System.out.println("press: " + input.toString());
            input.add(code);
        });

        paneGameBoard.setOnKeyReleased((event) -> {
            KeyCode code = event.getCode();
//            System.out.println("release: " + input.toString());
            input.remove(code);
        });

        gameLoop = new Timeline(new KeyFrame(Duration.millis(20), (event) -> {
            movePaddle();
            checkBorderCollision();
            moveBall();
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
        System.out.println(ball.getBoundsInParent().getMinY());
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
