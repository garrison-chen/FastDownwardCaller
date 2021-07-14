package de.dfki.fastdownwardcaller;

import java.util.ArrayList;

public class PlanDetails {
	
	int cost;
	ArrayList<PlanAction> steps;
	
	public PlanDetails(int cost, ArrayList<PlanAction> steps) {
		
		this.cost = cost;
		this.steps = steps;
		
	}
	
	public int getCost() {
		return cost;
	}

	public ArrayList<PlanAction> getActions() {
		return steps;
	}

}
