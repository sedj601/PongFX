/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pongfx;

import java.util.Random;
import javafx.geometry.Bounds;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author blj0011
 */
public class PongAIPlayer
{
    private final Rectangle paddle;
    private final Random random = new Random();
    private boolean isHitLocationCalculated = false;
    private double paddleHitLocation;

    public PongAIPlayer(Rectangle paddle)
    {
        this.paddle = paddle;
    }

    public void MovePaddle(boolean isBallComingAtPaddle, double ballLocationX, double ballLocationY)
    {

        if (isBallComingAtPaddle) {

            if (!isHitLocationCalculated) {
                paddleHitLocation = this.paddle.getBoundsInParent().getHeight() * random.nextDouble();
                isHitLocationCalculated = true;
                System.out.println(paddleHitLocation);
            }
            Bounds paddleBounds = paddle.getBoundsInParent();

            if (paddleBounds.getMinY() + paddleHitLocation > ballLocationY) {
                this.paddle.setY(this.paddle.getY() - 10);
            }
            else if (paddleBounds.getMaxY() - paddleHitLocation < ballLocationY) {
                this.paddle.setY(this.paddle.getY() + 10);
            }
        }
        else {
            isHitLocationCalculated = false;
        }
    }

}
