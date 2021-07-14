package de.dfki.fastdownwardcaller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlanAction {

	String actionName;
	int stepNumber;


	public PlanAction(String originalInput) {

		parseVariables(originalInput);
	}



	private void parseVariables(String s) {

		Pattern p = Pattern.compile("(<<-[0-9]+->>)+");

		Matcher matcher = p.matcher(s);

		while(matcher.find()) 
			this.stepNumber = Integer.parseInt(matcher.group(0).replaceAll("[^\\d.]", ""));

		this.actionName = s.replaceAll("(<<-[0-9]+->>)+", "");
	}

	public String toString(boolean removeHttp) {

		return Integer.toString(stepNumber) + ". " + getActionName(removeHttp);

	}
	
	public String toString() {

		return Integer.toString(stepNumber) + ". " + getActionName(false);

	}

	public int getNumber() {

		return stepNumber;
	}
	
	public URI getURI() {
		
		try {
			String firstArgument = actionName.split("\\s+")[0];
			return new URI(firstArgument);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public String getActionName(boolean removeHttp) {

		if (removeHttp)
			return actionName.replaceAll("(http:\\S+.owl#)", "");
		else
			return actionName;
	}




}
