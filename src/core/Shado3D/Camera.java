/*******************************
 * @Date: 04 Mars 2020 6:13 PM
 * @author: Shadi Jiha
 * *****************************
 */

package core.Shado3D;

import shadoMath.Vector;
import shadoMath.Vertex;

public class Camera {

	private Vertex position;
	private Vector direction;
	private Renderer renderer;

	private boolean isDefault;

	public Camera(Renderer renderer, Vertex pos, Vector direction) {
		this.renderer = renderer;
		this.position = new Vertex(pos);
		this.direction = new Vector(direction);
		this.isDefault = false;
	}

	public Camera(Renderer renderer) {
		this(renderer, new Vertex(0, 0, 0), new Vector(0, 0, 1));
	}

	// Setter
	public void lookAt(Vector dir) {
		// TODO:: implement
	}

	public void setPosition(Vertex p) {
		position = p;
	}

	public void setAsDefault() {
		renderer.cameras.parallelStream().forEach(c -> c.isDefault = false);
		this.isDefault = true;
	}

	// Getter
	public boolean isDefault() {
		return isDefault;
	}

}
