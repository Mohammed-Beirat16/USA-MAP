package com.example.usadijkstra;

public class City {
	private String name;
	private double x; // original x-coordinate (e.g., longitude)
	private double y; // original y-coordinate (e.g., latitude)

	public City(String name, double x, double y) {
		this.name = name;
		this.x = x;
		this.y = y;
	}

	// Getters and setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	// Scales x-coordinate to fit screen dimensions
	public double getMercatorX() {
		return (x - Graph.originalXMin) / (Graph.originalXMax - Graph.originalXMin) * Graph.canvasWidth;
	}

	// Scales y-coordinate to fit screen dimensions (inverted for screen coordinates)
	public double getMercatorY() {
		return Graph.canvasHeight - ((y - Graph.originalYMin) / (Graph.originalYMax - Graph.originalYMin) * Graph.canvasHeight);
	}

	@Override
	public String toString() {
		return "City{" + "name='" + name + '\'' + ", x=" + x + ", y=" + y + '}';
	}
}
