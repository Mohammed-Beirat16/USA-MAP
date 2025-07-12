package com.example.usadijkstra;

public class Edge {

	private double dist;
	private Vertex startVertex;
	private Vertex targetVertex;

	public Edge(Vertex startVertex, Vertex targetVertex, double dist) {
		this.dist = dist;
		this.startVertex = startVertex;
		this.targetVertex = targetVertex;
	}

	public double getWeight() {
		return dist;
	}

	public void setWeight(double weight) {
		this.dist = weight;
	}

	public Vertex getStartVertex() {
		return startVertex;
	}

	public void setStartVertex(Vertex startVertex) {
		this.startVertex = startVertex;
	}

	public Vertex getTargetVertex() {
		return targetVertex;
	}

	public void setTargetVertex(Vertex targetVertex) {
		this.targetVertex = targetVertex;
	}

	@Override
	public String toString() {
		return "Edge [dist=" + dist + ", startVertex=" + startVertex + ", targetVertex=" + targetVertex + "]";
	}
}
