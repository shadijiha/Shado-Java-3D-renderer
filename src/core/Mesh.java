/**
 *
 */

package core;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mesh {

	public List<Triangle> tris;

	public Mesh(Triangle[] array)	{
		tris = Arrays.asList(array);
	}

	public Mesh(List<Triangle> list)	{
		tris = new ArrayList<>(list);
	}

	public Mesh()	{
		tris = new ArrayList<>();
	}

	public Mesh(final Mesh other)	{
		tris = new ArrayList<>(other.tris);
	}

	public void add(Triangle tri)	{
		this.tris.add(tri);
	}

}
