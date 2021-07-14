package de.dfki.fastdownwardcaller;

import java.util.ArrayList;

public class Plan {

	int cost;
	ArrayList<PlanAction> actions;

	public Plan(ArrayList<PlanAction> steps, int cost) {

		this.actions = steps;
		this.cost = cost;
	}
	
	public Plan(PlanDetails details) {

		this.actions = details.getActions();
		this.cost = details.getCost();
	}

	public ArrayList<PlanAction> getActions(){
		return actions;
	}

	public int length() {
		return actions.size();
	}


	public PlanAction getStepNumber(int stepNumber) {

		for(PlanAction s: actions)
			if(s.getNumber() == stepNumber)
				return s;
		
		return null;
		
	}
	
	public int getCost() {
		return cost;
	}
	
	public String toString() {

		StringBuffer answer = new StringBuffer();

		for (PlanAction step: actions)
			answer.append(step.toString(false));

		return answer.toString();
	}

	public String toString(boolean includeCost, boolean removeHttpFormat) {

		StringBuffer answer = new StringBuffer();

		for (PlanAction step: actions)
			answer.append(step.toString(removeHttpFormat));
		
		if (includeCost)
		answer.append("Cost: " + cost + "\n");

		return answer.toString();
	}

//	public String toHumanReadableString(boolean includeCost) {
//
//		StringBuffer answer = new StringBuffer();
//
//		for (Step step: steps) 
//			answer.append(step.toString().replaceAll("(http:\\S+.owl#)", ""));
//		
//		if (includeCost)
//		answer.append("Cost: " + cost + "\n");
//		
//		return answer.toString();
//	}

}
