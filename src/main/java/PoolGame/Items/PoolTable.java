package PoolGame.Items;

import java.util.*;

import PoolGame.Drawable;
import PoolGame.Game;
import PoolGame.Config.TableConfig;
import PoolGame.Items.Ball.BallType;
import PoolGame.TableRecord;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/** A pool table */
public class PoolTable implements Drawable {
    private int hit_num = 0;
    private long[] dim;
    private String colourName;
    private Color colour;
    private double friction;
    private Rectangle shape;
    private List<Ball> balls;
    private List<Pocket> pockets;
    private Label timerLabel;
    private int seconds = 0;

    private Label scoreLabel;
    private int score = 0;
    private Text messageLabel;
    private Button buttonRollback;
    public static final double LabelX = 100;
    private double LabelY = 30;
    public static final double LabelOffset = 10;
    private List<TableRecord> records;

    /**
     * Offset of pockets on the table.
     */
    public static final double POCKET_OFFSET = 5;


    /**
     * Build the pool table with the provided values
     * @param colourName The colour of the table in String
     * @param friction The friction of the table
     * @param dimX The dimension of the table in the x-axis
     * @param dimY The dimension of the table in the y-axis
     */
    public PoolTable(String colourName, double friction, long dimX, long dimY) {
        this.init(colourName, friction, dimX, dimY);
    }

    /**
     * Build the pool table using a `TableConfig` instance
     * @param config The `TableConfig` instance
     */
    public PoolTable(TableConfig config) {
        this.init(config.getColour(),
            config.getFriction(),
            config.getSizeConfig().getX(),
            config.getSizeConfig().getY());
    }

    private void init(String colourName, double friction, long dimX, long dimY) {
        this.colourName = colourName;
        this.colour = Color.valueOf(this.colourName);
        this.friction = friction;
        this.dim = new long[2];
        this.dim[0] = dimX;
        this.dim[1] = dimY;
        this.shape = new Rectangle(this.dim[0], this.dim[1], this.colour);
        this.balls = new LinkedList<>();
        this.pockets = new ArrayList<>();
        this.records = new ArrayList<>();
        this.pockets.add(new Pocket(POCKET_OFFSET, POCKET_OFFSET));
        this.pockets.add(new Pocket(dimX / 2, POCKET_OFFSET));
        this.pockets.add(new Pocket(dimX - POCKET_OFFSET, POCKET_OFFSET));
        this.pockets.add(new Pocket(POCKET_OFFSET, dimY - POCKET_OFFSET));
        this.pockets.add(new Pocket(dimX / 2, dimY - POCKET_OFFSET));
        this.pockets.add(new Pocket(dimX - POCKET_OFFSET, dimY - POCKET_OFFSET));

        this.timerLabel = new Label("0 seconds");
        this.timerLabel.setLayoutX(dimX);
        this.timerLabel.setLayoutY(0);
        this.timerLabel.setPrefWidth(LabelX);
        this.timerLabel.setPrefHeight(LabelY);
        this.timerLabel.setStyle("-fx-alignment: center; -fx-background-color: lightblue;");
        this.timerLabel.setTextFill(Color.BLACK);
        startTimer();

        this.scoreLabel = new Label("score: " + score);
        this.scoreLabel.setLayoutX(dimX);
        this.scoreLabel.setLayoutY(LabelY + LabelOffset);
        this.scoreLabel.setPrefWidth(LabelX);
        this.scoreLabel.setPrefHeight(LabelY);
        this.scoreLabel.setStyle("-fx-alignment: center; -fx-background-color: lightblue;");
        this.scoreLabel.setTextFill(Color.BLACK);

        this.buttonRollback = new Button("RollBack");
        this.buttonRollback.setLayoutX(dimX);
        this.buttonRollback.setLayoutY(2 * LabelY + 2 * LabelOffset);
        this.buttonRollback.setPrefWidth(LabelX);
        this.buttonRollback.setPrefHeight(LabelY);
        this.buttonRollback.setStyle("-fx-alignment: center; -fx-background-color: lightblue;");
        this.buttonRollback.setTextFill(Color.BLACK);
        this.buttonRollback.setOnAction(e -> Rollback());

        this.messageLabel = new Text();
        this.messageLabel.setText("""
                Scoring Rules
                RED--1
                YELLOW--2
                GREEN--3
                BROWN--4
                BLUE--5
                PURPLE--6
                ORANGE--7
                BLACK--8""");
        this.messageLabel.setLayoutX(dimX - LabelX);
        this.messageLabel.setLayoutY(3 * LabelY + 4 * LabelOffset);
        this.messageLabel.setTextAlignment(TextAlignment.RIGHT);
        this.messageLabel.setX(LabelX);
    }

    private void Rollback() {
        if (records.isEmpty()) return ;

        int index = records.size() - 1;
        TableRecord record = records.remove(index);
        for (int i = 0; i < balls.size(); i++) {
            balls.get(i).setXPos(record.getBallX().get(i));
            balls.get(i).setYPos(record.getBallY().get(i));
            if(balls.get(i).isDisabled() && !record.getDisabled().get(i)) {
                balls.get(i).enable();
            }
            balls.get(i).setDisabled(record.getDisabled().get(i));
            balls.get(i).setFallCounter(record.getFallCounter().get(i));
            balls.get(i).resetVelocity();
        }
        this.score = record.getScore();
        this.seconds = record.getSeconds();
        this.scoreLabel.setText("score :" + this.score);
        this.timerLabel.setText(this.seconds + " seconds");
    }
    public void LoggingStatus() {
        records.add(new TableRecord(balls, score, seconds));
        System.out.println(records.size());
    }
    private void startTimer() {
        // 创建时间轴 (Timeline) 用于更新计时器
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            seconds++;
            updateTimerLabel();
        }));

        // 设置时间轴为无限循环
        timeline.setCycleCount(Animation.INDEFINITE);

        // 启动时间轴
        timeline.play();
    }

    private void updateTimerLabel() {
        // 更新 Label 显示
        timerLabel.setText(seconds + " seconds");
    }

    /**
     * Get the x dimension of the table.
     * @return The dimension of the table for the x axis.
     */
    public long getDimX() {
        return this.dim[0];
    }

    /**
     * Get the y dimension of the table.
     * @return The dimension of the table for the y axis.
     */
    public long getDimY() {
        return this.dim[1];
    }

    /**
     * Get the colour of the table.
     * @return The colour of the table.
     */
    public Color getColour() {
        return this.colour;
    }

    /**
     * Get the friction of the table.
     * @return The friction value of the table.
     */
    public double getFriction() {
        return this.friction;
    }

    public Node getNode() {
        return this.shape;
    }

    /**
     * Add a ball onto the pool table
     * @param ball The ball to be added
     */
    public void addBall(Ball ball) {
        if (!this.balls.contains(ball)) {
            this.balls.add(ball);
        }
    }

    /**
     * Get all balls on table.
     * @return List of ball on the table.
     */
    public List<Ball> getBalls() {
        return this.balls;
    }

    /**
     * Set up table with the list of balls, which includes the CueBall.
     * @param balls The list of balls to be added to the table
     */
    public void setupBalls(List<Ball> balls) {
        for (Ball ball : balls) {
            // if (ball.getBallType() == Ball.BallType.CUEBALL) {
            //     this.setCueBall(ball);
            // }
            this.addBall(ball);
        }
    }

    /**
     * Add the table and the balls to the JavaFX group so they can be drawn.
     * @param groupChildren The list of `Node` obtained from the JavaFX Group.
     */
    @Override
    public void addToGroup(ObservableList<Node> groupChildren) {
        groupChildren.add(this.shape);
        for (Pocket pocket : this.pockets) {
            pocket.addToGroup(groupChildren);
        }
        for (Ball ball : this.balls) {
            ball.addToGroup(groupChildren);
        }

        groupChildren.add(this.timerLabel);
        groupChildren.add(this.scoreLabel);
        groupChildren.add(this.buttonRollback);
        groupChildren.add(this.messageLabel);
    }

    /**
     * Apply friction to all the balls
     */
    public void applyFrictionToBalls() {
        for (Ball ball : this.balls) {
            ball.applyFriction(this.getFriction());
        }
    }

    public void checkHit() {
        if (Ball.hit_num != this.hit_num) {
            System.out.println(Ball.hit_num+","+this.hit_num);
            LoggingStatus();
            this.hit_num = Ball.hit_num;
        }
    }

    /**
     * Check if any of the balls is in a pocket and handle the ball in the 
     * pocket
     * @param game The instance of the game
     */
    public void checkPocket(Game game) {
        for (Pocket pocket : this.pockets) {
            for (Ball ball : this.balls) {
                if (ball.isDisabled()) {
                    continue;
                }
                Point2D ballCenter = new Point2D(ball.getXPos(), ball.getYPos());
                if (pocket.isInPocket(ballCenter)) {
                    ball.fallIntoPocket(game);
                    if (ball.getColour().equals(Color.RED)) {
                        this.score++;
                    } else if (ball.getColour().equals(Color.YELLOW)) {
                        this.score += 2;
                    } else if (ball.getColour().equals(Color.GREEN)) {
                        this.score += 3;
                    } else if (ball.getColour().equals(Color.BROWN)) {
                        this.score += 4;
                    } else if (ball.getColour().equals(Color.BLUE)) {
                        this.score += 5;
                    } else if (ball.getColour().equals(Color.PURPLE)) {
                        this.score += 6;
                    } else if (ball.getColour().equals(Color.ORANGE)) {
                        this.score += 7;
                    } else if (ball.getColour().equals(Color.BLACK)) {
                        this.score += 8;
                    }
                    scoreLabel.setText("score :" + score);
                }
            }
        }
    }

    /**
     * Handle the collision between the balls and table and between balls.
     */
    public void handleCollision() {
        Bounds tableBounds = this.shape.getBoundsInLocal();
        for (Ball ball : this.balls) {
            if (ball.isDisabled()) {
                continue;
            }
            Bounds ballBound = ball.getLocalBounds();
            if (!tableBounds.contains(ballBound)) {
                if (ballBound.getMaxX() >= tableBounds.getMaxX()) {
                    ball.setXVel(-ball.getXVel());
                    ball.setXPos(tableBounds.getMaxX() - ball.getRadius());
                } else if (ballBound.getMinX() <= tableBounds.getMinX()){
                    ball.setXVel(-ball.getXVel());
                    ball.setXPos(tableBounds.getMinX() + ball.getRadius());
                }
                if (ballBound.getMaxY() >= tableBounds.getMaxY()) {
                    ball.setYVel(-ball.getYVel());
                    ball.setYPos(tableBounds.getMaxY() - ball.getRadius());
                } else if (ballBound.getMinY() <= tableBounds.getMinY()) {
                    ball.setYVel(-ball.getYVel());
                    ball.setYPos(tableBounds.getMinY() + ball.getRadius());
                }
            }
            for (Ball ballB : this.balls) {
                if (ballB.isDisabled()) {
                    continue;
                }
                // if (ball.getBallType() == BallType.CUEBALL && ball.isColliding(ballB)) {
                //     System.out.printf("%f, %f, %s\n", ballB.getXVel(), ballB.getYVel(), ballB.isColliding(ball));
                // }
                if (ball.isColliding(ballB)) {
                    ball.handleCollision(ballB);
                }
            }
            
        }
    }

    /**
     * If all the balls has been disabled except the cue ball, the game has ended
     * and the player won.
     * @return The win status of the game.
     */
    public boolean hasWon() {
        boolean won = true;
        for (Ball ball : this.balls) {
            if (ball.getBallType() == BallType.CUEBALL) {
                continue;
            }
            if (!ball.isDisabled()) {
                won = false;
                break;
            }
        }
        return won;
    }

    /**
     * Reset the game.
     */
    public void reset() {
        for (Ball ball : this.balls) {
            ball.reset();
        }
    }
}
