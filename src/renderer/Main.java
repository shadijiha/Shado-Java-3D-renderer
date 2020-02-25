package renderer;

import core.Mesh;
import core.Triangle;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logger.Logger;
import shadoMath.Matrix;
import shadoMath.Vector;
import shapes.Shado;
import shapes.Timer;

public class Main extends Application {

	public static final Logger LOGGER = new Logger(false);
	private final long[] frameTimes = new long[100];
	private int frameTimeIndex = 0;

	// =================== Matrices and angles ===============================
	private final static Matrix projectionMatrix = new Matrix(4, 4);
	private static double angleZ = 0.0;
	private static double angleX = 0.0;

	private static Vector camera = new Vector(0, 0, 0);        // Position of the camera

	//==================== Cube mesh==================
	private final static Mesh cube = generateCubeMesh();

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
		Canvas canvas = new Canvas(1920, 1080);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		// FPS text
		final Shado.Text FPS_TEXT = new Shado.Text("Loading...", 10, 30);
		FPS_TEXT.setFill(Color.WHITE);

		// Init projection matrix
		double fNear = 0.1;
		double fFar = 1000.0;
		double fFov = 90.0;
		double fAspectRatio = canvas.getHeight() / canvas.getWidth();
		double fFovRad = 1.0 / Math.tan(fFov * 0.5 / 180.0 * Math.PI);

		projectionMatrix.setData(0, 0, fAspectRatio * fFovRad);
		projectionMatrix.setData(1, 1, fFovRad);
		projectionMatrix.setData(2, 2, fFar / (fFar - fNear));
		projectionMatrix.setData(3, 2, (-fFar * fNear) / (fFar - fNear));
		projectionMatrix.setData(2, 3, 1.0);
		projectionMatrix.setData(3, 3, 0.0);

		new AnimationTimer() {
			public void handle(long now) {
				gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

				// Black background
				gc.setFill(Color.BLACK);
				gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

				// Render stuff
				Main.render(gc, canvas);

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

	public static void render(GraphicsContext g, Canvas c) {

		// Increment angles
		angleZ += 0.01;
		angleX += 0.005;

		Matrix rotationZ = Matrix.rotationZ(angleZ);
		Matrix rotationX = Matrix.rotationX(angleX);

		// Draw triangles
		for (var tri : cube.tris) {

			Triangle triProjected = new Triangle();
			Triangle triRotatedZ = new Triangle();
			Triangle triRotatedZX = new Triangle();

			// Rotate in Z
			for (int i = 0; i < tri.vectors.length; i++) {
				triRotatedZ.vectors[i] = multiplyMatrixVector(tri.vectors[i], rotationZ);
			}

			// Rotate in X
			for (int i = 0; i < triRotatedZ.vectors.length; i++) {
				triRotatedZX.vectors[i] = multiplyMatrixVector(triRotatedZ.vectors[i], rotationX);
			}

			// Translate the triangle
			Triangle triTranslated = new Triangle(triRotatedZX);
			for (Vector v : triTranslated.vectors) {
				v.z += 3.0;
			}

			Vector normal = triTranslated.getNormal();
			normal.normalize();

			// Only draw Visible triangles
			if (normal.dotProduct(triTranslated.vectors[0].substract(camera)) < 0.0) {
				// Project the triangle (from 3D ----> 2D)
				for (int i = 0; i < triTranslated.vectors.length; i++) {
					triProjected.vectors[i] = multiplyMatrixVector(triTranslated.vectors[i], projectionMatrix);
				}

				// Scale the triangle to the screen size
				for (Vector v : triProjected.vectors) {
					v.x += 1.0;
					v.y += 1.0;

					v.x *= 0.5 * c.getWidth();
					v.y *= 0.5 * c.getHeight();
				}

				triProjected.draw(g, Color.WHITE);
			}

		}
	}

	public static Vector multiplyMatrixVector(Vector i, Matrix m) {

		if (m.getRows() == 3 && m.getCols() == 3) {
			Vector o = new Vector();

			o.x = i.x * m.getData(0, 0) + i.y * m.getData(1, 0) + i.z * m.getData(2, 0);
			o.y = i.x * m.getData(0, 1) + i.y * m.getData(1, 1) + i.z * m.getData(2, 1);
			o.z = i.x * m.getData(0, 2) + i.y * m.getData(1, 2) + i.z * m.getData(2, 2);

			return o;
		} else {

			Vector o = new Vector();

			o.x = i.x * m.getData(0, 0) + i.y * m.getData(1, 0) + i.z * m.getData(2, 0) + m.getData(3, 0);
			o.y = i.x * m.getData(0, 1) + i.y * m.getData(1, 1) + i.z * m.getData(2, 1) + m.getData(3, 1);
			o.z = i.x * m.getData(0, 2) + i.y * m.getData(1, 2) + i.z * m.getData(2, 2) + m.getData(3, 2);

			double w = i.x * m.getData(0, 3) + i.y * m.getData(1, 3) + i.z * m.getData(2, 3) + m.getData(3, 3);

			if (w > 0.0) {
				o.x /= w;
				o.y /= w;
				o.z /= w;
			}

			return o;
		}

	}

	private static Mesh generateCubeMesh() {

		Mesh result = new Mesh();

		result.add(new Triangle(0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0));
		result.add(new Triangle(0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 1.0, 0.0, 0.0));
		result.add(new Triangle(1.0, 0.0, 0.0, 1.0, 1.0, 0.0, 1.0, 1.0, 1.0));
		result.add(new Triangle(1.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 0.0, 1.0));
		result.add(new Triangle(1.0, 0.0, 1.0, 1.0, 1.0, 1.0, 0.0, 1.0, 1.0));
		result.add(new Triangle(1.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 0.0, 1.0));
		result.add(new Triangle(0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 1.0, 0.0));
		result.add(new Triangle(0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0));
		result.add(new Triangle(1.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0));
		result.add(new Triangle(1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0));

		return result;
	}
}
