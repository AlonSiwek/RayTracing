package edu.cg.scene.objects;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Hit;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;

public class Mesh extends Shape implements Iterable<Triangle> {
	public class Triplet {
		private int i1, i2, i3;
		
		public Triplet() {
			i1 = i2 = i3 = 0;
		}
		
		public Triplet(int i1, int i2, int i3) {
			this.i1 = i1;
			this.i2 = i2;
			this.i3 = i3;
		}
		
		public Triangle makeTriangle() {
			return new Triangle(vertices.get(i1), vertices.get(i2), vertices.get(i3));
		}
		
		@Override
		public String toString() {
			return makeTriangle().toString();
		}
	}
	
	private List<Point> vertices;
	private List<Triplet> indices;
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Mesh:" + endl + 
				"Vertices:" + endl + vertices + endl +
				"Triangles: " + indices + endl;
	}
	
	@Override
	public Iterator<Triangle> iterator() {
		return new Iterator<Triangle>() {
			private int currentTripletIndex = 0;
			
			@Override
			public boolean hasNext() {
				return currentTripletIndex < indices.size();
			}

			@Override
			public Triangle next() {
				if(!hasNext())
					throw new NoSuchElementException();
				
				return indices.get(currentTripletIndex++).makeTriangle();
			}
		};
	}

	@Override
	public Hit intersect(Ray ray) {
		edu.cg.algebra.Hit minHit = null;

		for (Triangle triangle : this) {
			edu.cg.algebra.Hit newHit = triangle.intersect(ray);
			if ((minHit == null) || ((newHit != null) && (newHit.compareTo(minHit) < 0))) {
				minHit = newHit;
			}
		}
		return minHit;
	}
}
