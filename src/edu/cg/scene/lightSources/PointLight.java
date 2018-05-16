package edu.cg.scene.lightSources;

import edu.cg.algebra.Hit;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;
import edu.cg.scene.objects.Surface;

public class PointLight extends Light {
	protected Point position;

	//Decay factors:
	protected double kq = 0.01;
	protected double kl = 0.1;
	protected double kc = 1;

	protected String description() {
		String endl = System.lineSeparator();
		return "Intensity: " + intensity + endl +
				"Position: " + position + endl +
				"Decay factors: kq = " + kq + ", kl = " + kl + ", kc = " + kc + endl;
	}

	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Point Light:" + endl + description();
	}

	@Override
	public PointLight initIntensity(Vec intensity) {
		return (PointLight)super.initIntensity(intensity);
	}

	public PointLight initPosition(Point position) {
		this.position = position;
		return this;
	}

	public PointLight initDecayFactors(double kq, double kl, double kc) {
		this.kq = kq;
		this.kl = kl;
		this.kc = kc;
		return this;
	}

	public Ray createLightRay(Point p) {
		return new Ray(p, this.position);
	}

	public boolean isBlocked(Surface s, Ray r) {
		Hit hit = s.intersect(r);
		if (hit == null) {
			return false;
		}
		Point source = r.source();
		Point hittingPoint = r.getHittingPoint(hit);
		return source.distSqr(this.position) > source.distSqr(hittingPoint);
	}

	public Vec intensity(Point p, Ray r) {
		double dist = p.dist(this.position);
		double decay = this.kc + (this.kl + this.kq * dist) * dist;
		return this.intensity.mult(1.0 / decay);
	}
}
