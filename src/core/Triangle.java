/**
 *
 */

package core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import shadoMath.Vector;
import shapes.Shado;

public class Triangle {

	public Vector[] vectors;

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

	public void draw(GraphicsContext g, Color c) {

		new Shado.Line(vectors[0].x, vectors[0].y, vectors[1].x, vectors[1].y).setFill(c).setStroke(c).draw(g);
		new Shado.Line(vectors[1].x, vectors[1].y, vectors[2].x, vectors[2].y).setFill(c).setStroke(c).draw(g);
		new Shado.Line(vectors[2].x, vectors[2].y, vectors[0].x, vectors[0].y).setFill(c).setStroke(c).draw(g);

	}

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
}
