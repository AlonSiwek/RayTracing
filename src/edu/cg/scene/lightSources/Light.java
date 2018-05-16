package edu.cg.scene.lightSources;

import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;
import edu.cg.scene.objects.Surface;

public abstract class Light {
	protected Vec intensity = new Vec(1, 1, 1); //white color

	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Intensity: " + intensity + endl;
	}

	public Light initIntensity(Vec intensity) {
		this.intensity = intensity;
		return this;
	}

	public abstract Ray createLightRay(Point p);

	public abstract boolean isBlocked(Surface s, Ray r);

	public abstract Vec intensity(Point p, Ray r);
}
