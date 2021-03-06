package edu.cg.scene.objects;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Hit;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;

public class Dome extends Shape {
	private Sphere sphere;
	private Plain plain;
	
	public Dome() {
		sphere = new Sphere().initCenter(new Point(0, -0.5, -6));
		plain = new Plain(new Vec(-1, 0, -1), new Point(0, -0.5, -6));
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Dome:" + endl + 
				sphere + plain + endl;
	}
	
	@Override
	public Hit intersect(Ray ray)
	{
		Hit hit = sphere.intersect(ray);
		if (hit == null) {
			return null;
		}
		return hit.isWithinTheSurface() ? hittingFromInside(ray, hit) : hittingFromOutside(ray, hit);
	}

	private Hit hittingFromOutside(Ray ray, Hit hit) {
		Point hittingPoint = ray.getHittingPoint(hit);
		if (plain.substitute(hittingPoint) > 0.0D) {
			return hit;
		}
		hit = plain.intersect(ray);

		if (hit == null) {
			return null;
		}
		hittingPoint = ray.getHittingPoint(hit);

//		if (sphere.substitute(hittingPoint) < 0.0D) {
//			return hit;
//		}
		return null;
	}

	private Hit hittingFromInside(Ray ray, Hit hit)
	{
		Point hittingPoint = ray.getHittingPoint(hit);
		if (plain.substitute(ray.source()) > 1.0E-5D) {
			if (plain.substitute(hittingPoint) > 0.0D) {
				return hit;
			}
			hit = plain.intersect(ray);

			if (hit == null) {
				return null;
			}
			return hit.setWithin();
		}

		if (plain.substitute(hittingPoint) > 0.0D) {
			return plain.intersect(ray);
		}
		return null;
	}
}
