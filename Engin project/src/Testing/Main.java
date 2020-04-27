package Testing;

import core.Shado3D.Object3D;
import core.Shado3D.Renderer;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import shapes.Shado;
import shapes.Timer;

public class Main extends Application {

	private final long[] frameTimes = new long[100];
	private int frameTimeIndex = 0;

	// ==================== Cube mesh==================

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Shado 3D Objects renderer test");
		Group root = new Group();
		Canvas canvas = new Canvas(1920, 1080);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		// FPS text
		final Shado.Text FPS_TEXT = new Shado.Text("Loading...", 10, 30);
		FPS_TEXT.setFill(Color.WHITE);

		// init renderer
		Renderer renderer = new Renderer(canvas);

		// Create object
		Object3D obj = Object3D.loadOBJ("src/DataFiles/teapot.obj");
		// Object3D obj2 = Object3D.loadOBJ("src/DataFiles/VideoShip.obj");

		new AnimationTimer() {
			public void handle(long now) {

				renderer.clear();

				renderer.add(obj);

				// Render stuff
				renderer.render();
				renderer.rotateX(0.005);
				renderer.rotateZ(0.01);

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
