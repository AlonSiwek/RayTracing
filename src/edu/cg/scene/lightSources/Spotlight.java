package edu.cg.scene.lightSources;

import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;
import edu.cg.scene.objects.Surface;

public class Spotlight extends PointLight {
	private Vec direction;
	private double angle = 0.866; //cosine value ~ 30 degrees

	public Spotlight initDirection(Vec direction) {
		this.direction = direction;
		return this;
	}

	public Spotlight initAngle(double angle) {
		this.angle = angle;
		return this;
	}

	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Spotlight: " + endl +
				description() +
				"Direction: " + direction + endl +
				"Angle: " + angle + endl;
	}

	@Override
	public Spotlight initPosition(Point position) {
		return (Spotlight)super.initPosition(position);
	}

	@Override
	public Spotlight initIntensity(Vec intensity) {
		return (Spotlight)super.initIntensity(intensity);
	}

	@Override
	public Spotlight initDecayFactors(double q, double l, double c) {
		return (Spotlight)super.initDecayFactors(q, l, c);
	}

	@Override
	public boolean isBlocked(Surface s, Ray r) {
		if (r.direction().neg().dot(this.direction.normalize()) < this.angle) {
			return true;
		}
		return super.isBlocked(s, r);
	}


	public Vec intensity(Point p, Ray r) {
		Vec v = this.direction.normalize().neg();
		return super.intensity(p, r).mult(v.dot(r.direction()));
	}
}
