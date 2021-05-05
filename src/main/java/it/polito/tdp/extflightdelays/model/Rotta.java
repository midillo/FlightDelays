package it.polito.tdp.extflightdelays.model;

public class Rotta {

	private Airport A1; 
	private Airport A2;
	private int n;
	
	public Rotta(Airport a1, Airport a2, int n) {
		super();
		A1 = a1;
		A2 = a2;
		this.n = n;
	}
	public Airport getA1() {
		return A1;
	}
	public void setA1(Airport a1) {
		A1 = a1;
	}
	public Airport getA2() {
		return A2;
	}
	public void setA2(Airport a2) {
		A2 = a2;
	}
	public int getN() {
		return n;
	}
	public void setN(int n) {
		this.n = n;
	}
	
}
