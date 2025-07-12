package com.example.usadijkstra;
import java.util.LinkedList;

public class Vertex implements Comparable<Vertex> {

	private City city;
	private LinkedList<Edge> adj;
	private boolean known;
	private Vertex prev;
	private double distance;

	public Vertex(City city) {
		this.city = city;
		this.adj = new LinkedList<>();
	}

	public void addNeighbour(Edge edge) {
		this.adj.add(edge);
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public LinkedList<Edge> getAdj() {
		return adj;
	}

	public void setAdj(LinkedList<Edge> adj) {
		this.adj = adj;
	}

	public boolean isKnown() {
		return known;
	}

	public void setKnown(boolean known) {
		this.known = known;
	}

	public Vertex getPrev() {
		return prev;
	}

	public void setPrev(Vertex prev) {
		this.prev = prev;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	@Override
	public String toString() {
		return "Vertex{" + city.getName() + ", dist=" + distance + ", known=" + known + '}';
	}

	@Override
	public int compareTo(Vertex otherVertex) {
		return Double.compare(this.distance, otherVertex.getDistance());
	}
}
