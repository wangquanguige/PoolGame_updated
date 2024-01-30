package PoolGame;

import java.util.ArrayList;
import java.util.List;

import PoolGame.Builder.BallBuilderDirector;
import PoolGame.Config.BallConfig;
import PoolGame.Items.Ball;
import PoolGame.Items.PoolTable;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;

/** The game class that runs the game */
public class Game {
    private PoolTable table;
    private boolean shownWonText = false;
    private final Text winText = new Text(50, 50, "Win and Bye");

    /**
     * Initialise the game with the provided config
     * @param config The config parser to load the config from
     */
    public Game(ConfigReader config) {
        this.setup(config);
    }

    private void setup(ConfigReader config) {
        this.table = new PoolTable(config.getConfig().getTableConfig());
        List<BallConfig> ballsConf = config.getConfig().getBallsConfig().getBallConfigs();
        List<Ball> balls = new ArrayList<>();
        BallBuilderDirector builder = new BallBuilderDirector();
        builder.registerDefault();
        for (BallConfig ballConf: ballsConf) {
            Ball ball = builder.construct(ballConf);
            if (ball == null) {
                System.err.println("WARNING: Unknown ball, skipping...");
            } else {
                balls.add(ball);
            }
        }
        this.table.setupBalls(balls);
        this.winText.setVisible(false);
        this.winText.setX(table.getDimX() / 2);
        this.winText.setY(table.getDimY() / 2);
    }

    /**
     * Get the window dimension in the x-axis
     * @return The x-axis size of the window dimension
     */
    public double getWindowDimX() {
        return this.table.getDimX();
    }

    /**
     * Get the window dimension in the y-axis
     * @return The y-axis size of the window dimension
     */
    public double getWindowDimY() {
        return this.table.getDimY();
    }

    /**
     * Get the pool table associated with the game
     * @return The pool table instance of the game
     */
    public PoolTable getPoolTable() {
        return this.table;
    }

    /** Add all drawable object to the JavaFX group
     * @param root The JavaFX `Group` instance
    */
    public void addDrawables(Group root) {
        ObservableList<Node> groupChildren = root.getChildren();
        table.addToGroup(groupChildren);
        groupChildren.add(this.winText);
    }

    /** Reset the game */
    public void reset() {
        this.winText.setVisible(false);
        this.shownWonText = false;
        this.table.reset();
    }

    /** Code to execute every tick. */
    public void tick() {
        if (table.hasWon() && !this.shownWonText) {
            System.out.println(this.winText.getText());
            this.winText.setVisible(true);
            this.shownWonText = true;
        }
        table.checkPocket(this);
        table.handleCollision();
        this.table.applyFrictionToBalls();
        for (Ball ball : this.table.getBalls()) {
            ball.move();
        }
        table.checkHit();
    }
}
