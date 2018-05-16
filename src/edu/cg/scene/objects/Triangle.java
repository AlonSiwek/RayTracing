package edu.cg.scene.objects;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Hit;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;

import java.util.LinkedList;
import java.util.function.BiConsumer;

public class Triangle extends Shape {
	private Point p1, p2, p3;
	
	public Triangle() {
		p1 = p2 = p3 = null;
	}
	
	public Triangle(Point p1, Point p2, Point p3) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Triangle:" + endl +
				"p1: " + p1 + endl + 
				"p2: " + p2 + endl +
				"p3: " + p3 + endl;
	}

	private transient Plain plain = null;
	private synchronized Plain plain() {
		if (this.plain == null) {
			Vec v2 = this.p2.sub(this.p1);
			Vec v3 = this.p3.sub(this.p1);
			Vec nomal = v2.cross(v3).normalize();
			this.plain = new Plain(nomal, this.p1);
		}
		return this.plain;
	}

	private Vec normal() {
		return this.plain().normal();
	}

	private void forEach(BiConsumer<Point, Point> foo) {
		foo.accept(this.p1, this.p2);
		foo.accept(this.p2, this.p3);
		foo.accept(this.p3, this.p1);
	}

	private boolean isInside(Ray ray, Hit hit) {
		LinkedList <Triangle> triangles = new LinkedList();
		this.forEach((pi, pj) -> {
			Triangle newTriangle = new Triangle(ray.source(), pi, pj);
			boolean bl = triangles.add(newTriangle);
		});
		Point hittingPoint = ray.getHittingPoint(hit);
		Ray rayToHittingPoint = new Ray(ray.source(), hittingPoint);
		boolean[] signs = new boolean[3];
		int i = 0;

		for (Triangle triangle : triangles) {
			boolean bl = signs[i++] = triangle.normal().dot(rayToHittingPoint.direction()) > 0.0;
		}
		boolean allTrue = true;
		boolean allFalse = true;
		boolean[] arrbl = signs;
		int n = arrbl.length;
		int n2 = 0;
		while (n2 < n) {
			boolean sign = arrbl[n2];
			allTrue &= sign;
			allFalse &= !sign;
			++n2;
		}
		return allTrue | allFalse;
	}

	public Hit intersect(Ray ray) {
		Hit hit = this.plain().intersect(ray);
		if (hit != null && this.isInside(ray, hit)) {
			return hit;
		}
		return null;
	}
}
