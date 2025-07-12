package com.example.usadijkstra;

import java.util.ArrayList;

public class MinHeap {
    private final ArrayList<Vertex> heap;

    public MinHeap() {
        heap = new ArrayList<>();
    }

    public void add(Vertex vertex) {
        heap.add(vertex);
        heapifyUp(heap.size() - 1);
    }

    public Vertex poll() {
        if (isEmpty()) return null;

        Vertex min = heap.get(0);
        Vertex last = heap.remove(heap.size() - 1);

        if (!heap.isEmpty()) {
            heap.set(0, last);
            heapifyDown(0);
        }

        return min;
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public void clear() {
        heap.clear();
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;

            if (heap.get(index).getDistance() < heap.get(parent).getDistance()) {
                swap(index, parent);
                index = parent;
            } else break;
        }
    }

    private void heapifyDown(int index) {
        int size = heap.size();

        while (index < size) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = index;

            if (left < size && heap.get(left).getDistance() < heap.get(smallest).getDistance()) {
                smallest = left;
            }

            if (right < size && heap.get(right).getDistance() < heap.get(smallest).getDistance()) {
                smallest = right;
            }

            if (smallest != index) {
                swap(index, smallest);
                index = smallest;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        Vertex temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
}
