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
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;
import logger.Logger;
import shapes.Shado;
import shapes.Timer;

public class Main extends Application {

	public static final Logger LOGGER = new Logger(false);
	private final long[] frameTimes = new long[100];
	private int frameTimeIndex = 0;

	//==================== Cube mesh==================

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
		primaryStage.setTitle("Shado 3D Objects renderer test");
		Group root = new Group();
		Canvas canvas = new Canvas(1280, 720);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		// FPS text
		final Shado.Text FPS_TEXT = new Shado.Text("Loading...", 10, 30);
		FPS_TEXT.setFill(Color.WHITE);

		// init renderer
		Renderer renderer = new Renderer(canvas);

		// Create object
		//Object3D obj = Object3D.loadOBJ("src/DataFiles/Fusepresquefinal.obj");
		Object3D obj2 = Object3D.loadOBJ("src/DataFiles/tea.obj");

		Sphere sphere = new Sphere(200);
		sphere.setTranslateX(canvas.getWidth() / 2);
		sphere.setTranslateY(canvas.getHeight() / 2);
		root.getChildren().add(sphere);

		new AnimationTimer() {
			public void handle(long now) {

				renderer.clear();

				renderer.add(obj2);
				//renderer.add(obj2);

				// Render stuff
				renderer.render();
				renderer.rotateX(0.005);
				renderer.rotateZ(0.005);


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
