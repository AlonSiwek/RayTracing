package edu.cg.scene.objects;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Hit;
import edu.cg.algebra.Ops;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;

public class Sphere extends Shape {
	private Point center;
	private double radius;

	public Sphere(Point center, double radius) {
		this.center = center;
		this.radius = radius;
	}

	public Sphere() {
		this(new Point(0, -0.5, -6), 0.5);
	}

	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Sphere:" + endl + 
				"Center: " + center + endl +
				"Radius: " + radius + endl;
	}

	public Sphere initCenter(Point center) {
		this.center = center;
		return this;
	}

	public Sphere initRadius(double radius) {
		this.radius = radius;
		return this;
	}
	//CHANGE
	private Vec normal(Point p) {
		return p.sub(center).normalize();
	}

	//CHANGE
	@Override
	public Hit intersect(Ray ray) {
		double b = ray.direction().mult(2.0D).dot(ray.source().sub(center));
		double c = (ray.source()).distSqr(center) - Math.pow(radius, 2);

		double discriminant = Math.sqrt(b * b - 4.0D * c);
		if (Double.isNaN(discriminant)) {
			return null;
		}
		double t1 = (-b - discriminant) / 2.0D;
		double t2 = (-b + discriminant) / 2.0D;

		if (t2 < 1.0E-5D) {
			return null;
		}
		double minT = t1;
		Vec normal = normal(ray.add(t1));
		boolean isWithin = false;
		if (t1 < 1.0E-5D) {
			minT = t2;
			normal = normal(ray.add(t2)).neg();
			isWithin = true;
		}

		if (minT > 1.0E8D) {
			return null;
		}
		return new edu.cg.algebra.Hit(minT, normal).setIsWithin(isWithin);
	}

	private Point getNearestPoint(Ray ray, double [] vals) {
		double dis1 = Double.MAX_VALUE;
		double dis2 = Double.MAX_VALUE;
		Point p1 = null; 
		Point p2 = null;
		
		if (vals[0] > 0) {
			p1 = Ops.add(ray.source(), Ops.mult(vals[0], ray.direction())); 
			dis1 = Ops.dist(p1, ray.source());
		}
		if (vals[0] > 0) {
			p2 = Ops.add(ray.source(), Ops.mult(vals[1], ray.direction()));
			dis2 = Ops.dist(p2, ray.source());
		}
		if (dis1 < dis2) {
			return p1;
		}else {
			return p2;
		}
	}
}
