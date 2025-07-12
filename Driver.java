package com.example.usadijkstra;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.Locale;

public class Driver extends Application {

	private Vertex selectedSource = null;
	private Vertex selectedTarget = null;

	private ComboBox<String> comboSource;
	private ComboBox<String> comboTarget;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		Locale.setDefault(Locale.ENGLISH);

		Image image = new Image(getClass().getResource("/MapPhoto.png").toExternalForm());
		ImageView imageView = new ImageView(image);
		imageView.setFitWidth(1000);
		imageView.setFitHeight(1000);

		Button startButton = new Button("Start");
		startButton.setMinWidth(100);
		startButton.setMinHeight(100);
		startButton.setStyle("-fx-background-radius: 50; -fx-background-color: #4285F4; -fx-text-fill: white; -fx-font-size: 16px;");
		startButton.setOnAction(e -> openGraphStage(primaryStage));

		HBox buttonContainer = new HBox(startButton);
		buttonContainer.setAlignment(Pos.CENTER);

		StackPane root = new StackPane(imageView, buttonContainer);
		Scene scene = new Scene(root, 1000, 600);

		primaryStage.setTitle("Map Viewer");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void openGraphStage(Stage primaryStage) {
		VBox controlsBox = new VBox(20);

		Image mapImage = new Image(getClass().getResource("/Map.png").toExternalForm());
		ImageView mapView = new ImageView(mapImage);
		int height = 600;
		int width = 1200;
		mapView.setFitHeight(height);
		mapView.setFitWidth(width);
		Graph.setInhales();

		Group mapGroup = new Group(mapView);

		// Load cities and draw city points
		Graph.canvasWidth = width;
		Graph.canvasHeight = height;
		LinkedList<Vertex> cities = Graph.loadCitiesFromFile();

		if (cities.isEmpty()) {
			//showAlert(Alert.AlertType.ERROR, "File Error", "❌ Failed to load cities. Please check the file.");
			return;
		}

		Graph.drawCityPoints(mapGroup);

		// ComboBoxes for source and target
		comboSource = new ComboBox<>();
		comboTarget = new ComboBox<>();

		for (Vertex v : cities) {
			comboSource.getItems().add(v.getCity().getName());
			comboTarget.getItems().add(v.getCity().getName());
		}

		comboSource.setPromptText("Select Source City");
		comboTarget.setPromptText("Select Target City");

		comboSource.setPrefWidth(180);
		comboTarget.setPrefWidth(180);

		// TextArea to show path
		TextArea pathArea = new TextArea();
		pathArea.setPrefHeight(150);
		pathArea.setPrefWidth(350);

		// Distance text field
		TextField distanceField = new TextField();
		distanceField.setEditable(false);
		distanceField.setAlignment(Pos.CENTER);
		distanceField.setPrefWidth(100);

		// Labels
		Label pathLabel = new Label("Path:");
		Label distanceLabel = new Label("Distance:");

		HBox pathBox = new HBox(10, pathLabel, pathArea);
		HBox distanceBox = new HBox(10, distanceLabel, distanceField);
		pathBox.setAlignment(Pos.CENTER_LEFT);
		distanceBox.setAlignment(Pos.CENTER_LEFT);

		// Buttons
		Button clearButton = new Button("Clear");
		clearButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
		clearButton.setPrefWidth(100);

		Button findPathButton = new Button("Find Path");
		findPathButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
		findPathButton.setPrefWidth(100);

		VBox comboBoxContainer = new VBox(15, comboSource, comboTarget, findPathButton, clearButton);
		comboBoxContainer.setAlignment(Pos.CENTER);

		controlsBox.getChildren().addAll(comboBoxContainer, pathBox, distanceBox);
		controlsBox.setPadding(new Insets(15));
		controlsBox.setStyle("-fx-background-color: #2B2B2B;");

		// Main layout
		HBox mainLayout = new HBox(20, mapGroup, controlsBox);
		mainLayout.setPadding(new Insets(10));

		Scene scene = new Scene(mainLayout, 1400, 700);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Dijkstra - Select Cities from Map or Dropdown");
		primaryStage.centerOnScreen();
		primaryStage.show();

		// Map mouse click to select source and target
		mapGroup.setOnMouseClicked(event -> {
			double clickX = event.getX();
			double clickY = event.getY();

			Vertex clickedVertex = findClosestCity(clickX, clickY);
			if (clickedVertex == null) return;

			if (selectedSource == null) {
				selectedSource = clickedVertex;
				comboSource.getSelectionModel().select(selectedSource.getCity().getName());
				//(Alert.AlertType.INFORMATION, "Source Selected", "Source city selected: " + selectedSource.getCity().getName());
			} else if (selectedTarget == null) {
				selectedTarget = clickedVertex;
				comboTarget.getSelectionModel().select(selectedTarget.getCity().getName());
				//showAlert(Alert.AlertType.INFORMATION, "Target Selected", "Target city selected: " + selectedTarget.getCity().getName());
				runDijkstraAndDisplayPath(mapGroup, pathArea, distanceField);
			} else {
				// Reset if both selected and user clicks again
				clearPathLines(mapGroup);
				selectedSource = clickedVertex;
				selectedTarget = null;
				comboSource.getSelectionModel().select(selectedSource.getCity().getName());
				comboTarget.getSelectionModel().clearSelection();
				pathArea.clear();
				distanceField.clear();
				//showAlert(Alert.AlertType.INFORMATION, "Source Re-selected", "Source city re-selected: " + selectedSource.getCity().getName());
			}
		});

		// ComboBox change listeners
		comboSource.setOnAction(e -> {
			String cityName = comboSource.getSelectionModel().getSelectedItem();
			if (cityName != null) {
				selectedSource = Graph.cityMap.get(cityName);
			}
		});

		comboTarget.setOnAction(e -> {
			String cityName = comboTarget.getSelectionModel().getSelectedItem();
			if (cityName != null) {
				selectedTarget = Graph.cityMap.get(cityName);
			}
		});

		// Find path button handler
		findPathButton.setOnAction(e -> {
			if (selectedSource == null || selectedTarget == null) {
				//showAlert(Alert.AlertType.WARNING, "Selection Missing", "Please select both source and target cities.");
				return;
			}
			runDijkstraAndDisplayPath(mapGroup, pathArea, distanceField);
		});

		// Clear button handler
		clearButton.setOnAction(e -> {
			clearPathLines(mapGroup);
			selectedSource = null;
			selectedTarget = null;
			comboSource.getSelectionModel().clearSelection();
			comboTarget.getSelectionModel().clearSelection();
			pathArea.clear();
			distanceField.clear();
		});
	}

	private void runDijkstraAndDisplayPath(Group mapGroup, TextArea pathArea, TextField distanceField) {
		Graph graph = new Graph();
		graph.dijkstra(selectedSource, selectedTarget);
		LinkedList<String> path = graph.getShortestPath(selectedSource, selectedTarget);

		if (path.isEmpty() || path.get(0).equals("No Path")) {
			pathArea.setText("No Path Found");
			distanceField.setText("∞");
			return;
		}

		// Format path with arrows and line breaks every 5 cities
		StringBuilder pathText = new StringBuilder();
		int count = 0;
		for (int i = 0; i < path.size(); i++) {
			pathText.append(path.get(i));
			if (i != path.size() - 1) {
				pathText.append(" ---> ");
			}
			count++;
			if (count % 5 == 0 && i != path.size() - 1) {
				pathText.append("\n");
			}
		}
		pathArea.setText(pathText.toString());

		// Distance
		if (selectedTarget.getDistance() == Double.MAX_VALUE) {
			distanceField.setText("No Path");
		} else {
			distanceField.setText(String.format("%.2f", selectedTarget.getDistance()));
		}

		// Draw path lines on map in RED
		clearPathLines(mapGroup);
		drawPathLines(mapGroup, selectedTarget);
	}

	private Vertex findClosestCity(double x, double y) {
		final double threshold = 15;  // pixel radius for click detection
		Vertex closest = null;
		double minDist = Double.MAX_VALUE;

		for (Vertex v : Graph.cityList) {
			double cityX = v.getCity().getMercatorX();
			double cityY = v.getCity().getMercatorY();
			double dist = Math.sqrt(Math.pow(cityX - x, 2) + Math.pow(cityY - y, 2));
			if (dist < threshold && dist < minDist) {
				minDist = dist;
				closest = v;
			}
		}
		return closest;
	}

	private void clearPathLines(Group group) {
		group.getChildren().removeIf(node -> node instanceof Line);
	}

	private void drawPathLines(Group group, Vertex target) {
		for (Vertex current = target; current.getPrev() != null; current = current.getPrev()) {
			double startX = current.getPrev().getCity().getMercatorX();
			double startY = current.getPrev().getCity().getMercatorY();
			double endX = current.getCity().getMercatorX();
			double endY = current.getCity().getMercatorY();

			Line line = new Line(startX, startY, endX, endY);
			line.setStroke(Color.RED);  // المسار باللون الأحمر كما طلبت
			line.setStrokeWidth(3);
			group.getChildren().add(line);
		}
	}

	private void showAlert(Alert.AlertType type, String title, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setContentText(content);
		alert.showAndWait();
	}
}
