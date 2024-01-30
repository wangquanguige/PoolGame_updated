package PoolGame;

import PoolGame.Items.Ball;

import java.util.ArrayList;
import java.util.List;

public class TableRecord {
    private List<Double> ballX;
    private List<Double> ballY;
    private List<Boolean> disabled;
    private List<Integer> fallCounter;
    private int score;
    private int seconds;
    public TableRecord(List<Ball> balls, int score, int seconds) {
        this.ballX = new ArrayList<>();
        this.ballY = new ArrayList<>();
        this.disabled = new ArrayList<>();
        this.fallCounter = new ArrayList<>();

        setBall(balls);
        this.score = score;
        this.seconds = seconds;
    }

    private void setBall(List<Ball> balls) {
        for (Ball ball : balls) {
            addBallX(ball.getXPos());
            addBallY(ball.getYPos());
            addDisabled(ball.isDisabled());
            addFallCounter(ball.getFallCounter());
        }
    }

    private void addBallX(double x) {
        this.ballX.add(x);
    }

    private void addBallY(double y) {
        this.ballY.add(y);
    }

    private void addDisabled(boolean disabled) {
        this.disabled.add(disabled);
    }

    private void addFallCounter(int fallCounter) {
        this.fallCounter.add(fallCounter);
    }

    public List<Double> getBallX() {
        return ballX;
    }

    public List<Double> getBallY() {
        return ballY;
    }

    public List<Boolean> getDisabled() {
        return disabled;
    }

    public List<Integer> getFallCounter() {
        return fallCounter;
    }

    public int getScore() {
        return score;
    }

    public int getSeconds() {
        return seconds;
    }
}
