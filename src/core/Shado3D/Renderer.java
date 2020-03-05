/*******************************
 * @Date: 04 Mars 2020 6:13 PM
 * @author: Shadi Jiha
 * *****************************
 */

package core.Shado3D;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import shadoMath.Matrix;
import shadoMath.Vector;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Renderer {

	private List<Object3D> render_buffer = new ArrayList<>();

	private double rotation_X;
	private double rotation_Y;
	private double rotation_Z;

	private Canvas canvas;
	private GraphicsContext g;
	private Color background;


	protected List<Camera> cameras;
	private Vector camera; // Todo: Create a Camera class
	private Matrix projectionMatrix;
	private Matrix rotationMatrix;

	private double furthest_point;
	private double translation;

	public Renderer(Canvas c) {
		this.canvas = c;
		this.g = c.getGraphicsContext2D();

		rotation_X = 0.0;
		rotation_Y = 0.0;
		rotation_Z = 0.0;

		cameras = new ArrayList<>();
		camera = new Vector(0, 0, 0);
		projectionMatrix = new Matrix(4, 4);
		rotationMatrix = new Matrix(3, 3);
		background = Color.WHITE;

		furthest_point = 1000.0;
		translation = 8.0;

		this.initialize();
	}

	private void initialize() {
		// Init projection matrix
		double fNear = 0.1;
		double fFar = 1000.0;
		double fAspectRatio = canvas.getHeight() / canvas.getWidth();
		double fFovRad = 1.0 / Math.tan(furthest_point * 0.5 / 180.0 * Math.PI);

		projectionMatrix.setData(0, 0, fAspectRatio * fFovRad);
		projectionMatrix.setData(1, 1, fFovRad);
		projectionMatrix.setData(2, 2, fFar / (fFar - fNear));
		projectionMatrix.setData(3, 2, (-fFar * fNear) / (fFar - fNear));
		projectionMatrix.setData(2, 3, 1.0);
		projectionMatrix.setData(3, 3, 0.0);
	}

	private void updateRotationMatrix() {

		double a = rotation_Z;
		double b = rotation_Y;
		double y = rotation_X;

		rotationMatrix.setData(0, 0, cos(a) * cos(b));
		rotationMatrix.setData(0, 1, cos(a) * sin(b) * sin(y) - sin(a) * cos(y));
		rotationMatrix.setData(0, 2, cos(a) * sin(b) * cos(y) + sin(a) * sin(y));

		rotationMatrix.setData(1, 0, sin(a) * cos(b));
		rotationMatrix.setData(1, 1, sin(a) * sin(b) * sin(y) - cos(a) * cos(y));
		rotationMatrix.setData(1, 2, sin(a) * sin(b) * cos(y) - cos(a) * sin(y));

		rotationMatrix.setData(2, 0, -1 * sin(b));
		rotationMatrix.setData(2, 1, cos(b) * sin(y));
		rotationMatrix.setData(2, 2, cos(b) * cos(y));
	}

	public void render() {

		long start_time = System.nanoTime();

		// Draw background
		g.setFill(Color.BLACK);
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		Matrix rotationZ = Matrix.rotationZ(rotation_Z);
		//Matrix rotationY = Matrix.rotationY(rotation_Y);
		Matrix rotationX = Matrix.rotationX(rotation_X);

		updateRotationMatrix();

		for (Object3D obj : render_buffer) {

			ArrayList<Triangle> triangles_to_draw = new ArrayList<>();

			// Draw triangles
			for (var tri : obj.mesh.tris) {

				Triangle triProjected = new Triangle();
				Triangle triRotatedZ = new Triangle();
				Triangle triRotatedZX = new Triangle();
				//Triangle triRotated = new Triangle();

				// Rotate in Z
				for (int i = 0; i < tri.vectors.length; i++) {
					triRotatedZ.vectors[i] = multiplyMatrixVector(tri.vectors[i], rotationZ);
				}

				// Rotate in X
				for (int i = 0; i < triRotatedZX.vectors.length; i++) {
					triRotatedZX.vectors[i] = multiplyMatrixVector(triRotatedZ.vectors[i], rotationX);
				}

				// Translate the triangle
				Triangle triTranslated = new Triangle(triRotatedZX);
				for (Vector v : triTranslated.vectors) {
					v.z += translation;
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

						v.x *= 0.5 * canvas.getWidth();
						v.y *= 0.5 * canvas.getHeight();
					}
					// Add to the Triangle to draw list
					triProjected.setZBuffer(triTranslated.depth());
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

				// TODO: Illumination

				// Draw the triangle with the appropiate color
				tri.shade(translation);
				tri.draw(g);
			}
		}

		// Clear the buffer
		render_buffer.clear();
	}

	public void clear() {
		g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	public void add(Object3D obj) {
		render_buffer.add(obj);
	}

	// Setters and modifiers
	public Renderer rotateX(double offset) {
		rotation_X += offset;
		return this;
	}

	public Renderer rotateY(double offset) {
		rotation_Y += offset;
		return this;
	}

	public Renderer rotateZ(double offset) {
		rotation_Z += offset;
		return this;
	}

	public Renderer setBackground(Color c) {
		background = c;
		return this;
	}

	// Getters
	private Camera defaultCamera() {
		return cameras.stream().filter(Camera::isDefault).findFirst().orElse(null);
	}

	// Private stuff

	/**
	 * This function multiplies a 4x4 Matrix with a vector
	 *
	 * @param i The vector the multiply with
	 * @param m The matrix to multiply
	 * @return Returns a vector with the result of the multiplication
	 */
	private static Vector multiplyMatrixVector(Vector i, Matrix m) {

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
}
