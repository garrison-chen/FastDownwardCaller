package de.dfki.fastdownwardcaller;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;

public class FastDownwardCaller {

	private Path fastDownwardDirectory;
	private Path fastDownwardStart;
	private Path fastDownwardTempDirectory;
	private Path domainPath;
	private Path problemPath;
	private Path sasOutputPath;
	private Path portfolioDirectory;
	private Path onlyPlan;
	
	//private Path composedServiceOutputDirectory;

	private boolean deleteDomainFileAfter;
	private boolean deleteProblemFileAfter;

	private static final String TEMP_FOLDER_NAME = "tmp";
	private static final String PLAN_FILE_NAME = "plan";
	private static final String FAST_DOWNWARD_NAME = "fast-downward.py";
	private static final String PLAN_COST_IDENTIFIER = "(.*?)cost\\s=\\s-?[0-9]+(.*?)+"; // this means "match anything that includes 'cost = [some number]'"
	private static final String STEP_COUNTER_INIT = "<<-"; // used to identify the number of a step for regex parsers
	private static final String STEP_COUNTER_FINAL = "->>";
	private static final int TEMP_DIR_ATTEMPTS = 10000;
	public static final URI SERVER_URI = URI.create("http://127.0.0.1:8080");

	public FastDownwardCaller(String fastDownwardPath) {


		try {
			File f = new File(fastDownwardPath);

			if (!f.exists() || !f.isDirectory())
				throw new Exception("The following is not the FastDownward directory: " + fastDownwardPath);


			this.fastDownwardDirectory = Paths.get(fastDownwardPath);
			fastDownwardStart = this.fastDownwardDirectory.resolve(FAST_DOWNWARD_NAME);

			if (!fastDownwardStart.toFile().exists())
				throw new Exception("fast-downward.py not found in " + fastDownwardPath + "\n Are you sure this is the correct path?");

			if(!f.canWrite()) 
				throw new Exception("Java does not have write privileges in" + fastDownwardPath + "\n Please move the FastDownward directory where Java has write privileges");

			this.fastDownwardTempDirectory = this.fastDownwardDirectory.resolve(TEMP_FOLDER_NAME);

			if (!this.fastDownwardTempDirectory.toFile().exists())
				this.fastDownwardTempDirectory.toFile().mkdirs();
			
			//this.composedServiceOutputDirectory = Paths.get("/tmp/");

		}
		catch (Exception e) {
			e.printStackTrace();

		}

	}

	/*
	public void setComposedServiceOutputDirectory(String path) throws Exception {
		
		File check = new File(path);
		
		if (check.isDirectory())
			this.composedServiceOutputDirectory = Paths.get(path);
		else 
			throw new Exception(path + "is not a valid directory.");
		
		if(!check.canWrite()) 
			throw new Exception("Java does not have write privileges in" + path + "\n Please move the FastDownward directory where Java has write privileges");
		
	}
	*/
	
	public void readInDomain(String domainAsString) throws Exception{

		// check if domain was passed as a string, not a file
		File check = new File(domainAsString);

		if (check.exists()) {
			readInDomain(check);
			return;
		}

		try {
			File uniqueFile = File.createTempFile("domain", ".pddl", fastDownwardTempDirectory.toFile());
			this.domainPath = uniqueFile.toPath();
			writeFile(domainPath.toString(), domainAsString);
			deleteDomainFileAfter = true;
		}
		catch (IOException e) {
			e.printStackTrace();

		}

	}

	private void writeFile(String path, String fileContent) {

		FileWriter fileWriter;

		try {
			fileWriter = new FileWriter(path);
			fileWriter.write(fileContent);
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void readInProblem(String problemAsString) throws Exception{


		// check if domain was passed as a string, not a file
		File check = new File(problemAsString);

		if (check.exists()) {
			readInProblem(check);
			return;
		}

		try {
			File uniqueFile = File.createTempFile("problem", ".pddl", fastDownwardTempDirectory.toFile());
			this.problemPath = uniqueFile.toPath();
			writeFile(problemPath.toString(), problemAsString);
			deleteProblemFileAfter = true;
		}
		catch (IOException e) {
			e.printStackTrace();

		}

	}

	public void readInDomain(File file) throws Exception {

		if (!file.exists())
			throw new Exception("Domain file does not exist");

		this.domainPath = file.toPath();
		deleteDomainFileAfter = false;

	}

	public void readInProblem(File file) throws Exception {

		if (!file.exists())
			throw new Exception("Problem file does not exist");

		this.problemPath = file.toPath();
		deleteProblemFileAfter = false;

	}

	private void cleanUpTempFiles() {
		// all plan files, output files, and intermediates files made by fast-downward are deleted.
		// pre-existing pddl files passed to FastDownwardCaller as input are not deleted.

		try {

			Files.delete(sasOutputPath);

			if (deleteDomainFileAfter) 
				Files.delete(domainPath);

			if (deleteProblemFileAfter) 
				Files.delete(problemPath); 

			if (portfolioDirectory != null) {

				for (File plan : portfolioDirectory.toFile().listFiles()) 
					Files.delete(plan.toPath());

				Files.delete(portfolioDirectory);
			}

			else
				Files.delete(onlyPlan);


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}


	private void runCommand(String command, boolean verbose) {

		String s = null;

		try {

			//			String test = "cmd /c python " +fastDownwardStart.toString() + " --help";
			//			String[] tokens = test.split(" ");

			String[] tokens = command.split(" ");

			ProcessBuilder builder = new ProcessBuilder();


			builder.redirectErrorStream(true);
			builder.redirectInput();
			builder.command(tokens);
			builder.directory(fastDownwardDirectory.toFile());
			Process generateOutput = builder.start();	

			BufferedReader stdInput = new BufferedReader(new 
					InputStreamReader(generateOutput.getInputStream()));

			BufferedReader stdError = new BufferedReader(new 
					InputStreamReader(generateOutput.getErrorStream()));


			// read the output from the command
			while ((s = stdInput.readLine()) != null) {

				// according to 
				// https://stackoverflow.com/questions/5483830/process-waitfor-never-returns
				// not clearing the inputstream can cause deadlock
				if (verbose)
					System.out.println(s);
			}

			// read any errors from the attempted command
			while ((s = stdError.readLine()) != null) {
				if (verbose)
					System.out.println(s);
			}


			generateOutput.waitFor();




		}
		catch (IOException e) {
			e.printStackTrace();
		}

		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	/*
	private static void printFile(File file) {

		try {
			BufferedReader br = new BufferedReader(new FileReader(file.toString()));
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 */

	public File createTempDir() {

		File baseDir = fastDownwardTempDirectory.toFile();
		String baseName = System.currentTimeMillis() + "-";

		for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
			File tempDir = new File(baseDir, baseName + counter);
			if (tempDir.mkdir()) {
				return tempDir;
			}
		}
		throw new IllegalStateException("Failed to create directory within "
				+ TEMP_DIR_ATTEMPTS + " attempts (tried "
				+ baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
	}

	private String generateOutputFileCommand() {

		// setup sas_output
		try {
			File uniqueFile = File.createTempFile("sas_output", ".sas", fastDownwardTempDirectory.toFile());
			this.sasOutputPath = uniqueFile.toPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// the way that commandline is invoked differs between windows and linux
		if (System.getProperty("os.name").toLowerCase().contains("windows"))

			return "cmd /c python " + fastDownwardStart.toString() + " --sas-file " + sasOutputPath.toString() + " --translate " + domainPath.toString() 
			+ " " + problemPath.toString();
		else
			return fastDownwardStart.toString() + " --sas-file " + sasOutputPath.toString() + " --translate " + domainPath.toString() 
			+ " " + problemPath.toString();

	}

	private String generateFullPortfolioCommand() {

		File directory = createTempDir();
		portfolioDirectory = directory.toPath();
		String planFile = portfolioDirectory.resolve(PLAN_FILE_NAME).toString();

		// the way that commandline is invoked differs between windows and linux
		if (System.getProperty("os.name").toLowerCase().contains("windows"))

			return "cmd /c python " + fastDownwardStart.toString() + " --plan-file " + planFile + " --alias lama " + sasOutputPath.toString();
		// for example: ./fast-downward.py --search-time-limit 1m --plan-file ./test/sas_plan --alias seq-sat-fdss-2018 output.sas
		else

			return fastDownwardStart.toString() + " --search-time-limit 1m --plan-file " + planFile + " --alias seq-sat-fdss-2018 " + sasOutputPath.toString();

	}
	/*
	private String generateFirstPlanOnlyCommand() {

		try {
			File uniqueFile = File.createTempFile(PLAN_FILE_NAME, ".sas", fastDownwardTempDirectory.toFile());
			this.onlyPlan = uniqueFile.toPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		// the way that commandline is invoked differs between windows and linux
		if (System.getProperty("os.name").toLowerCase().contains("windows"))

			return "cmd /c python " + fastDownwardStart.toString() + " --plan-file " + onlyPlan + " --alias lama " + sasOutputPath.toString();
		// for example: ./fast-downward.py --search-time-limit 1m --plan-file ./test/sas_plan --alias seq-sat-fdss-2018 output.sas
		else

			return fastDownwardStart.toString() + " --search-time-limit 1m --plan-file " + onlyPlan + " --alias seq-sat-fdss-2018 " + sasOutputPath.toString();

	}
	 */

	private double getPlanCost(File plan) throws Exception  {

		try {

			Scanner input = new Scanner(plan);

			while(input.hasNext()) {
				String nextLine = input.nextLine();

				if (nextLine.matches(PLAN_COST_IDENTIFIER)) {
					input.close();
					return Double.parseDouble(nextLine.replaceAll("[^\\d.]", ""));
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		throw new Exception("No cost was found in plan " + plan.toString());


	}

	public String fileToString(File plan) {

		StringBuffer answer = new StringBuffer();

		try (BufferedReader br = new BufferedReader(new FileReader(plan.toString()))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				answer.append(line);
				answer.append("\n");
			}


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return answer.toString();

	}

	private File selectLowestPlan() {

		// scan the plan portfolio, then return the plan with the lowest cost
		File selectedPlan = null;

		File[] plans = portfolioDirectory.toFile().listFiles();

		double lowestCostPlan = Double.POSITIVE_INFINITY;

		for (File plan : plans) {

			double cost = Double.POSITIVE_INFINITY;

			try {
				cost = getPlanCost(plan);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (cost < lowestCostPlan) {
				lowestCostPlan = cost; 
				selectedPlan = plan;
			}
		}
		
		if (selectedPlan == null) {
			
			//if no plan is found, it throws an error instead of an empty plan.
			throw new java.lang.Error("no plan has been found!");
		}

		return selectedPlan;

	}

	private void initFastDownward(boolean verbose) {

		try {
			confirmFilesAreLoaded();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		runCommand(generateOutputFileCommand(), verbose);
		runCommand(generateFullPortfolioCommand(), verbose);

	}

	public String getLowestCostPlanAsString(boolean verbose) {

		initFastDownward(verbose);

		File selectedPlan = selectLowestPlan();

		StringBuffer answer = new StringBuffer(fileToString(selectedPlan));

		formatAnswer(answer);

		cleanUpTempFiles();

		return answer.toString();
	}

	public Plan[] getAllPlans(boolean verbose) {

		initFastDownward(verbose);

		File[] files = portfolioDirectory.toFile().listFiles();
		Plan[] plans = new Plan[files.length];

		// load plan files into array
		for (int i = 0; i < files.length; i++ ) {
			StringBuffer answer = new StringBuffer(fileToString(files[i]));
			plans[i] = new Plan(generatePlan(answer));
		}

		// sort according to increasing cost
		Arrays.sort(plans, (a, b) -> a.getCost() - b.getCost());

		cleanUpTempFiles();

		return plans;

	}

	public Plan getLowestCostPlan(boolean verbose) {

		initFastDownward(verbose);

		File selectedPlan = selectLowestPlan();

		StringBuffer answer = new StringBuffer(fileToString(selectedPlan));

		cleanUpTempFiles();

		return new Plan(generatePlan(answer));
	}

	private ArrayList<PlanAction> generateSteps(StringBuffer buffer){

		ArrayList<PlanAction> steps = new ArrayList<PlanAction>();

		// match anything between brackets -- aside from the cost these are assumed to be steps
		Pattern p = Pattern.compile("\\((.*?)\\)");

		Matcher matcher = p.matcher(buffer);
		int counter = 1;

		while(matcher.find()) { 

			// skip the line containing the plan cost -- that's not a step
			if (matcher.group(0).matches("(.*?)unit cost(.*?)+"))
				continue;

			// encode the step
			else
				steps.add(new PlanAction(formatStep(matcher.group(0), counter)));

			counter++;
		}

		return steps;

	}

	private int getPlanCost(StringBuffer buffer) {

		// since the cost is definitely at the end of the buffer, read in from the end for efficiency
		Pattern p = Pattern.compile("[0-9]+-? = tsoc"); // backwards regex for "cost = [negative?][any number]"
		Matcher m = p.matcher(new StringBuilder(buffer).reverse());

		if(m.find()) {
			String regularAnswer = new StringBuilder(m.group(0)).reverse().toString();
			return Integer.parseInt(regularAnswer.replaceAll("[^\\d.]", ""));
		}

		// if no cost is found, then throw out the plan from further consideration using maximum cost
		return Integer.MAX_VALUE;

	}

	private PlanDetails generatePlan(StringBuffer buffer) {

		return new PlanDetails(getPlanCost(buffer), generateSteps(buffer));

	}

	private void formatAnswer(StringBuffer buffer) {

		//		Pattern p = Pattern.compile("\\((.*?)\\)(\\s+or\\s\\((.*?)\\))+");
		//		Pattern p = Pattern.compile("\\((.*?)\\)(\\s+[^\\W\\d_]+\\s\\((.*?)\\))+");

		// match anything between brackets -- aside from the cost these are assumed to be steps
		Pattern p = Pattern.compile("\\((.*?)\\)");

		Matcher matcher = p.matcher(buffer);

		StringBuffer fixed = new StringBuffer();

		int counter = 1;

		while(matcher.find()) { 

			// don't make any changes to the line containing e.g., "; cost = 4 (unit cost)"
			if (matcher.group(0).contains("unit cost"))
				continue;
			else
				matcher.appendReplacement(fixed, formatStep(matcher.group(0), counter));

			counter++;
		}

		matcher.appendTail(fixed);
		buffer.delete(0, buffer.length());

		buffer.append(fixed);

	}


	public static String formatStep(String s, int counter) {

		//		if (s.contains(PLAN_COST_IDENTIFIER))
		//			return s;

		s = s.replaceAll("\\(", STEP_COUNTER_INIT + Integer.toString(counter) + STEP_COUNTER_FINAL);
		// replace opening bracket with number
		//StringBuilder builder = new StringBuilder(s.replaceAll("\\(", Integer.toString(counter) + ". "));

		// replace closing bracket with new line
		StringBuilder builder = new StringBuilder(s.replaceAll("\\)", "\n"));


		//builder.insert(0, "(or \t");
		//builder.insert(builder.length(), ")");

		return builder.toString();
	}

	private void confirmFilesAreLoaded() throws Exception{

		if (domainPath == null) 
			throw new Exception("Unable to call FastDownward -- No domain has been loaded");

		if (problemPath == null)
			throw new Exception("Unable to call FastDownward -- No problem has been loaded");

		if (!domainPath.toFile().exists())
			throw new Exception("Unable to call FastDownward -- Domain file does not exist: " + domainPath.toString());

		if (!problemPath.toFile().exists())
			throw new Exception("Unable to call FastDownward -- Problem file does not exist: " + problemPath.toString());

	}
	
	public static void main(String[] args) {
		
		
		// initialize pointing to FastDownward directory
		//FastDownwardCaller caller = new FastDownwardCaller("/home/anthony/Documents/Bioinformatics/Work/WELCOME/Programs/downward");
		FastDownwardCaller caller = new FastDownwardCaller("/Users/guangyichen/Desktop/DFKI/software/downward-main"); 

		try {
			caller.readInProblem(new File("/Users/guangyichen/Desktop/PDDL_example_input/11Apr2021_functional-problem.pddl"));
			//caller.readInDomain(new File("/home/anthony/Documents/Bioinformatics/Work/WELCOME/PDDL-and-OWL-Files/3Jan2021_manually-edited-domain.pddl"));
			caller.readInDomain(new File("/Users/guangyichen/Desktop/PDDL_example_input/11Apr2021_functional-domain.pddl"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//optional alternative: read in domain and problem as strings
		//caller.readInDomain(converter.getDomain().makePDDLTextDocument());
		//caller.readInProblem(converter.getProblem().makePDDLTextDocument());
		// "true" for verbose fast-downward output ("false" for quiet calling)
		
		// TODO: CHANGE
		Plan lowestCostPlan = caller.getLowestCostPlan(true);
		
		File outputDirectory = new File("/Users/guangyichen/Desktop/");
		URI compositeServiceUri =  outputDirectory.toURI();
		
		CompositeService actionToService = new CompositeService(lowestCostPlan, compositeServiceUri);
		Document composedService = actionToService.getDocument();
		
		
		/*
		Step stepTwo = lowestCostPlan.getStepNumber(2);

		// The two arguments toString(boolean includeCost, boolean removeHttp) determine whether to append the cost of the plan, and whether to remove http format (if any)
		System.out.println("The lowest-cost plan has " + lowestCostPlan.getLength() + " steps. Its full description is: \n\n" + lowestCostPlan.toString(true, false) 
		+ "\nand, without http format and cost:\n\n" + lowestCostPlan.toString(false, true) + "\nIts second step is \n\n"
		+ stepTwo.toString(true) + "\nand, in full format:\n" + stepTwo.toString(false));
		//
		//		System.out.println("\n-----------------------------------------------------------------------------------------------------------------------------------------------\n");
		//
		String lowestCostPlanAsString = caller.getLowestCostPlanAsString(false);
		System.out.println("The lowest-cost plan without using the Plan class:\n\n" + lowestCostPlanAsString);
		//
		//		System.out.println("\n-----------------------------------------------------------------------------------------------------------------------------------------------\n");

		PlanProfile allPlans = new PlanProfile(caller.getAllPlans(false));

		Plan[] sortedAscending = allPlans.getPlansAscendingCost();

		System.out.println("Plan portfolio in ascending order of cost:\n");
		for (Plan p: sortedAscending) 
			// by default, plans are printed in http format without cost
			System.out.println(p.toString());

		
		Plan[] sortedDescending = allPlans.getPlansDescendingCost();

		System.out.println("Plan portfolio in descending order of cost (including costs):\n");
		for (Plan p: sortedDescending) 
			System.out.println(p.toString(true, false));

		/*
		// getPlan(int, boolean): pass "true" for ascending cost order, and "false" for descending
		Plan planTwo = allPlans.getPlan(2, false);
		Step step = planTwo.getStepNumber(3);
		System.out.println("Plan two of the latter portfolio has " + planTwo.getLength() + " steps. Its third step is:\n");
		System.out.println(step.toString()); 
		System.out.println("Without http format:\n");
		System.out.println(step.toString(true));
		System.out.println("Its step number (" + step.getNumber()+ ") can be retrieved using step.getNumber(int number)");
		System.out.println("and its description using getDescription(boolean removeHttp): \n");
		System.out.println(step.getDescription(true));
		*/

	}

}
