import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class JavaRun extends Application {
    private static final int SCENE_WIDTH = 800;
    private static final int SCENE_HEIGHT = 400;

    private Rectangle player;
    private double playerVelocity = 0;
    private boolean isJumping = false;

    private List<Rectangle> obstacles = new CopyOnWriteArrayList<>();
    private Random random = new Random();

    private int score = 0;
    private Text scoreText;

    private double obstacleDistance = 0;
    private final int METERS_PER_SCORE = 50;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

        // Create player rectangle
        player = new Rectangle(30, 30, Color.BLUE);
        player.setTranslateY(SCENE_HEIGHT - player.getHeight());
        root.getChildren().add(player);

        // Create initial obstacles
        createObstacle();

        // Create and add text node for "Java Run"
        Text titleText = new Text("Java Run");
        titleText.setFont(Font.font(30));
        titleText.setFill(Color.BLACK);
        titleText.setTranslateX((SCENE_WIDTH - titleText.getLayoutBounds().getWidth()) / 2);
        titleText.setTranslateY(30);
        root.getChildren().add(titleText);

        // Create and add text node for score
        scoreText = new Text("Score: " + score);
        scoreText.setFont(Font.font(30));
        scoreText.setFill(Color.BLACK);
        scoreText.setTranslateX(SCENE_WIDTH - scoreText.getLayoutBounds().getWidth() - 100);
        scoreText.setTranslateY(30);
        root.getChildren().add(scoreText);

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE && !isJumping) {
                jump();
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaRun");
        primaryStage.show();

        // Game loop
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        gameLoop.start();
    }

    private void jump() {
        isJumping = true;
        playerVelocity = -20;
    }

    private void update() {
        // Update player position
        playerVelocity += 1; // Gravity
        double newY = player.getTranslateY() + playerVelocity;

        // Check if the player has landed
        if (newY >= SCENE_HEIGHT - player.getHeight()) {
            newY = SCENE_HEIGHT - player.getHeight();
            isJumping = false;
        }

        player.setTranslateY(newY);

        // Move obstacles
        moveObstacles();

        obstacleDistance += 5;

        if (obstacleDistance >= METERS_PER_SCORE) {
            score += 50; // Add 50 score for every couple of meters
            scoreText.setText("Score: " + score);
            obstacleDistance = 0; // Reset the distance
        }

        // Check for collisions
        if (checkCollisions()) {
            showGameOverScreen();
            return; // Stop the game loop
        }

        // Check for successful jump over obstacles
        checkJumpOverObstacles();

        // Create new obstacles periodically
        if (Math.random() < 0.002) {
            createObstacle();
        }
    }

    private void moveObstacles() {
        for (Rectangle obstacle : obstacles) {
            obstacle.setTranslateX(obstacle.getTranslateX() - 5);

            // Remove obstacles that are out of the scene
            if (obstacle.getTranslateX() + obstacle.getWidth() < 0) {
                obstacles.remove(obstacle);
                createObstacle();
            }
        }
    }

    private void createObstacle() {
        Rectangle obstacle = new Rectangle(30, 30, Color.RED);
        obstacle.setTranslateX(SCENE_WIDTH + random.nextInt(200));
        obstacle.setTranslateY(SCENE_HEIGHT - obstacle.getHeight());
        obstacles.add(obstacle);
        ((Pane) player.getParent()).getChildren().add(obstacle);
    }

    private boolean checkCollisions() {
        for (Rectangle obstacle : obstacles) {
            if (player.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
                return true; // Collision detected
            }
        }
        return false; // No collision
    }

    private void checkJumpOverObstacles() {
        Iterator<Rectangle> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            Rectangle obstacle = iterator.next();
            double playerBottom = player.getTranslateY() + player.getHeight();
    
            if (!isJumping && player.getBoundsInParent().intersects(obstacle.getBoundsInParent())
                    && playerBottom < obstacle.getTranslateY() && playerBottom > obstacle.getTranslateY() - 5) {
                // Player successfully jumped over the obstacle
                score += 100;
                scoreText.setText("Score: " + score);
    
                // Remove the obstacle
                iterator.remove();
                createObstacle();
            }
        }
    }    

    private void showGameOverScreen() {
        // Create a game over scene
        Pane gameOverPane = new Pane();
        Scene gameOverScene = new Scene(gameOverPane, SCENE_WIDTH, SCENE_HEIGHT);

        Label gameOverLabel = new Label("Game Over!");
        gameOverLabel.setFont(Font.font(40));
        gameOverLabel.setTextFill(Color.RED);
        gameOverLabel.setTranslateX((SCENE_WIDTH - gameOverLabel.getWidth()) / 2 - 90);
        gameOverLabel.setTranslateY(SCENE_HEIGHT / 2 - 30);
        gameOverPane.getChildren().add(gameOverLabel);

        Label finalScoreLabel = new Label("Final Score: " + score);
        finalScoreLabel.setFont(Font.font(20));
        finalScoreLabel.setTranslateX((SCENE_WIDTH - finalScoreLabel.getWidth()) / 2 - 90);
        finalScoreLabel.setTranslateY(SCENE_HEIGHT / 2 + 30);
        gameOverPane.getChildren().add(finalScoreLabel);

        Stage primaryStage = (Stage) player.getScene().getWindow();
        primaryStage.setScene(gameOverScene);
    }
}