package it.polito.tdp.extflightdelays.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {

	private SimpleWeightedGraph<Airport, DefaultWeightedEdge> grafo;
	private ExtFlightDelaysDAO dao;
	private Map<Integer, Airport> idMap;
	private Map<Airport, Airport> visita;
	
	public Model() {
		dao = new ExtFlightDelaysDAO();
		idMap = new HashMap<Integer, Airport>();
		dao.loadAllAirports(idMap);
	}
	
	public void creaGrafo(int x) {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//aggiungo vertici filtrati
		List<Airport> vertici = dao.getVertex(idMap, x);
		Graphs.addAllVertices(this.grafo, vertici);
		
		//aggiungo archi
		for(Rotta r: dao.getRotte(idMap)) {
			if(this.grafo.containsVertex(r.getA1()) && this.grafo.containsVertex(r.getA2())) {
				DefaultWeightedEdge e = this.grafo.getEdge(r.getA1(), r.getA2());
				
				if(e==null) {
					Graphs.addEdgeWithVertices(this.grafo, r.getA1(), r.getA2(), r.getN());
				}else {
					double pesoVecchio = this.grafo.getEdgeWeight(e);
					double pesoNuovo = pesoVecchio+r.getN();
					this.grafo.setEdgeWeight(e, pesoNuovo);
					
				}
			}
		}
	}
	
	public Set<Airport> getVertici(){
		return this.grafo.vertexSet();
	}
	
	public Integer getNVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public Integer getNArchi(){
		return this.grafo.edgeSet().size();
	}
	
	public List<Airport> trovaPercorso(Airport a1, Airport a2){
		
		List<Airport> percorso = new LinkedList<>();
		
		BreadthFirstIterator<Airport, DefaultWeightedEdge> bfi = new BreadthFirstIterator<>(this.grafo, a1);
		
		visita = new HashMap<Airport, Airport>();
		visita.put(a1, null);
		
		bfi.addTraversalListener(new TraversalListener<Airport, DefaultWeightedEdge>(){

			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {	
			}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultWeightedEdge> e) {
				Airport air1 = grafo.getEdgeSource(e.getEdge());
				Airport air2 = grafo.getEdgeTarget(e.getEdge());
				if(visita.containsKey(air1) && !visita.containsKey(air2)) {
					visita.put(air2, air1);
				}else if(visita.containsKey(air2) && !visita.containsKey(air1)){
					visita.put(air1, air2);
				}
			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<Airport> e) {	
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<Airport> e) {	
			}
			
		});
		
		while(bfi.hasNext()) {
			bfi.next();
		}
		
		if(!visita.containsKey(a1) || !visita.containsKey(a2)) {
			return null;
		}
		
		percorso.add(a2);
		Airport step = a2;
		while(visita.get(step)!=null) {
			step = visita.get(step);
			percorso.add(step);
		}
		
		return percorso;
	}
}
