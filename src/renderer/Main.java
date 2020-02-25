package renderer;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logger.Logger;
import shapes.Shado;
import shapes.Timer;

public class Main extends Application {

    public static final Logger LOGGER = new Logger(false);
    private final long[] frameTimes = new long[100];
    private int frameTimeIndex = 0;

    public static void main(String[] args) {
        launch(args);

        try {
            LOGGER.close();
        } catch (Exception e) {
            System.out.print(e.getMessage() + " ");
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Drawing Operations Test");
        Group root = new Group();
        Canvas canvas = new Canvas(1920, 1080);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // clear the canvas and Draw shapes
        final Shado.Text FPS_TEXT = new Shado.Text("Loading...", 10, 30);
        FPS_TEXT.setFill(Color.WHITE);

        new AnimationTimer() {
            public void handle(long now) {
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

                // Black background
                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                // Calculate and display FPS
                long oldFrameTime = frameTimes[frameTimeIndex];
                frameTimes[frameTimeIndex] = now;
                frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length;

                long elapsedNanos = now - oldFrameTime;
                long elapsedNanosPerFrame = elapsedNanos / frameTimes.length;
                Timer.setFramerate(1_000_000_000.0 / elapsedNanosPerFrame);

                if (Timer.framerate() >= 1) {
                    Timer.deltaTime = elapsedNanos / 100000000D;
                    FPS_TEXT.setText(String.format("%.3f FPS", Timer.framerate()));

                    // Increment the time elapsed since program start
                    Timer.addTime(Timer.deltaTime);
                }

                // Draw FPS text
                FPS_TEXT.draw(gc);

            }
        }.start();


        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
