package de.dfki.fastdownwardcaller;

import java.util.Arrays;

public class PlanProfile {

	// Introduce a PlanProfile object, which contains a set of plans in ascending costs, with index starting at 1

	Plan[] plans;
	boolean sortedAscending;

	public PlanProfile(Plan[] plans) {

		Arrays.sort(plans, (a, b) -> a.getCost() - b.getCost());
		sortedAscending = true;

		this.plans = plans;

	}

	public PlanProfile(Plan[] plans, boolean leaveUnsorted) {

		if (!leaveUnsorted) {
			Arrays.sort(plans, (a, b) -> a.getCost() - b.getCost());
			sortedAscending = true;
		}

		this.plans = plans;

	}

	public Plan[] getPlansAscendingCost() {

		if (!sortedAscending) {
			Arrays.sort(plans, (a, b) -> a.getCost() - b.getCost());
			sortedAscending = true;
		}

		return plans;

	}

	public Plan[] getPlansDescendingCost() {

		Arrays.sort(plans, (a, b) -> b.getCost() - a.getCost());
		sortedAscending = false;


		return plans;

	}

	// Assume that the first plan is numbered 1
	// ascending = true means ascending order, false = descending
	public Plan getPlan(int number, boolean ascending){

		if(!ascending) {
			Arrays.sort(plans, (a, b) -> b.getCost() - a.getCost());
			sortedAscending = false;	
		}
		else { // should be ascending

			if (!sortedAscending) {
				Arrays.sort(plans, (a, b) -> a.getCost() - b.getCost());
				sortedAscending = true;
			}
		}


		return plans[number - 1];
	}

	public String toString(boolean includeCost, boolean removeHttp) {

		StringBuffer answer = new StringBuffer();

		for (Plan p: plans) {
			answer.append(p.toString(removeHttp, includeCost) + "\n");


		}
		return answer.toString();

	}

}
