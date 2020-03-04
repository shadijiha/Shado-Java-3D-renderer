/**
 *
 */

package core;

import shadoMath.Vector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Mesh {

	public List<Triangle> tris;

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

	/**
	 * This function loads a 3D object in *.obj file to a Engin Mesh object
	 * @param path The location of the file
	 * @return Returns the the mesh object to be rendered
	 */
	public static Mesh loadFromObj(String path) {

		Mesh mesh = new Mesh();

		try {
			File file = new File(path);
			Scanner scan = new Scanner(file);

			List<Vector> verts = new ArrayList<>();

			// Read the file and extract vertex and face data
			while (scan.hasNextLine()) {

				String line = scan.nextLine();
				String[] elements = line.split(" ");
				char junk;

				// if line is empty
				if (line.length() <= 0)
					continue;

				// If the line starts with a 'v' then it is a vertex data
				if (line.charAt(0) == 'v') {

					// Parse the vertex data
					Vector v = new Vector();

					junk = line.charAt(0);
					v.x = Double.parseDouble(elements[1]);
					v.y = Double.parseDouble(elements[2]);
					v.z = Double.parseDouble(elements[3]);

					verts.add(v);

				} else if (line.charAt(0) == 'f') {

					junk = line.charAt(0);
					int[] indeces = new int[3];

					// Match the faces with the appropriate vertex ID
					indeces[0] = Integer.parseInt(elements[1]);
					indeces[1] = Integer.parseInt(elements[2]);
					indeces[2] = Integer.parseInt(elements[3]);

					// Add it to the mesh
					mesh.add(new Triangle(verts.get(indeces[0] - 1), verts.get(indeces[1] - 1), verts.get(indeces[2] - 1)));
				}
			}
			return mesh;
		} catch (FileNotFoundException e) {
			System.out.println(path + " not found");
			return mesh;
		}
	}
}
