package hu.bme.mit.yakindu.analysis.workhere;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.stext.stext.EventDefinition;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.RuntimeService;
import hu.bme.mit.yakindu.analysis.TimerService;
import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;
import hu.bme.mit.yakindu.analysis.example.IExampleStatemachine;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// Reading model
		Statechart s = (Statechart) root;
		TreeIterator<EObject> iterator = s.eAllContents();
		
		//int counter = 1;
		List<String> ev  = new ArrayList<String>();
		List<String> var = new ArrayList<String>();
		
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			
			if(content instanceof EventDefinition) {
				EventDefinition ed = (EventDefinition) content;
				ev.add(ed.getName());
				//System.out.println(ed.getName() + " event");
			}
			else if(content instanceof VariableDefinition) {
				VariableDefinition vd = (VariableDefinition) content;
				var.add(vd.getName());
				//System.out.println(vd.getName() + " variable");
			}
			
			/*
			if(content instanceof State) {
				State state = (State) content;
				if(state.getName().equals("")) {
					state.setName("UNKNOWN STATE" + counter);
					System.out.println(state.getName());
					counter += 1;
				}
				if(state.getOutgoingTransitions().size() == 0) {
					System.out.println("from " + state.getName() + " State there is no outgoing transition, its a trap!");
				}
				else {
					System.out.println("State: " + state.getName());
				}
			}
			else if(content instanceof Transition) {
				Transition transition = (Transition) content;
				System.out.println(transition.getSource().getName() + " -> " + transition.getTarget().getName());
			}
			*/
		}
		
		System.out.println("public class RunStatechart {\n");
		System.out.println("\tpublic static void main(String[] args) throws IOException {");
		System.out.println("\t\tExampleStatemachine s = new ExampleStatemachine();\r\n" + 
				"		s.setTimer(new TimerService());\r\n" + 
				"		RuntimeService.getInstance().registerStatemachine(s, 200);\r\n" + 
				"		s.init();\r\n" + 
				"		s.enter();\r\n" + 
				"		s.runCycle();\r\n" + 
				"		Scanner scanner = new Scanner(System.in);\r\n" + 
				"		while(scanner.hasNextLine()) {\r\n" +
				"		\tString command = scanner.nextLine();\r\n" + 
				"			switch(command) {");
		
		for(String event:ev) {
			String capitalized = event.substring(0, 1).toUpperCase() + event.substring(1);
			System.out.println("\t\t\t\tcase \"" + event + "\":\r\n" +
					"			\t\ts.raise" + capitalized + "();");
			System.out.println("\t\t\t\t\ts.runCycle();\r\n" + 
					"			\t\tbreak;");
		}
		System.out.println("\t\t\t\tcase \"exit\":\r\n" + 
				"					print(s);\r\n" + 
				"					System.exit(0);\r\n" + 
				"					break;\r\n" + 
				"				default:\r\n" + 
				"					System.out.println(\"unknown command\");\r\n" + 
				"					break;");
		
		System.out.println("\t\t\t}");
		System.out.println("\t\t\tprint(s);");
		System.out.println("\t\t}");
		System.out.println("\t}\n");
		
		System.out.println("\tpublic static void print(IExampleStateachine s) {");
		
		for(String variable:var) {
			String capitalized = variable.substring(0, 1).toUpperCase() + variable.substring(1);
			System.out.println("\t\tSystem.out.println(\"" + capitalized.charAt(0) + " = \" + s.getSCInterface().get" + capitalized + "());");
		}
		System.out.println("\t}");
		System.out.println("}");
		
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
}
