/*******************************
 * @Date: 04 Mars 2020 6:13 PM
 * @author: Shadi Jiha
 * *****************************
 */

package core.Shado3D.shapes;

import core.Shado3D.Material;
import core.Shado3D.Object3D;
import core.Shado3D.Triangle;
import shadoMath.Vector;

public class Cube extends Object3D {

	private float length;

	public Cube(float length) {
		super();
		this.length = length;
		this.material = Material.DEFAULT;

		generateCubeMesh();
	}

	public Cube() {
		this(1.0f);
	}

	/**
	 * This function generates a Cube using the mesh and triangle classes
	 */
	private void generateCubeMesh() {
		// SOUTH
		mesh.add(new Triangle(new Vector(0.0f, 0.0f, 0.0f), new Vector(0.0f, length, 0.0f), new Vector(length, length, 0.0f)));
		mesh.add(new Triangle(new Vector(0.0f, 0.0f, 0.0f), new Vector(length, length, 0.0f), new Vector(length, 0.0f, 0.0f)));

		// EAST
		mesh.add(new Triangle(new Vector(length, 0.0f, 0.0f), new Vector(length, length, 0.0f), new Vector(length, length, length)));
		mesh.add(new Triangle(new Vector(length, 0.0f, 0.0f), new Vector(length, length, length), new Vector(length, 0.0f, length)));

		// NORTH
		mesh.add(new Triangle(new Vector(length, 0.0f, length), new Vector(length, length, length), new Vector(0.0f, length, length)));
		mesh.add(new Triangle(new Vector(length, 0.0f, length), new Vector(0.0f, length, length), new Vector(0.0f, 0.0f, length)));

		// WEST
		mesh.add(new Triangle(new Vector(0.0f, 0.0f, length), new Vector(0.0f, length, length), new Vector(0.0f, length, 0.0f)));
		mesh.add(new Triangle(new Vector(0.0f, 0.0f, length), new Vector(0.0f, length, 0.0f), new Vector(0.0f, 0.0f, 0.0f)));

		// TOP
		mesh.add(new Triangle(new Vector(0.0f, length, 0.0f), new Vector(0.0f, length, length), new Vector(length, length, length)));
		mesh.add(new Triangle(new Vector(0.0f, length, 0.0f), new Vector(length, length, length), new Vector(length, length, 0.0f)));

		// BOTTOM
		mesh.add(new Triangle(new Vector(length, 0.0f, length), new Vector(0.0f, 0.0f, length), new Vector(0.0f, 0.0f, 0.0f)));
		mesh.add(new Triangle(new Vector(length, 0.0f, length), new Vector(0.0f, 0.0f, 0.0f), new Vector(length, 0.0f, 0.0f)));
	}
}
