package edu.cg.scene.lightSources;

import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;
import edu.cg.scene.objects.Surface;

public class DirectionalLight extends Light {
	private Vec direction = new Vec(0, -1, -1);

	public DirectionalLight initDirection(Vec direction) {
		this.direction = direction;
		return this;
	}

	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Directional Light:" + endl + super.toString() +
				"Direction: " + direction + endl;
	}



	@Override
	public DirectionalLight initIntensity(Vec intensity) {
		return (DirectionalLight)super.initIntensity(intensity);
	}

	public Ray createLightRay(Point p) {
		return new Ray(p, this.direction.neg());
	}

	public boolean isBlocked(Surface s ,Ray r) {
		return s.intersect(r) != null;
	}

	public Vec intensity(Point p,Ray r) {
		Vec v = this.direction.normalize().neg();
		return this.intensity.mult(v.dot(r.direction()));
	}
}
