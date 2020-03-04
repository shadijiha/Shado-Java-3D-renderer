package renderer;

import core.Mesh;
import core.Triangle;
import core.coloring.CHAR_INFO;
import core.coloring.COLORS;
import core.coloring.PIXEL_TYPE;
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

import java.util.ArrayList;

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
	private final static Mesh cube = Mesh.loadFromObj("src/DataFiles/teapot.obj");

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

		ArrayList<Triangle> triangles_to_draw = new ArrayList<>();

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
				v.z += 8.0;
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
				// Add to the Triangle to draw list
				triangles_to_draw.add(triProjected);
			}
		}

		// Sort the triangles from back to front
		triangles_to_draw.sort((Triangle a, Triangle b) -> {

			double mid_point_1 = (a.vectors[0].z + a.vectors[1].z + a.vectors[2].z) / 3.0;
			double mid_point_2 = (b.vectors[0].z + b.vectors[1].z + b.vectors[2].z) / 3.0;

			return (int) (mid_point_1 - mid_point_2);
		});

		for (var tri : triangles_to_draw) {

			// Illumination
			Vector light_direction = new Vector(0.0, 0.0, -1.0);
			light_direction.normalize();


			// Draw the triangle with the appropiate color
			tri.draw(g);
		}
	}

	/**
	 * This function multiplies a 4x4 Matrix with a vector
	 *
	 * @param i The vector the multiply with
	 * @param m The matrix to multiply
	 * @return Returns a vector with the result of the multiplication
	 */
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

	/**
	 * This function generates a Cube using the mesh and triangle classes
	 *
	 * @return
	 */
	private static Mesh generateCubeMesh() {

		Mesh result = new Mesh();

		// SOUTH
		result.add(new Triangle(new Vector(0.0f, 0.0f, 0.0f), new Vector(0.0f, 1.0f, 0.0f), new Vector(1.0f, 1.0f, 0.0f)));
		result.add(new Triangle(new Vector(0.0f, 0.0f, 0.0f), new Vector(1.0f, 1.0f, 0.0f), new Vector(1.0f, 0.0f, 0.0f)));

		// EAST
		result.add(new Triangle(new Vector(1.0f, 0.0f, 0.0f), new Vector(1.0f, 1.0f, 0.0f), new Vector(1.0f, 1.0f, 1.0f)));
		result.add(new Triangle(new Vector(1.0f, 0.0f, 0.0f), new Vector(1.0f, 1.0f, 1.0f), new Vector(1.0f, 0.0f, 1.0f)));

		// NORTH
		result.add(new Triangle(new Vector(1.0f, 0.0f, 1.0f), new Vector(1.0f, 1.0f, 1.0f), new Vector(0.0f, 1.0f, 1.0f)));
		result.add(new Triangle(new Vector(1.0f, 0.0f, 1.0f), new Vector(0.0f, 1.0f, 1.0f), new Vector(0.0f, 0.0f, 1.0f)));

		// WEST
		result.add(new Triangle(new Vector(0.0f, 0.0f, 1.0f), new Vector(0.0f, 1.0f, 1.0f), new Vector(0.0f, 1.0f, 0.0f)));
		result.add(new Triangle(new Vector(0.0f, 0.0f, 1.0f), new Vector(0.0f, 1.0f, 0.0f), new Vector(0.0f, 0.0f, 0.0f)));

		// TOP
		result.add(new Triangle(new Vector(0.0f, 1.0f, 0.0f), new Vector(0.0f, 1.0f, 1.0f), new Vector(1.0f, 1.0f, 1.0f)));
		result.add(new Triangle(new Vector(0.0f, 1.0f, 0.0f), new Vector(1.0f, 1.0f, 1.0f), new Vector(1.0f, 1.0f, 0.0f)));

		// BOTTOM
		result.add(new Triangle(new Vector(1.0f, 0.0f, 1.0f), new Vector(0.0f, 0.0f, 1.0f), new Vector(0.0f, 0.0f, 0.0f)));
		result.add(new Triangle(new Vector(1.0f, 0.0f, 1.0f), new Vector(0.0f, 0.0f, 0.0f), new Vector(1.0f, 0.0f, 0.0f)));


		return result;
	}

	/**
	 * This function returns an appropriate color depending on
	 * how close the position to the camera
	 *
	 * @param lum
	 * @return
	 */
	private static CHAR_INFO getColor(double lum) {
		long bg_col, fg_col;
		char sym;
		int pixel_bw = (int) (13.0 * lum);
		switch (pixel_bw) {
			case 0:
				bg_col = COLORS.BG_BLACK.color();
				fg_col = COLORS.FG_BLACK.color();
				sym = PIXEL_TYPE.PIXEL_SOLID.type();
				break;

			case 1:
				bg_col = COLORS.BG_BLACK.color();
				fg_col = COLORS.FG_DARK_GREY.color();
				sym = PIXEL_TYPE.PIXEL_QUARTER.type();
				break;
			case 2:
				bg_col = COLORS.BG_BLACK.color();
				fg_col = COLORS.FG_DARK_GREY.color();
				sym = PIXEL_TYPE.PIXEL_HALF.type();
				break;
			case 3:
				bg_col = COLORS.BG_BLACK.color();
				fg_col = COLORS.FG_DARK_GREY.color();
				sym = PIXEL_TYPE.PIXEL_THREEQUARTERS.type();
				break;
			case 4:
				bg_col = COLORS.BG_BLACK.color();
				fg_col = COLORS.FG_DARK_GREY.color();
				sym = PIXEL_TYPE.PIXEL_SOLID.type();
				break;

			case 5:
				bg_col = COLORS.BG_DARK_GREY.color();
				fg_col = COLORS.FG_GREY.color();
				sym = PIXEL_TYPE.PIXEL_QUARTER.type();
				break;
			case 6:
				bg_col = COLORS.BG_DARK_GREY.color();
				fg_col = COLORS.FG_GREY.color();
				sym = PIXEL_TYPE.PIXEL_HALF.type();
				break;
			case 7:
				bg_col = COLORS.BG_DARK_GREY.color();
				fg_col = COLORS.FG_GREY.color();
				sym = PIXEL_TYPE.PIXEL_THREEQUARTERS.type();
				break;
			case 8:
				bg_col = COLORS.BG_DARK_GREY.color();
				fg_col = COLORS.FG_GREY.color();
				sym = PIXEL_TYPE.PIXEL_SOLID.type();
				break;

			case 9:
				bg_col = COLORS.BG_GREY.color();
				fg_col = COLORS.FG_WHITE.color();
				sym = PIXEL_TYPE.PIXEL_QUARTER.type();
				break;
			case 10:
				bg_col = COLORS.BG_GREY.color();
				fg_col = COLORS.FG_WHITE.color();
				sym = PIXEL_TYPE.PIXEL_HALF.type();
				break;
			case 11:
				bg_col = COLORS.BG_GREY.color();
				fg_col = COLORS.FG_WHITE.color();
				sym = PIXEL_TYPE.PIXEL_THREEQUARTERS.type();
				break;
			case 12:
				bg_col = COLORS.BG_GREY.color();
				fg_col = COLORS.FG_WHITE.color();
				sym = PIXEL_TYPE.PIXEL_SOLID.type();
				break;
			default:
				bg_col = COLORS.BG_BLACK.color();
				fg_col = COLORS.FG_BLACK.color();
				sym = PIXEL_TYPE.PIXEL_SOLID.type();
		}

		CHAR_INFO c = new CHAR_INFO();
		c.Attributes = (short) (bg_col | fg_col);
		c.UnicodeChar = sym;
		return c;
	}
}
