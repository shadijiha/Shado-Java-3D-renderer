/*******************************
 * @Date: 04 Mars 2020 6:13 PM
 * @author: Shadi Jiha
 * *****************************
 */

package core.Shado3D;

import javafx.scene.paint.Color;

public class Material {

	public static Material DEFAULT = new Material(Color.WHITE, Color.BLACK);

	private Color fill;
	private Color stroke;

	private Material(Color fill, Color stroke) {
		this.fill = fill;
		this.stroke = stroke;
	}

	public Material(final Material other) {
		this(other.fill, other.stroke);
	}

	// Setters
	public Material setFill(Color c) {
		fill = c;
		return this;
	}

	public Material setStroke(Color c) {
		stroke = c;
		return this;
	}

	public Material noStroke() {
		stroke = Color.rgb(0, 0, 0, 0);
		return this;
	}

	public Material noFill() {
		fill = Color.rgb(0, 0, 0, 0);
		return this;
	}

	// Getters
	public Color getFill() {
		return fill;
	}

	public Color getStroke() {
		return stroke;
	}
}
