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
import javafx.scene.control.Label;
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

    @FXML
    private Label lblPlayer1Score, lblPlayer2Score;

    Timeline gameLoop;
    Rectangle leftPaddle, rightPaddle; //Paddle 1 is left paddle
    Circle ball;
    Set<KeyCode> input = new HashSet();
    double gameBoardHeight = 600;
    double gameBoardWidth = 900;
    double paddleWidth = 7;
    double paddleHeight = 100;
    double bounceAngle = 0;
    double maxBounceAngle = 5 * Math.PI / 12;
    int ballSpeedX = -12;
    int ballSpeedY = 12;
    PongAIPlayer player1, player2;
    int player1Score = 0;
    int player2Score = 0;

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

        leftPaddle = new Rectangle(paddleWidth, paddleHeight, Color.WHITE);
        leftPaddle.setX(10);
        leftPaddle.setY((gameBoardHeight - paddleHeight) / 2);
        player1 = new PongAIPlayer(leftPaddle);
        player1.setPaddleSpeed(15);

        rightPaddle = new Rectangle(paddleWidth, paddleHeight, Color.WHITE);
        rightPaddle.setX(gameBoardWidth - paddleWidth - 10);
        rightPaddle.setY((gameBoardHeight - paddleHeight) / 2);
        player2 = new PongAIPlayer(rightPaddle);
        player2.setPaddleSpeed(15);

        paneGameBoard.getChildren().addAll(ball, leftPaddle, rightPaddle);

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
            movePaddle();//Human Player
            aiPaddlesMove();//AI Player
        }));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
    }

    //Human Player Controls
    private void movePaddle()
    {
        if (input.contains(KeyCode.LEFT)) {
            rightPaddle.setY(rightPaddle.getY() - 10);
        }
        else if (input.contains(KeyCode.RIGHT)) {
            rightPaddle.setY(rightPaddle.getY() + 10);
        }

        if (input.contains(KeyCode.Z)) {
            leftPaddle.setY(leftPaddle.getY() - 10);
        }
        else if (input.contains(KeyCode.X)) {
            leftPaddle.setY(leftPaddle.getY() + 10);
        }
    }

    private void checkScore()
    {
        if (ball.getBoundsInParent().getMinX() <= 0) {
            player2Score += 1;
            lblPlayer2Score.setText(Integer.toString(player2Score));
            gameLoop.stop();

        }
        else if (ball.getBoundsInParent().getMaxX() >= paneGameBoard.getWidth()) {
            player1Score += 1;
            lblPlayer1Score.setText(Integer.toString(player1Score));
            gameLoop.stop();
        }
    }

    private void moveBall()
    {
        checkPaddleCollision();
        checkWallCollision();
        checkScore();
    }

    private void checkWallCollision()
    {
        if (ball.getBoundsInParent().getMinY() <= 0) {
            ball.setCenterY(ball.getCenterY() + (0 - ball.getBoundsInParent().getMinY()));
            ballSpeedY *= -1;
        }
        else if (ball.getBoundsInParent().getMaxY() >= paneGameBoard.getHeight()) {
            ball.setCenterY(ball.getCenterY() - (ball.getBoundsInParent().getMaxY() - paneGameBoard.getHeight()));
            ballSpeedY *= -1;
        }

    }

    private void checkPaddleCollision()
    {
        if (ball.getBoundsInParent().intersects(leftPaddle.getBoundsInParent())) {
            ball.setCenterX((ball.getCenterX()) + (leftPaddle.getBoundsInParent().getMaxX() - ball.getBoundsInParent().getMinX()));
            ballSpeedX *= -1;

            double intersectY = ball.getCenterY() - leftPaddle.getBoundsInParent().getMinY();
            double relativeIntersectY = intersectY - (paddleHeight / 2);
            double normalizedRelativeIntersectionY = relativeIntersectY / (paddleHeight / 2);
            bounceAngle = normalizedRelativeIntersectionY * maxBounceAngle;

            ball.setCenterX(ball.getCenterX() + ballSpeedX * Math.cos(bounceAngle));
            ball.setCenterY(ball.getCenterY() + ballSpeedY * -Math.sin(bounceAngle));
        }
        else if (ball.getBoundsInParent().intersects(rightPaddle.getBoundsInParent())) {
            ball.setCenterX((ball.getCenterX()) - (ball.getBoundsInParent().getMaxX() - rightPaddle.getBoundsInParent().getMinX()));
            ballSpeedX *= -1;

            double intersectY = ball.getCenterY() - rightPaddle.getBoundsInParent().getMinY();
            double relativeIntersectY = intersectY - (paddleHeight / 2);
            double normalizedRelativeIntersectionY = relativeIntersectY / (paddleHeight / 2);
            bounceAngle = normalizedRelativeIntersectionY * maxBounceAngle;

            ball.setCenterX(ball.getCenterX() + ballSpeedX * Math.cos(bounceAngle));
            ball.setCenterY(ball.getCenterY() + ballSpeedY * -Math.sin(bounceAngle));
        }
        else {
            ball.setCenterX(ball.getCenterX() + ballSpeedX * Math.cos(bounceAngle));
            ball.setCenterY(ball.getCenterY() + ballSpeedY * -Math.sin(bounceAngle));
        }
    }

    private void aiPaddlesMove()
    {
        if (ballSpeedX < 0) {
            player1.MovePaddle(true, ball.getCenterX(), ball.getCenterY());
            player2.MovePaddle(false, ball.getCenterX(), ball.getCenterY());
        }
        else {
            player1.MovePaddle(false, ball.getCenterX(), ball.getCenterY());
            player2.MovePaddle(true, ball.getCenterX(), ball.getCenterY());
        }
    }

    public void newBallReset()
    {

    }
}
