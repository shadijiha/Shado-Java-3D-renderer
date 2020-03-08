/*******************************
 * @Date: 04 Mars 2020 6:13 PM
 * @author: Shadi Jiha
 * *****************************
 */

package core.Shado3D;

import core.coloring.CHAR_INFO;
import core.coloring.COLORS;
import core.coloring.PIXEL_TYPE;
import shadoMath.Vector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Object3D {

	protected Mesh mesh;
	protected Texture texture;
	protected Material material;

	public Object3D(Mesh mesh, Texture texture, Material material) {
		this.mesh = mesh;
		this.texture = texture;
		this.material = material;
	}

	public Object3D(Mesh mesh) {
		this(mesh, null, Material.DEFAULT);
	}

	protected Object3D() {
		this(new Mesh());
	}

	public void render(Renderer renderer) {
		renderer.add(this);
	}

	/**
	 * This function loads a 3D object in *.obj file to a Engin Mesh object
	 *
	 * @param filepath The location of the file
	 */
	public static Object3D loadOBJ(String filepath) {

		Mesh tempmesh = new Mesh();

		try {
			File file = new File(filepath);
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

					// If it has the normal vector data: e.g. "f v1//vn1 v2//vn2 v3//vn3"
					if (line.contains("//")) {
						// split all elements by "//"
						int i = 0;
						for (String e : elements) {
							String[] temp_data = e.split("//");

							if (temp_data[0].equals("f"))
								continue;

							indeces[i] = Integer.parseInt(temp_data[0]);
							i++;
						}
					} else {
						// Match the faces with the appropriate vertex ID
						indeces[0] = Integer.parseInt(elements[1]);
						indeces[1] = Integer.parseInt(elements[2]);
						indeces[2] = Integer.parseInt(elements[3]);
					}

					// Add it to the mesh
					tempmesh.add(new Triangle(verts.get(indeces[0] - 1), verts.get(indeces[1] - 1), verts.get(indeces[2] - 1)));
				}
			}

			return new Object3D(tempmesh);
		} catch (FileNotFoundException e) {
			System.out.println(filepath + " not found");
			return null;
		}
	}

	/**
	 * This function returns an appropriate color depending on
	 * how close the position to the camera
	 *
	 * @param lum
	 * @return
	 */
	public static CHAR_INFO getColor(double lum) {
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

	// Setters
	public Object3D setMaterial(Material m) {
		this.material = m;
		return this;
	}

	public Object3D setTexture(Texture t) {
		this.texture = t;
		return this;
	}
}
