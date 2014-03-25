package org.fnlp.app.keyword;

import java.util.ArrayList;

public class Graph {
	private ArrayList<Vertex> vertexList = new ArrayList<Vertex>();
	private int nVerts = 0;
	
	public ArrayList<Vertex> getVertexList(){
		return vertexList;
	}
	
	public int getNVerts(){
		return nVerts;
	}
	
	public Vertex getVertex(int index){
		return vertexList.get(index);
	}
	
	public int getIndex(String id){
		int index;
		for(index = 0; index < nVerts; index++)
			if(vertexList.get(index).getId() == id)
				break;
		if(index == nVerts)
			index = -1;
		return index;
	}
	
	public void addVertex(Vertex vertex){
		vertex.setIndex(nVerts);
		vertexList.add(vertex);
		nVerts++;
	}
	
	public void addEdge(int start, int end){
		Vertex vertex1 = vertexList.get(start);
		Vertex vertex2 = vertexList.get(end);
		if(vertex1.getNext() != null){
			int index = vertex1.getNext().indexOf(vertex2);
			if(index != -1){
				vertex1.setWNext(index, 1);
			}
			else
				vertex1.addVer(vertex2);
		}
		else
			vertexList.get(start).addVer(vertexList.get(end));
		vertexList.get(end).addForwardCount(1);
	}
}
