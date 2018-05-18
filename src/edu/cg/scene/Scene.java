package edu.cg.scene;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.cg.Logger;
import edu.cg.algebra.*;
import edu.cg.scene.lightSources.Light;
import edu.cg.scene.objects.Surface;

public class Scene {
	private String name = "scene";
	private int maxRecursionLevel = 1;
	private int antiAliasingFactor = 1; //gets the values of 1, 2 and 3
	private boolean renderRefarctions = false;
	private boolean renderReflections = false;

	private Point camera = new Point(0, 0, 5);
	private Vec ambient = new Vec(1, 1, 1); //white
	private Vec backgroundColor = new Vec(0, 0.5, 1); //blue sky
	private List<Light> lightSources = new LinkedList<>();
	private List<Surface> surfaces = new LinkedList<>();


	//MARK: initializers
	public Scene initCamera(Point camera) {
		this.camera = camera;
		return this;
	}

	public Scene initAmbient(Vec ambient) {
		this.ambient = ambient;
		return this;
	}

	public Scene initBackgroundColor(Vec backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	public Scene addLightSource(Light lightSource) {
		lightSources.add(lightSource);
		return this;
	}

	public Scene addSurface(Surface surface) {
		surfaces.add(surface);
		return this;
	}

	public Scene initMaxRecursionLevel(int maxRecursionLevel) {
		this.maxRecursionLevel = maxRecursionLevel;
		return this;
	}

	public Scene initAntiAliasingFactor(int antiAliasingFactor) {
		this.antiAliasingFactor = antiAliasingFactor;
		return this;
	}

	public Scene initName(String name) {
		this.name = name;
		return this;
	}

	public Scene initRenderRefarctions(boolean renderRefarctions) {
		this.renderRefarctions = renderRefarctions;
		return this;
	}

	public Scene initRenderReflections(boolean renderReflections) {
		this.renderReflections = renderReflections;
		return this;
	}

	//MARK: getters
	public String getName() {
		return name;
	}

	public int getFactor() {
		return antiAliasingFactor;
	}

	public int getMaxRecursionLevel() {
		return maxRecursionLevel;
	}

	public boolean getRenderRefarctions() {
		return renderRefarctions;
	}

	public boolean getRenderReflections() {
		return renderReflections;
	}

	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Camera: " + camera + endl +
				"Ambient: " + ambient + endl +
				"Background Color: " + backgroundColor + endl +
				"Max recursion level: " + maxRecursionLevel + endl +
				"Anti aliasing factor: " + antiAliasingFactor + endl +
				"Light sources:" + endl + lightSources + endl +
				"Surfaces:" + endl + surfaces;
	}

	private static class IndexTransformer {
		private final int max;
		private final int deltaX;
		private final int deltaY;

		IndexTransformer(int width, int height) {
			max = Math.max(width, height);
			deltaX = (max - width) / 2;
			deltaY = (max - height) / 2;
		}

		Point transform(int x, int y) {
			double xPos = (2*(x + deltaX) - max) / ((double)max);
			double yPos = (max - 2*(y + deltaY)) / ((double)max);
			return new Point(xPos, yPos, 0);
		}
	}

	private transient IndexTransformer transformaer = null;
	private transient ExecutorService executor = null;
	private transient Logger logger = null;

	private void initSomeFields(int imgWidth, int imgHeight, Logger logger) {
		this.logger = logger;
		//TODO: initialize your additional field here.
	}


	public BufferedImage render(int imgWidth, int imgHeight, Logger logger)
			throws InterruptedException, ExecutionException {

		initSomeFields(imgWidth, imgHeight, logger);

		BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
		transformaer = new IndexTransformer(imgWidth, imgHeight);
		int nThreads = Runtime.getRuntime().availableProcessors();
		nThreads = nThreads < 2 ? 2 : nThreads;
		this.logger.log("Intitialize executor. Using " + nThreads + " threads to render " + name);
		executor = Executors.newFixedThreadPool(nThreads);

		@SuppressWarnings("unchecked")
		Future<Color>[][] futures = (Future<Color>[][])(new Future[imgHeight][imgWidth]);

		this.logger.log("Starting to shoot " +
				(imgHeight*imgWidth*antiAliasingFactor*antiAliasingFactor) +
				" rays over " + name);

		for(int y = 0; y < imgHeight; ++y)
			for(int x = 0; x < imgWidth; ++x)
				futures[y][x] = calcColor(x, y);

		this.logger.log("Done shooting rays.");
		this.logger.log("Wating for results...");

		for(int y = 0; y < imgHeight; ++y)
			for(int x = 0; x < imgWidth; ++x) {
				Color color = futures[y][x].get();
				img.setRGB(x, y, color.getRGB());
			}

		executor.shutdown();

		this.logger.log("Ray tracing of " + name + " has been completed.");

		executor = null;
		transformaer = null;
		this.logger = null;

		return img;
	}

	private Future<Color> calcColor(int x, int y) {
		return executor.submit(() -> {
			//TODO: change this method implementation to implement super sampling
			Point pointOnScreenPlain = transformaer.transform(x, y);
			Ray ray = new Ray(camera, pointOnScreenPlain);
			return calcColor(ray, 0).toColor();
		});
	}

	private Vec calcColor(Ray r, int recusionLevel) {
		Hit h = this.intersection(r);
		if (h == null) return this.backgroundColor;
		Point hittingPoint = r.getHittingPoint(h);
		Surface surface = h.getSurface();
		Vec color = surface.Ka().mult(this.ambient);
		for (Light l : this.lightSources) {
			Ray lightRay;
			if (this.isBlocked(l, lightRay = l.createLightRay(hittingPoint))) continue;
			Vec tmpColor = this.diffuse(h, lightRay);
			tmpColor = tmpColor.add(this.specular(h, lightRay, r));
			Vec intensity = l.intensity(hittingPoint, lightRay);
			color = color.add(tmpColor.mult(intensity));
		}
		return color;
	}

	private boolean isBlocked(Light l, Ray r) {
		return this.surfaces.stream().anyMatch(surface -> l.isBlocked(surface, r));
	}

	private Hit intersection(Ray r) {
		Hit minHit = null;
		for (Surface surface : this.surfaces) {
			Hit newHit = surface.intersect(r);
			if (minHit == null || (newHit != null) && (newHit.compareTo(minHit) < 0)) {
				minHit = newHit;
			}
		}
		return minHit;
	}

	private Vec diffuse(Hit h, Ray r) {
		Vec lightDirection = r.direction();
		Vec normalToSurface = h.getNormalToSurface();
		Vec Kd = h.getSurface().Kd(r.source());
		return Kd.mult(Math.max(normalToSurface.dot(lightDirection), 0.0));
	}

	private Vec specular(Hit h, Ray lightRay, Ray cameraRay) {
		Vec lightDirection = lightRay.direction();
		Vec normal = h.getNormalToSurface();
		Vec reflect = Ops.reflect(lightDirection.neg(),normal);
		Vec cameraDirection = cameraRay.direction();
		int shine = h.getSurface().shininess();
		double dot = reflect.dot(cameraDirection.neg());
		return dot < 0.0 ?
				new Vec() :
				h.getSurface().Ks().mult(Math.pow(dot, shine));
	}

}
