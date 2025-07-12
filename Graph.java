package com.example.usadijkstra;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Graph {

	public static LinkedList<Vertex> cityList = new LinkedList<>();
	public static HashMap<String, Vertex> cityMap = new HashMap<>();

	public static double originalXMin = -300;
	public static double originalXMax = 10000;
	public static double originalYMin = -200;
	public static double originalYMax = 4500;
	public static int canvasWidth = 1600;
	public static int canvasHeight = 393;

	public void dijkstra(Vertex source, Vertex target) {
		for (Vertex vertex : cityList) {
     		vertex.setDistance(Double.MAX_VALUE);
			vertex.setKnown(false);
			vertex.setPrev(null);
		}

		source.setDistance(0);

		MinHeap pq = new MinHeap();
		pq.add(source);

		while (!pq.isEmpty()) {
			Vertex current = pq.poll();

			if (current.isKnown()) continue;
			current.setKnown(true);

			if (current == target) break;

			for (Edge edge : current.getAdj()) {
				Vertex neighbor = edge.getTargetVertex();

				if (!neighbor.isKnown()) {
					double newDistance = current.getDistance() + edge.getWeight();
					if (newDistance < neighbor.getDistance()) {
						neighbor.setDistance(newDistance);
						neighbor.setPrev(current);
						pq.add(neighbor);
					}
				}
			}
		}
	}
	public void addVertex(Vertex v) {
		cityList.add(v);
		setInhales();
	}

	public void addEdge(Vertex from, Vertex to, double w) {
		from.getAdj().add(new Edge(from,to, w));
		setInhales();
	}

	public static void setInhales(){
		for (Vertex vertex : cityList) {
			vertex.setDistance(Double.MAX_VALUE);
			vertex.setKnown(false);
			vertex.setPrev(null);
		}



	}




	// Clear existing data to avoid duplicates on reload
	public static void clear() {
		cityList.clear();
		cityMap.clear();
	}

	public static LinkedList<Vertex> loadCitiesFromFile() {
		clear();  // Clear old data before loading new

		try {
			// Make sure your file path includes the extension, e.g. "Test.txt"
			File file = new File("C:\\Users\\97059\\OneDrive\\Desktop\\Java\\USA-Dijkstra\\src\\USA.txt");
			BufferedReader reader = new BufferedReader(new FileReader(file));

			// First line: number of cities and edges
			String line = reader.readLine();
			if (line == null) {
				reader.close();
				return cityList;
			}

			String[] firstLine = line.trim().split("\\s+");
			int cityCount = Integer.parseInt(firstLine[0]);
			int edgeCount = Integer.parseInt(firstLine[1]);

			// Read cities
			for (int i = 0; i < cityCount; i++) {
				line = reader.readLine();
				if (line == null) break;

				String[] tokens = line.trim().split("\\s+");
				if (tokens.length < 3) continue;

				String name = tokens[0];
				double x = Double.parseDouble(tokens[1]);
				double y = Double.parseDouble(tokens[2]);

				Vertex vertex = new Vertex(new City(name, x, y));
				cityList.add(vertex);
				cityMap.put(name, vertex);
			}

			// Read edges
			for (int i = 0; i < edgeCount; i++) {
				line = reader.readLine();
				if (line == null) break;

				String[] tokens = line.trim().split("\\s+");
				if (tokens.length < 2) continue;

				Vertex from = cityMap.get(tokens[0]);
				Vertex to = cityMap.get(tokens[1]);

				if (from == null || to == null) continue;

				double distance = calculateDistance(from, to);
				Edge edge = new Edge(from, to, distance);
				from.addNeighbour(edge);
			}

			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("❌ File not found: " + e.getMessage());
		} catch (NumberFormatException e) {
			System.out.println("❌ Invalid number format: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("❌ Error reading the file: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("❌ Unexpected error: " + e.getMessage());
		}

		return cityList;
	}

	public static double calculateDistance(Vertex a, Vertex b) {
		double dx = a.getCity().getX() - b.getCity().getX();
		double dy = a.getCity().getY() - b.getCity().getY();
		return Math.sqrt(dx * dx + dy * dy);
	}

	public static void drawCityPoints(Group group) {
		// Draw red circles for each city at its Mercator coordinates on the map
		for (Vertex vertex : cityList) {
			double x = vertex.getCity().getMercatorX();
			double y = vertex.getCity().getMercatorY();

			Circle circle = new Circle(x, y, 3, Color.BLUE);
			group.getChildren().add(circle);
		}
	}

	public LinkedList<String> getShortestPath(Vertex source, Vertex target) {
		LinkedList<String> path = new LinkedList<>();
		if (source.getCity().getName().equals(target.getCity().getName())) {
			path.add(source.getCity().getName());
		} else if (target.getPrev() == null) {
			path.add("No Path");
		} else {
			for (Vertex vertex = target; vertex != null; vertex = vertex.getPrev()) {
				path.add(vertex.getCity().getName());
			}
			Collections.reverse(path);
		}
		return path;
	}

}
