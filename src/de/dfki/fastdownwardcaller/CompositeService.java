package de.dfki.fastdownwardcaller;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

//import com.sun.org.apache.xerces.internal.dom.DocumentImpl;

public class CompositeService {

	HashMap<URI, Document> constituentServices;
	Document compositeService;

	public static final String ALWAYS_TRUE = "http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#AlwaysTrue";

	public static final String BASE_NAMESPACE = "xml:base";

	public static final String CONTACT_INFO_ONE = "https://raw.githubusercontent.com/gtzionis/WelcomeOntology/main/welcome.ttl#OAC_Cambrils";
	public static final String CONTACT_INFO_TWO = "https://raw.githubusercontent.com/gtzionis/WelcomeOntology/main/welcome.ttl#Servei_Primera_Acollida";

	public static final String EXPR_EXPRESSIONBODY = "expr:expressionBody";

	public static final String XML_SCHEMA_STRING = "http://www.w3.org/2001/XMLSchema#string";

	public static final String FULL_ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWZYZ";

	public static final String RDF_ABOUT = "rdf:about";
	public static final String RDF_DATATYPE ="rdf:datatype";
	public static final String RDF_ID ="rdf:ID";
	public static final String RDF_RDF = "rdf:RDF";
	public static final String RDF_RESOURCE ="rdf:resource";

	public static final String NIL = "http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#nil";

	public static final String OWL_IMPORTS = "owl:imports";
	public static final String OWL_ONTOLOGY = "owl:Ontology";

	public static final String OBJLIST_FIRST = "objList:first";
	public static final String OBJLIST_REST ="objList:rest";

	public static final String PARENT = "http://www.daml.org/services/owl-s/1.1/Process.owl#TheParentPerform";

	public static final String PERFORM = "Perform";

	public static final String PLACEHOLDER = "Placeholder";

	public static final String PROCESS_ATOMICPROCESS = "process:AtomicProcess";
	public static final String PROCESS_COMPOSITEPROCESS = "process:CompositeProcess";
	public static final String PROCESS_CONTROLCONSTRUCTLIST = "process:ControlConstructList";
	public static final String PROCESS_COMPONENTS = "process:components";
	public static final String PROCESS_COMPOSEDOF = "process:composedOf";
	public static final String PROCESS_FROMPROCESS = "process:fromProcess";
	public static final String PROCESS_HASDATAFROM = "process:hasDataFrom";
	public static final String PROCESS_HASEFFECT = "process:hasEffect";
	public static final String PROCESS_HASINPUT = "process:hasInput";
	public static final String PROCESS_HASOUTPUT = "process:hasOutput";
	public static final String PROCESS_HASPRECONDITION = "process:hasPrecondition";	
	public static final String PROCESS_HASRESULT = "process:hasResult";
	public static final String PROCESS_INCONDITION = "process:inCondition";
	public static final String PROCESS_INPUT ="process:Input";
	public static final String PROCESS_INPUTBINDING = "process:InputBinding";	
	public static final String PROCESS_OUTPUT = "process:Output";
	public static final String PROCESS_OUTPUTBINDING = "process:OutputBinding";
	public static final String PROCESS_PARAMETERTYPE ="process:parameterType";
	public static final String PROCESS_PERFORM = "process:Perform";
	public static final String PROCESS_PROCESS = "process:process";
	public static final String PROCESS_RESULT = "process:Result";
	public static final String PROCESS_SEQUENCE = "process:Sequence";
	public static final String PROCESS_THEVAR ="process:theVar";
	public static final String PROCESS_TOPARAM = "process:toParam";
	public static final String PROCESS_VALUESOURCE = "process:valueSource";
	public static final String PROCESS_VALUEOF = "process:ValueOf";
	public static final String PROCESS_WITHOUTPUT = "process:withOutput";

	public static final String PROFILE_CONTACTINFORMATION = "profile:contactInformation";
	public static final String PROFILE_HASINPUT = "profile:hasInput";
	public static final String PROFILE_HASOUTPUT = "profile:hasOutput";
	public static final String PROFILE_HASPRECONDITION = "profile:hasPrecondition";
	public static final String PROFILE_HASPROCESS = "profile:has_process";
	public static final String PROFILE_HASRESULT = "profile:hasResult";
	public static final String PROFILE_PROFILE = "profile:Profile";
	public static final String PROFILE_SERVICENAME = "profile:serviceName";
	public static final String PROFILE_TEXTDESCRIPTION = "profile:textDescription";

	public static final String PDDLEXPR_PDDLEXPRESSION = "pddlexpr:PDDL-Expression";

	public static final String SERVICE_DESCRIBEDBY = "service:describedBy";
	public static final String SERVICE_DESCRIBES = "service:describes";
	public static final String SERVICE_PRESENTS = "service:presents";
	public static final String SERVICE_PRESENTEDBY = "service:presentedBy";
	public static final String SERVICE_SERVICE = "service:Service";

	public static final String WELCOME_HASNEXTDIP = "welcome:hasNextDIP";

	public static final String XMLNS = "xmlns";
	public static final String XMLNS_OBJLIST = "xmlns:objList";
	public static final String XMLNS_OBJLIST_VALUE = "http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#";
	public static final String XMLNS_PDDLEXPR = "xmlns:pddlexpr";
	public static final String XMLNS_PDDLEXPR_VALUE = "https://raw.githubusercontent.com/gtzionis/WelcomeOntology/main/PDDLExpression.owl#";

	public static final String XML_BASE ="xml:base";



	public CompositeService(Plan toConvert, URI selfURI) {

		constituentServices = new HashMap<URI, Document>();
		loadConstituentServices(toConvert, constituentServices);
		compositeService = generateCompositeService(constituentServices, toConvert, selfURI);

	}

	public Document getDocument() {
		return this.compositeService;
	}

	private static HashMap<String, String> getAllNameSpaces(Collection<Document> toParse){

		// key = namespace, value = URI to resource
		HashMap<String, String> answer = new HashMap<String, String>();

		for (Document nameSpaces: toParse) {

			Element rootNode = nameSpaces.getDocumentElement();
			NamedNodeMap attributes = rootNode.getAttributes();

			for (int i = 0; i < attributes.getLength(); i++) {
				Attr attr = (Attr) attributes.item(i);
				String attrName = attr.getNodeName();

				// xml:base should be equal to the file's URI
				if (attrName.equals(BASE_NAMESPACE) || attrName.equals(XMLNS))
					continue;

				String attrValue = attr.getNodeValue();
				//System.out.println("Found attribute: " + attrName + " with value: " + attrValue);

				if (!answer.containsKey(attrName))
					answer.put(attrName, attrValue);}}

		return answer;
	}

	private static List<String> getAllAttributes(Collection<Document> toParse, String nodeName){

		List<String> answer = new ArrayList<String>();

		for (Document grabImport: toParse) {

			NodeList nList = grabImport.getElementsByTagName(nodeName);

			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE){
					Element attr = (Element) nNode;

					//String attrName = attr.getNodeName();

					String attrValue = attr.getAttribute(RDF_RESOURCE);

					if (!answer.contains(attrValue))
						answer.add(attrValue);
				}
			}
		}


		return answer;
	}

	private static void loadPddlExpressions(NodeList nodeList, List<String> pddlExpressions) {

		for (int count = 0; count < nodeList.getLength(); count++) {

			Node tempNode = nodeList.item(count);
			boolean added = false;

			if (tempNode.getNodeName().equals(EXPR_EXPRESSIONBODY)) { 

				pddlExpressions.add(tempNode.getTextContent());
				added = true; // prevents infinite recursion

			}

			if (tempNode.hasChildNodes() && !added)
				loadPddlExpressions(tempNode.getChildNodes(), pddlExpressions);
		}

	}


	private static String combinePddlStatements(Collection<Document> constituents, String[] nodeParentNames) {

		List<String> allPddlExpressions = new ArrayList<String>();

		for (Document grabImport: constituents) {

			ArrayList<NodeList> nodes = new ArrayList<NodeList>();

			for (String s: nodeParentNames) {
				nodes.add(grabImport.getElementsByTagName(s));
			}

			for (NodeList nList: nodes) {
				for (int i = 0; i < nList.getLength(); i++) {
					Node nNode = nList.item(i);
					loadPddlExpressions(nNode.getChildNodes(), allPddlExpressions);
				}
			}
		}


		return appendWithinAndStatement(allPddlExpressions);
	}

	private static String appendWithinAndStatement(List<String> pddlExpressions) {

		StringBuffer answer = new StringBuffer();

		answer.append("(and\n");

		for (String expression: pddlExpressions)
			answer.append(expression + "\n");

		answer.append(")");


		return answer.toString();

	}

	private static String generateUniqueServiceName() {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyy");  
		LocalDateTime now = LocalDateTime.now();  
		LocalTime timeToMillis = java.time.LocalTime.now();
		String exactTime = timeToMillis.toString().replace(".", "");
		String dateAndTime = dtf.format(now) + exactTime.replace(":", "");

		Random r = new Random();

		StringBuilder randomLetters = new StringBuilder();

		for (int i = 0; i < 20; i++) 
			randomLetters.append(FULL_ALPHABET.charAt(r.nextInt(FULL_ALPHABET.length())));

		return dateAndTime + randomLetters.toString();

	}

	private static void addOwlOntologyNode(Document doc, Collection<Document> services, String uniqueName) {

		Element rootNode = doc.getDocumentElement();

		Element owlOntologyNode = doc.createElement(OWL_ONTOLOGY);
		rootNode.appendChild(owlOntologyNode);

		Attr owlOntologyAttribute = doc.createAttribute(RDF_ABOUT);
		owlOntologyAttribute.setValue(uniqueName);
		owlOntologyNode.setAttributeNode(owlOntologyAttribute);

		for (String entry : getAllAttributes(services, OWL_IMPORTS)) {

			Element owlImport = doc.createElement(OWL_IMPORTS);
			owlOntologyNode.appendChild(owlImport);

			Attr importUri = doc.createAttribute(RDF_RESOURCE);
			importUri.setValue(entry);
			owlImport.setAttributeNode(importUri);

		}
	}

	private static void addServiceServiceNode(Document doc, String uniqueName) {

		Element rootNode = doc.getDocumentElement();

		Element serviceService = doc.createElement(SERVICE_SERVICE);
		rootNode.appendChild(serviceService);
		serviceService.setAttributeNode(makeAttr(doc, RDF_ID, uniqueName));

		Element serviceDescribedBy = doc.createElement(SERVICE_DESCRIBEDBY);
		serviceService.appendChild(serviceDescribedBy);
		serviceDescribedBy.setAttributeNode(makeAttr(doc, RDF_RESOURCE, uniqueName + "b"));

		Element servicePresents = doc.createElement(SERVICE_PRESENTS);
		serviceService.appendChild(servicePresents);
		servicePresents.setAttributeNode(makeAttr(doc, RDF_RESOURCE, uniqueName + "c"));


	}

	private static Attr makeAttr(Document doc, String attrName, String attrValue) {
		Attr attr = doc.createAttribute(attrName);
		attr.setValue(attrValue);
		return attr;
	}

	private static List<Node> getAllProcessNodes(Collection<Document> toParse){

		List<Node> answer = new ArrayList<Node>();

		for (Document getProcesses: toParse) {

			NodeList atomic = getProcesses.getElementsByTagName(PROCESS_ATOMICPROCESS);

			for (int i = 0; i < atomic.getLength(); i++)
				answer.add(atomic.item(i));

			NodeList composite = getProcesses.getElementsByTagName(PROCESS_COMPOSITEPROCESS);

			for (int i = 0; i < composite.getLength(); i++)
				answer.add(composite.item(i));
		}


		return answer;
	}


	private static void addCompositeServiceNode(Document doc, Collection<Document> constituents, String uniqueName) {

		Element rootNode = doc.getDocumentElement();

		// start the process:CompositeProcess node
		Element processCompositeProcess = doc.createElement(PROCESS_COMPOSITEPROCESS);
		rootNode.appendChild(processCompositeProcess);
		processCompositeProcess.setAttributeNode(makeAttr(doc, RDF_ID, uniqueName + "b"));

		Element serviceDescribes = doc.createElement(SERVICE_DESCRIBES);
		processCompositeProcess.appendChild(serviceDescribes);
		serviceDescribes.setAttributeNode(makeAttr(doc, RDF_RESOURCE, uniqueName));

		Element processHasPrecondition = doc.createElement(PROCESS_HASPRECONDITION);
		processCompositeProcess.appendChild(processHasPrecondition);

		Element pddlexprPddlExpression = doc.createElement(PDDLEXPR_PDDLEXPRESSION);
		processHasPrecondition.appendChild(pddlexprPddlExpression);
		pddlexprPddlExpression.setAttributeNode(makeAttr(doc, RDF_ID, generateUniqueServiceName()));

		Element exprExpressionBody = doc.createElement(EXPR_EXPRESSIONBODY);
		pddlexprPddlExpression.appendChild(exprExpressionBody);
		exprExpressionBody.setAttributeNode(makeAttr(doc, RDF_DATATYPE, XML_SCHEMA_STRING));
		exprExpressionBody.appendChild(doc.createTextNode(combinePddlStatements(constituents, new String[]{PROCESS_HASPRECONDITION})));


		// add all inputs and outputs
		for (Document d: constituents) {


			NodeList inputs = d.getElementsByTagName(PROCESS_INPUT);

			for (int i = 0; i < inputs.getLength(); i++) {
				Node nNode = inputs.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE){
					Element processHasInput = doc.createElement(PROCESS_HASINPUT);
					pddlexprPddlExpression.appendChild(processHasInput);
					processHasInput.appendChild(doc.adoptNode(nNode.cloneNode(true)));}}

			NodeList outputs = d.getElementsByTagName(PROCESS_OUTPUT);

			for (int i = 0; i < outputs.getLength(); i++) {
				Node nNode = outputs.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE){
					Element processHasInput = doc.createElement(PROCESS_HASOUTPUT);
					pddlexprPddlExpression.appendChild(processHasInput);
					processHasInput.appendChild(doc.adoptNode(nNode.cloneNode(true)));}}
		}

		// start the process:composedOf node
		Element processComposedOf = doc.createElement(PROCESS_COMPOSEDOF);
		processCompositeProcess.appendChild(processComposedOf);

		Element processSequence = doc.createElement(PROCESS_SEQUENCE);
		processComposedOf.appendChild(processSequence);

		Element processComponents = doc.createElement(PROCESS_COMPONENTS);
		processSequence.appendChild(processComponents);

		Node parentNode = processComponents;

		List<Node> processNodes = getAllProcessNodes(constituents);

		// generate process:Components
		for (int i = 0; i < processNodes.size(); i++) {

			Node n = processNodes.get(i);

			Element processControlConstructList = doc.createElement(PROCESS_CONTROLCONSTRUCTLIST);
			parentNode.appendChild(processControlConstructList);

			Element objListFirst = doc.createElement(OBJLIST_FIRST);
			processControlConstructList.appendChild(objListFirst);

			generateProcessPerformNode(doc, objListFirst, n);

			Element objListRest = doc.createElement(OBJLIST_REST);
			processControlConstructList.appendChild(objListRest);

			if (i == processNodes.size()-1)
				objListRest.setAttributeNode(makeAttr(doc, RDF_RESOURCE, NIL));


			parentNode = objListRest;
		}

		// generate process:hasResult
		Element processHasResult = doc.createElement(PROCESS_HASRESULT);
		processCompositeProcess.appendChild(processHasResult);

		Element processResult = doc.createElement(PROCESS_RESULT);
		processHasResult.appendChild(processResult);
		processResult.setAttributeNode(makeAttr(doc, RDF_ID, uniqueName));

		Element processInCondition = doc.createElement(PROCESS_INCONDITION);
		processResult.appendChild(processInCondition);
		processInCondition.setAttributeNode(makeAttr(doc, RDF_RESOURCE, ALWAYS_TRUE));

		// generate process:withOutput for each output found in constituent services
		for (Node n: processNodes) {

			Element e = (Element) n;

			for (Element processOutputNode: asElementList(e.getElementsByTagName(PROCESS_HASOUTPUT))){
				String outputName = processOutputNode.getAttribute(RDF_RESOURCE);
				//String base = ((Element) d.getFirstChild()).getAttribute(XMLNS);

				Element processWithOutput = doc.createElement(PROCESS_WITHOUTPUT);
				processResult.appendChild(processWithOutput);

				Element processOutputBinding = doc.createElement(PROCESS_OUTPUTBINDING);
				processWithOutput.appendChild(processOutputBinding);

				Element processToParam = doc.createElement(PROCESS_TOPARAM);
				processOutputBinding.appendChild(processToParam);
				processToParam.setAttributeNode(makeAttr(doc, RDF_RESOURCE, outputName));

				Element processValueSource = doc.createElement(PROCESS_VALUESOURCE);
				processOutputBinding.appendChild(processValueSource);

				Element processValueOf = doc.createElement(PROCESS_VALUEOF);
				processValueSource.appendChild(processValueOf);

				Element processTheVar = doc.createElement(PROCESS_THEVAR);
				processValueOf.appendChild(processTheVar);
				processTheVar.setAttributeNode(makeAttr(doc, RDF_RESOURCE, outputName + "Atomic"));

				Element processFromProcess = doc.createElement(PROCESS_FROMPROCESS);
				processValueSource.appendChild(processFromProcess);
				processFromProcess.setAttributeNode(makeAttr(doc, RDF_RESOURCE, "#" + e.getAttribute(RDF_ID)));


			}
		}


		// generate process:hasEffect for each output found in constituent services
		// i'm assuming that all effects can be appended into a single PDDL statement
		Element processHasEffect = doc.createElement(PROCESS_HASEFFECT);
		processResult.appendChild(processHasEffect);

		Element pddlexprPddlExpressionEffect = doc.createElement(PDDLEXPR_PDDLEXPRESSION);
		processHasEffect.appendChild(pddlexprPddlExpressionEffect);

		Element exprExpressionBodyEffect = doc.createElement(EXPR_EXPRESSIONBODY);
		pddlexprPddlExpressionEffect.appendChild(exprExpressionBodyEffect);
		exprExpressionBodyEffect.setAttributeNode(makeAttr(doc, RDF_DATATYPE, XML_SCHEMA_STRING));
		exprExpressionBodyEffect.appendChild(doc.createTextNode(combinePddlStatements(constituents, new String[] {PROCESS_HASEFFECT})));


	}

	private static List<Element> asElementList(NodeList toSearch){

		List<Element> elements = new LinkedList<Element>();

		for (int i = 0; i < toSearch.getLength(); i++) {

			Node node = toSearch.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE){
				Element nodeAsElement = (Element) node;
				elements.add(nodeAsElement);
			}
		}

		return elements;
	}

	private static void generateProcessPerformNode(Document doc, Node parentNode, Node processNode) {

		//objListFirst.appendChild(doc.adoptNode(n.cloneNode(true)));

		Element attr = (Element) processNode;

		String attrName = attr.getNodeName();
		String attrValue = attr.getAttribute(RDF_ID);

		Element processPerform = doc.createElement(PROCESS_PERFORM);
		parentNode.appendChild(processPerform);
		processPerform.setAttributeNode(makeAttr(doc, RDF_ID, attrValue + PERFORM));

		Element processProcess = doc.createElement(PROCESS_PROCESS);
		processPerform.appendChild(processProcess);
		processProcess.setAttributeNode(makeAttr(doc, RDF_RESOURCE, "#" + attrValue));

		// get inputs from original process node; add its inputs as input bindings
		NodeList nList = ((Element)processNode).getElementsByTagName(PROCESS_HASINPUT);

		for (int i = 0; i < nList.getLength(); i++) {

			Node nNode = nList.item(i);
			String value = attr.getAttribute(RDF_ID);

			Element processHasDataFrom = doc.createElement(PROCESS_HASDATAFROM);
			processPerform.appendChild(processHasDataFrom);

			Element processInputBinding = doc.createElement(PROCESS_INPUTBINDING);
			processHasDataFrom.appendChild(processInputBinding);

			Element processToParam = doc.createElement(PROCESS_TOPARAM);
			processInputBinding.appendChild(processToParam);
			processToParam.setAttributeNode(makeAttr(doc, RDF_RESOURCE, "#" + value));

			Element processValueSource = doc.createElement(PROCESS_VALUESOURCE);
			processInputBinding.appendChild(processValueSource);

			Element processValueOf = doc.createElement(PROCESS_VALUEOF);
			processValueSource.appendChild(processValueOf);

			Element processTheVar = doc.createElement(PROCESS_THEVAR);
			processValueOf.appendChild(processTheVar);
			String newName = attrValue.replace("Atomic", "");
			processTheVar.setAttributeNode(makeAttr(doc, RDF_RESOURCE, "#" + newName.replace("Composed", "")));

			Element processFromProcess = doc.createElement(PROCESS_FROMPROCESS);
			processValueOf.appendChild(processFromProcess);
			processFromProcess.setAttributeNode(makeAttr(doc, RDF_RESOURCE, PARENT));

		}



	}

	private static void addConstituentProcessesNode(Document doc, Collection<Document> services) {

		Element rootNode = doc.getDocumentElement();

		for (Node process: getAllProcessNodes(services)) {

			Element processType = doc.createElement(process.getNodeName());
			rootNode.appendChild(processType);
			processType.setAttributeNode(makeAttr(doc, RDF_ID, process.getAttributes().getNamedItem(RDF_ID).getNodeValue()));

			for (Element welcomeHasNextDip: asElementList(((Element) process).getElementsByTagName(WELCOME_HASNEXTDIP))) 
				processType.appendChild(doc.adoptNode(welcomeHasNextDip.cloneNode(true)));

			for (Element processInput: asElementList(((Element) process).getElementsByTagName(PROCESS_INPUT))) {
				Element processHasInput = doc.createElement(PROCESS_HASINPUT);
				processType.appendChild(processHasInput);
				processHasInput.appendChild(doc.adoptNode(processInput.cloneNode(true)));
			}

			for (Element processOutput: asElementList(((Element) process).getElementsByTagName(PROCESS_OUTPUT))){
				Element processHasOutput = doc.createElement(PROCESS_HASOUTPUT);
				processType.appendChild(processHasOutput);
				processHasOutput.appendChild(doc.adoptNode(processOutput.cloneNode(true)));
			}

			for (Element processResult: asElementList(((Element) process).getElementsByTagName(PROCESS_RESULT))){
				Element processHasResult = doc.createElement(PROCESS_HASRESULT);
				processType.appendChild(processHasResult);
				processHasResult.appendChild(doc.adoptNode(processResult.cloneNode(true)));
			}



		}



	}

	private static void addProfileNode(Document doc, HashMap<URI, Document> services, String uniqueServicesName) {

		Element rootNode = doc.getDocumentElement();

		Element profileProfile = doc.createElement(PROFILE_PROFILE);
		rootNode.appendChild(profileProfile);
		profileProfile.setAttributeNode(makeAttr(doc, RDF_ID, uniqueServicesName + "c"));

		Element servicePresentedBy = doc.createElement(SERVICE_PRESENTEDBY);
		profileProfile.appendChild(servicePresentedBy);
		servicePresentedBy.setAttributeNode(makeAttr(doc, RDF_RESOURCE,"#" + uniqueServicesName));

		Element profileContactInformationOne = doc.createElement(PROFILE_CONTACTINFORMATION);
		profileProfile.appendChild(profileContactInformationOne);
		profileContactInformationOne.setAttributeNode(makeAttr(doc, RDF_RESOURCE, CONTACT_INFO_ONE));

		Element profileContactInformationTwo = doc.createElement(PROFILE_CONTACTINFORMATION);
		profileProfile.appendChild(profileContactInformationTwo);
		profileContactInformationTwo.setAttributeNode(makeAttr(doc, RDF_RESOURCE, CONTACT_INFO_TWO));

		List<Node> inputs = new LinkedList<Node>();
		List<Node> outputs = new LinkedList<Node>();
		List<Node> results = new LinkedList<Node>();
		List<Node> preconditions = new LinkedList<Node>();

		for (Node process: getAllProcessNodes(services.values())) {

			inputs.addAll(getProfileDescriptions(doc, process, PROCESS_HASINPUT));
			outputs.addAll(getProfileDescriptions(doc, process, PROCESS_HASOUTPUT));
			results.addAll(getProfileDescriptions(doc, process, PROCESS_HASRESULT));
			preconditions.addAll(getProfileDescriptions(doc, process, PROCESS_HASPRECONDITION));

		}

		for (Node n: inputs)
			profileProfile.appendChild(n);

		for (Node n: outputs)
			profileProfile.appendChild(n);

		for (Node n: results)
			profileProfile.appendChild(n);

		for (Node n: preconditions)
			profileProfile.appendChild(n);

		Element profileHasProcess = doc.createElement(PROFILE_HASPROCESS);
		profileProfile.appendChild(profileHasProcess);
		profileHasProcess.setAttributeNode(makeAttr(doc, RDF_RESOURCE, uniqueServicesName + "b"));

		Element profileServiceName = doc.createElement(PROFILE_SERVICENAME);
		profileProfile.appendChild(profileServiceName);
		profileServiceName.setAttributeNode(makeAttr(doc, RDF_DATATYPE, XML_SCHEMA_STRING));
		String date = uniqueServicesName.substring(0, 2) + "/" + uniqueServicesName.substring(2, 4) + "/" + uniqueServicesName.substring(4, 8);
		String time = uniqueServicesName.substring(8, 10) + ":" + uniqueServicesName.substring(10, 12) + "." + uniqueServicesName.substring(12, 20);
		String serviceDescription = "A composed service generated on " + date + " at " + time + " with arbitrary name \"" + uniqueServicesName.substring(20) + "\"";
		profileServiceName.appendChild(doc.createTextNode(serviceDescription));

		Element profileTextDescription = doc.createElement(PROFILE_SERVICENAME);
		profileProfile.appendChild(profileTextDescription);

		StringBuilder builder = new StringBuilder();

		for (URI value: services.keySet())
			builder.append("\n" + value.toString());

		profileTextDescription.appendChild(doc.createTextNode("Generated using actions within:" + builder.toString()));


	}

	private static List<Node> getProfileDescriptions(Document doc, Node toSearch, String searchTerm) {

		String noProfile = searchTerm.replace("process", "profile");

		List<Node> answer = new LinkedList<Node>();

		for (Element matchedElement: asElementList(((Element) toSearch).getElementsByTagName(searchTerm))) {

			Element adaptation = doc.createElement(noProfile);

			String value = matchedElement.getAttribute(RDF_RESOURCE);

			// recurse through children, first attempting to get RDF_ID, then RDF_RESOURCE
			if (value.equals("")) {
				for (Element e: asElementList(matchedElement.getChildNodes())) {

					value = e.getAttribute(RDF_ID);

					if (!value.equals("")) {

						if(!value.substring(0, 1).equals("#"))
							value = "#" + value;

						break;
					}
				}
			}

			adaptation.setAttributeNode(makeAttr(doc, RDF_RESOURCE, value));
			answer.add(adaptation);
		}

		return answer;
	}


	private static void addRdfRdfNode(Document doc, HashMap<URI, Document> services, URI selfURI) {

		// recall that the "original" xmlns should be the URI to this composite service
		Element rdfImportRootNode = doc.createElement(RDF_RDF);
		doc.appendChild(rdfImportRootNode);

		rdfImportRootNode.setAttribute(XMLNS, selfURI.toString());
		rdfImportRootNode.setAttribute(BASE_NAMESPACE, selfURI.toString());
		rdfImportRootNode.setAttribute(XMLNS_PDDLEXPR, XMLNS_PDDLEXPR_VALUE);
		rdfImportRootNode.setAttribute(XMLNS_OBJLIST, XMLNS_OBJLIST_VALUE);

		// add all namespaces from constituent actions
		for (Map.Entry<String, String> entry : getAllNameSpaces(services.values()).entrySet())
			rdfImportRootNode.setAttribute(entry.getKey(), entry.getValue());

	}

	private static ArrayList<Document> sortedDocuments(HashMap<URI, Document> services, Plan toConvert){

		ArrayList<Document> answer = new ArrayList<Document>();

		for (int i = 0; i < toConvert.length(); i++) {

			URI planUri = toConvert.getStepNumber(i + 1).getURI();

			// remove fragment
			if( planUri.getFragment() != null ) { 
				try {
					planUri = new URI( planUri.getScheme(), planUri.getSchemeSpecificPart(), /*fragment*/null );
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} };

				// match to key
				for (URI key: services.keySet()) {
					if (key.toString().toLowerCase().equals(planUri.toString().toLowerCase()))
						answer.add(services.get(key));
				}
		}


		return answer;
	}

	private static Document generateCompositeService(HashMap<URI, Document> services, Plan toConvert, URI selfURI) {


		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setNamespaceAware(true);
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			//Collections.sort(new ArrayList<Document>(services.values()), new Sortbyroll());

			Collection<Document> sorted = sortedDocuments(services, toConvert);

			addRdfRdfNode(doc, services, selfURI);

			String uniqueServiceName = generateUniqueServiceName();

			addOwlOntologyNode(doc, sorted, uniqueServiceName);

			addServiceServiceNode(doc, uniqueServiceName);

			addCompositeServiceNode(doc, sorted, uniqueServiceName);

			addConstituentProcessesNode(doc, sorted);

			addProfileNode(doc, services, uniqueServiceName);
			// write the content into xml file
			printToFile(doc, new File("/Users/guangyichen/Desktop/check.owl"));

			return doc;

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return null;
	}

	private static void printToFile(Document doc, File file) {

		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(file);

			transformer.transform(source, result);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		System.out.println("File saved!");

	}

	private static URL getMatchingUrl(URI originalUri) {

		// fun fact: fast downward always outputs its action names in lower case. However, URLs are case-sensitive.
		// so, this method searches localhost for a url that, when set to lower case, matches the originalUri

		// drop fragment from uri (i.e., portion after the '#')
		if( originalUri.getFragment() != null ) { 
			try {
				originalUri = new URI( originalUri.getScheme(), originalUri.getSchemeSpecificPart(), /*fragment*/null );
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} }


		List<URL> matchedLink = new ArrayList<URL>();
		// search localhost for a matching uri

		try {
			// potential infinite loops on server
			findMatchingUrl(FastDownwardCaller.SERVER_URI.toString(), originalUri.toString(), null, matchedLink);
			//findMatchingUrl(FastDownwardCaller.SERVER_URI.toString()+"/services", originalUri.toString(), null, matchedLink);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		if (matchedLink.get(0) == null) {
			
			throw new java.lang.Error("no maching services found, please recheck the files on your hosting directory");
			
		}
		
		return matchedLink.get(0);
	}

	public static void findMatchingUrl(String urlToSearch, String toMatch, List<URL> visitedLinks, List<URL> matchedLink) throws IOException{

		//the link has been found -- exit out of recursion
		if (matchedLink.size() > 0)
			return;

		if (visitedLinks == null)
			visitedLinks = new ArrayList<URL>();

		org.jsoup.nodes.Document doc = Jsoup.connect(urlToSearch).ignoreContentType(true).get();
		Elements links = doc.select("a[href]");

		for (org.jsoup.nodes.Element i : links) {

			URL link = new URL(print("%s", i.attr("abs:href")));

			if (visitedLinks.contains(link))
				continue;

			if (link.toString().toLowerCase().equals(toMatch)) {
				matchedLink.add(link);
				return;
			}

			if (link.toString().endsWith("/")){
				findMatchingUrl(link.toString(), toMatch, visitedLinks, matchedLink);}
			else {visitedLinks.add(link);}
		}

	}

	private static String print(String msg, Object... args){return String.format(msg, args);}

	private static void loadConstituentServices(Plan plan, HashMap<URI, Document> toLoad) {

		for (PlanAction action: plan.getActions()) {

			String scheme = action.getURI().getScheme();

			if ((scheme == null) || !scheme.equalsIgnoreCase("file")) {

				// this is not a file -- parse URL

				try {
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();

					//TODO: it would be more efficient to match the URLs in one method call, instead of re-searching for each individual URL
					URL actualUrl = getMatchingUrl(action.getURI());
					Document doc = db.parse(actualUrl.openStream());
					toLoad.put(actualUrl.toURI(), doc);

				} catch (ParserConfigurationException | SAXException | IOException | URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();}}

			else {

				// parse file
				try { 
					DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					File file = new File(action.getURI());
					toLoad.put(file.toURI(), dBuilder.parse(file));
				} catch (ParserConfigurationException | SAXException | IOException e) {}}}
	}

}

class Sortbyroll implements Comparator<Document>
{
	public int compare(Document a, Document b)
	{
		return 2;
	}
}



