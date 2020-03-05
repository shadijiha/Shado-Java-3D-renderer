/**
 *
 */

package core.Shado3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mesh {

	protected List<Triangle> tris;

	public Mesh(Triangle[] array) {
		tris = Arrays.asList(array);
	}

	public Mesh(List<Triangle> list) {
		tris = new ArrayList<>(list);
	}

	public Mesh() {
		tris = new ArrayList<>();
	}

	public Mesh(final Mesh other) {
		tris = new ArrayList<>(other.tris);
	}

	/**
	 * Adds a triangle to the face collection of the Mesh
	 * @param tri
	 */
	public void add(Triangle tri) {
		this.tris.add(tri);
	}
}
