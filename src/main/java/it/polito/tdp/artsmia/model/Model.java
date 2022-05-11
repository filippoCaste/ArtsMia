package it.polito.tdp.artsmia.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {
	
	private Graph<ArtObject, DefaultWeightedEdge> grafo; // il peso indica il numero di esibizioni in cui appaiono assieme
	private ArtsmiaDAO dao;
	private Map<Integer, ArtObject> idMap;
	
	public Model() {
		dao = new ArtsmiaDAO();
		this.idMap = new HashMap<Integer, ArtObject>();
	}

	public void creaGrafo() {
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		this.dao.listObjects(idMap); // passo al metodo la mappa affinché la riempia
		
		// aggiunta dei vertici
		Graphs.addAllVertices(grafo, idMap.values());
		
		// aggiunta degli archi
		
		/* NON ADATTO PERCHE' DATABASE GRANDE */
		
		/* 1. a partire dai vertici, doppio for per recuperare le coppie di vertici chiedendo al db vedere se sono da collegare */
//		for(ArtObject a1 : this.grafo.vertexSet()) {
//			for(ArtObject a2 : this.grafo.vertexSet()) {
//				if(!a1.equals(a2) && !this.grafo.containsEdge(a1, a2)) {
//					// richiesta al db
//					int peso = dao.getPeso(a1, a2);
//					if(peso>0) {
//						Graphs.addEdgeWithVertices(this.grafo, a1, a2, peso);
//					}
//				}
//			}
//		}
		
		/* 2. un solo ciclo for sui vertici, e si fa una query per vedere con quali oggetti è collegato */
		for(Adiacenza a : this.dao.getAdiacenze(idMap)) {
			Graphs.addEdgeWithVertices(grafo, a.getA1(), a.getA2(), a.getPeso());
		}
		
		System.out.println("Grafo creato!\n - Numero di vertici: "+this.grafo.vertexSet().size() + ";\n - Numero di archi: "+ this.grafo.edgeSet().size() + ".");
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}

	public ArtObject getObjectById(int objId) {
		return this.idMap.get(objId);
	}

	public int getComponenteConnessa(ArtObject vertice) {
		Set<ArtObject> visitati = new HashSet<>();
		DepthFirstIterator<ArtObject, DefaultWeightedEdge> it = new DepthFirstIterator<>(this.grafo, vertice);
		
		while(it.hasNext()) {
			visitati.add(it.next());
		}
		
		/*
		 * Connectivity inspector --> set dei vertici connessi --> fatto
		 */
		
		return visitati.size();
	}
}
