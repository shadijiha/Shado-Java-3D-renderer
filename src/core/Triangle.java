/**
 *
 */

package core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import renderer.Main;
import shadoMath.Vector;
import shadoMath.Vertex;

public class Triangle {

	public Vector[] vectors;

	private Color stroke_color = Color.WHITE;
	private Color fill_color = Color.WHITE;

	private boolean show_stroke = true;
	private boolean show_fill = true;

	private int z_buffer;

	// Constructors
	public Triangle(Vector v1, Vector v2, Vector v3) {
		vectors = new Vector[3];
		vectors[0] = v1;
		vectors[1] = v2;
		vectors[2] = v3;
	}

	public Triangle(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3) {
		this(new Vector(x1, y1, z1), new Vector(x2, y2, z2), new Vector(x3, y3, z3));
	}

	public Triangle() {
		vectors = new Vector[3];
	}

	public Triangle(final Triangle other) {
		this(new Vector(other.vectors[0]), new Vector(other.vectors[1]), new Vector(other.vectors[2]));
	}

	// Core
	public void draw(GraphicsContext g) {

		if (show_stroke) {
			g.setStroke(stroke_color);
			g.strokePolygon(new double[]{vectors[0].x, vectors[1].x, vectors[2].x},
					new double[]{vectors[0].y, vectors[1].y, vectors[2].y}, 3);
		}

		if (show_fill) {
			g.setFill(fill_color);
			g.fillPolygon(new double[]{vectors[0].x, vectors[1].x, vectors[2].x},
					new double[]{vectors[0].y, vectors[1].y, vectors[2].y}, 3);
		}

//		new Shado.Line(vectors[0].x, vectors[0].y, vectors[1].x, vectors[1].y).setFill(c).setStroke(c).draw(g);
//		new Shado.Line(vectors[1].x, vectors[1].y, vectors[2].x, vectors[2].y).setFill(c).setStroke(c).draw(g);
//		new Shado.Line(vectors[2].x, vectors[2].y, vectors[0].x, vectors[0].y).setFill(c).setStroke(c).draw(g);
	}

	public void shade() {

		Vertex v1 = Main.camera.toVertex();
		Vertex v2 = this.midPoint().toVertex();

		int xd = (int) v1.getDistance(v2);

		int mapped_value = map(xd,
				0, 1000,
				0, 255);

		mapped_value = Math.abs(mapped_value - 255);

		Color shadder = Color.rgb(mapped_value, mapped_value, mapped_value);
		this.setFill(shadder);
		this.setStroke(shadder);
	}

	private int map(int x, int in_min, int in_max, int out_min, int out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	// Setters
	public Triangle setStroke(Color c) {
		stroke_color = c;
		return this;
	}

	public Triangle setFill(Color c) {
		fill_color = c;
		return this;
	}

	public Triangle noStroke() {
		show_stroke = false;
		return this;
	}

	public Triangle noFill() {
		show_fill = false;
		return this;
	}

	public Triangle setZBuffer(int number) {
		z_buffer = number;
		return this;
	}

	// Getters
	public Vector getNormal() {

		Vector normal = new Vector();
		Vector line1 = new Vector();
		Vector line2 = new Vector();

		line1.x = vectors[1].x - vectors[0].x;
		line1.y = vectors[1].y - vectors[0].y;
		line1.z = vectors[1].z - vectors[0].z;

		line2.x = vectors[2].x - vectors[1].x;
		line2.y = vectors[2].y - vectors[1].y;
		line2.z = vectors[2].z - vectors[1].z;

		try {
			normal = line1.crossProduct(line2);
		} catch (Exception e) {
			e.printStackTrace();
		}

//		normal.x = line1.y * line2.z - line1.z * line2.y;
//		normal.y = line1.z * line2.x - line1.x * line2.z;
//		normal.z = line1.x * line2.y - line1.y * line2.x;

		return normal;
	}

	public int getZBuffer() {
		return z_buffer;
	}

	public double depth() {
		return (vectors[0].z + vectors[1].z + vectors[2].z) / 3.0;
	}

	private Vector midPoint() {
		return new Vector((vectors[0].x + vectors[1].x + vectors[2].x) / 3.0,
				(vectors[0].y + vectors[1].y + vectors[2].y) / 3.0,
				(vectors[0].z + vectors[1].z + vectors[2].z) / 3.0);
	}
}
