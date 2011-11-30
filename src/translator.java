import java.io.*;
import java.util.*;

public class translator {
	private static HashMap<String,HashSet<ArrayList<String>>> rules = new HashMap<String,HashSet<ArrayList<String>>>();
	private static HashSet<String> variables = new HashSet<String>();
	private static HashSet<String> terminals = new HashSet<String>();
	private static ArrayList<String> inFile = new ArrayList<String>();
	private static String oldStarting = new String();

	public static void main(String[] args) {
		try{
			  FileInputStream fstream = new FileInputStream("N:\\workspace\\CNF translator\\bin\\input.txt");
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine;
			  System.out.println("Reading inputfile for rules: ");
			  
			  while ((strLine = br.readLine()) != null)   {
				  inFile.add(strLine);
				  String[] rootAndProduct = strLine.split("->");
				  variables.add(rootAndProduct[0].trim());
				  String[] individualProduct = rootAndProduct[1].split("-");
				  HashSet<ArrayList<String>> productsFromSameRoot = new HashSet<ArrayList<String>>();
				  
				  for (String s : individualProduct)
				  {
					  ArrayList<String> productUnit = new ArrayList<String>(Arrays.asList(s.split(" ")));
					  //String singleRule = new String();
					  for (String s1 : productUnit)
					  {
						  if (s1.length() == 1)
							  terminals.add(s1);
						  else if (s1.length() == 2) 
							  variables.add(s1);
						  //singleRule += s1 + " ";
					  }
					  productsFromSameRoot.add(productUnit);				
					  PrintRule(rootAndProduct[0].trim(), productUnit);
				  }
				  rules.put(rootAndProduct[0].trim(), productsFromSameRoot);
			  }
			  in.close();
			  
			  System.out.println("\nStep 1: create new rules from terminals and replace terminals by them.");
			  VariablesFromTerminal();
			  
			  System.out.println("\nStep 2: shorten old rules");
			  NoMoreLongRules();
			  
			  System.out.println("\nStep 3: introduce new starting");
			  NewStartingSymbol();
			  
			  System.out.println("\nStep 4: remove possible epsilon variables");
			  RemoveEpsilon();
			  
			  System.out.println("\nStep 5: remove unit rules");
			  RemoveUnitRules();
			  
			  
			    }catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.toString());
			  }
	}

	private static void RemoveUnitRules() {
		
		
	}

	private static void RemoveEpsilon() {
		System.out.println("Variables that can possible generate epsilon: ");
		HashSet<String> epsiVariables = FindPossibleEpsilon();
		for (String s : epsiVariables)
			System.out.println(s);
		
		HashMap<String, HashSet<ArrayList<String>>> newRules = new HashMap<String, HashSet<ArrayList<String>>>();
		for (String var : variables)
		{
			HashSet<ArrayList<String>> varRules = rules.get(var);
			HashSet<ArrayList<String>> varNewRules = new HashSet<ArrayList<String>>();
			for (ArrayList<String> temp : varRules)
			{
				varNewRules.add(temp);
			}
			for (ArrayList<String> singleRule : varRules)
			{
				ArrayList<String> newSingleRule = new ArrayList<String>();
				boolean removed = false;
				for (String unit : singleRule)
				{
					if ((!removed) && (epsiVariables.contains(unit))&&(singleRule.size() == 2))
						removed = true;
					else
						newSingleRule.add(unit);
				}
				if (removed)
					varNewRules.add(newSingleRule);
			}
			newRules.put(var, varNewRules);
		}
		rules = newRules;
		newRules = new HashMap<String, HashSet<ArrayList<String>>>();
		for (String var : variables)
		{
			HashSet<ArrayList<String>> varRule = rules.get(var);
			HashSet<ArrayList<String>> varNewRule = new HashSet<ArrayList<String>>();
			for (ArrayList<String> singleRule : varRule)
			{
				if ((singleRule.size() > 1) || (!(singleRule.get(0).equals("Epsi"))))
					varNewRule.add(singleRule);
			}
			newRules.put(var, varNewRule);
		}
		rules = newRules;
		for (String var: variables)
			PrintRule(var, rules.get(var));
	}

	private static HashSet<String> FindPossibleEpsilon() {
		HashMap<String, HashSet<ArrayList<String>>> rulesCoppied = new HashMap<String, HashSet<ArrayList<String>>>();
		for (String var : variables)
		{
			rulesCoppied.put(var, rules.get(var));
		}
		
		HashSet<String> epsiVariables = new HashSet<String>();
		boolean found = true;
		while (found)
		{
			found = false;
			for (String var : variables)
			{
				HashSet<ArrayList<String>> varRules = rulesCoppied.get(var);
				for (ArrayList<String> varRule : varRules)
				{
					if ((varRule.size() == 1) && (varRule.get(0).equals("Epsi")) && (var != oldStarting) && (!epsiVariables.contains(var)))
					{
						epsiVariables.add(var);
						found = true;
					}
				}
			}

			for (String var : variables)
			{
				HashSet<ArrayList<String>> varRules = rulesCoppied.get(var);
				HashSet<ArrayList<String>> varNewRules = new HashSet<ArrayList<String>>();
				for (ArrayList<String> varRule : varRules)
				{
					ArrayList<String> varNewRule = new ArrayList<String>();
					for (String unit : varRule)
						if (!epsiVariables.contains(unit))
							varNewRule.add(unit);
					if (var.equals("B2"))
						System.out.println("Funny");
					if (varNewRule.isEmpty())
						varNewRule.add("Epsi");
					varNewRules.add(varNewRule);
				}
				rulesCoppied.remove(var);
				rulesCoppied.put(var, varNewRules);
			}
//			for (String epsiVar : epsiVariables)
//			{
//				rulesCoppied.remove(epsiVar);
//			}
		}
		return epsiVariables;
	}

	private static void NewStartingSymbol() {
		oldStarting = inFile.get(0).split("->")[0].trim();
		String newStarting = "S0";
		
		HashMap<String, HashSet<ArrayList<String>>> newRules = new HashMap<String, HashSet<ArrayList<String>>>();
		HashSet<String> newVariables = new HashSet<String>();
		
		newVariables.add(newStarting);
		newVariables.add(oldStarting);
		newRules.put(newStarting, rules.get(oldStarting));
		System.out.println("Introduce new S0 that takes place of old " + oldStarting);
		PrintRule(newStarting, newRules.get(newStarting));
		
		HashSet<ArrayList<String>> newStartingRules = new HashSet<ArrayList<String>>();
		ArrayList<String> newStartingRule = new ArrayList<String>();
		newStartingRule.add(newStarting);
		newStartingRules.add(newStartingRule);
		newRules.put(oldStarting, newStartingRules);
		System.out.println("New startinng rule: ");
		PrintRule(oldStarting, newRules.get(oldStarting));
		
		System.out.println("Replace old rules with " + oldStarting + " by new " + newStarting);
		for (String var: variables)
		{
			boolean changed = false;
			if (!var.equals(oldStarting))
			{
				newVariables.add(var);
				HashSet<ArrayList<String>> varRules = rules.get(var);
				HashSet<ArrayList<String>> varNewRules = new HashSet<ArrayList<String>>();

				for (ArrayList<String> oldSingleRule : varRules)
				{
					ArrayList<String> newSingleRule = new ArrayList<String>();
					for (String unit : oldSingleRule)
						if (unit.equals(oldStarting))
						{
							changed = true;					
							newSingleRule.add(newStarting);
						}
						else
							newSingleRule.add(unit);
					varNewRules.add(newSingleRule);
				}

				newRules.put(var, varNewRules);
				if (changed)
					PrintRule(var, varNewRules);
			}
		}
		
		
		variables = newVariables;
		rules = newRules;
	}

	private static void NoMoreLongRules() throws Exception {
		HashMap<String, HashSet<ArrayList<String>>> newRules = new HashMap<String, HashSet<ArrayList<String>>>();
		HashSet<String> newVariables = new HashSet<String>();
		for (String s: variables)
		{
			newVariables.add(s);
		}
		boolean noMoreLongRules = true;
		for (String var : variables)
		{
			newVariables.add(var);
			HashSet<ArrayList<String>> varRules = rules.get(var);
			HashSet<ArrayList<String>> varNewRules = new HashSet<ArrayList<String>>();
			for (ArrayList<String> singleRule : varRules)
			{
				if (singleRule.size() > 2)	// rule that contains more than 2 units (terminal/variable)
				{
					noMoreLongRules = false;
					
					String newVar = NextAvailableVariable(newVariables);
					newVariables.add(newVar);
					HashSet<ArrayList<String>> newVarRule = new HashSet<ArrayList<String>>();
					ArrayList<String> newRule1 = new ArrayList<String>();
					newRule1.add(singleRule.get(0));
					newRule1.add(singleRule.get(1));
					newVarRule.add(newRule1);
					System.out.print("New rule: ");
					PrintRule(newVar, newRule1);
					newRules.put(newVar, newVarRule);
					
					ArrayList<String> newRule2 = new ArrayList<String>();
					newRule2.add(newVar);
					for (int i = 2; i < singleRule.size(); i++)
					{
						newRule2.add(singleRule.get(i));
					}
					System.out.print("Update old rule: ");
					PrintRule(var, newRule2);
					varNewRules.add(newRule2);
				}
				else
					varNewRules.add(singleRule);
			}
			newRules.put(var, varNewRules);
		}
		
		variables = newVariables;
		rules = newRules;
		if (!noMoreLongRules)
			NoMoreLongRules();
	}

	private static String NextAvailableVariable(HashSet<String> variablesList) throws Exception {
		String nextVar;
		for (char a = 'A'; a <= 'Z'; a++)
		{
			for (char n = '1'; n <= '9'; n++)
			{
				char[] cc = {a,n};
				nextVar = new String(cc);
				if ((!variablesList.contains(nextVar))&&(nextVar != "S0"))
					return nextVar;
			}
		}
		throw new Exception("Out of variable");
	}

	private static void VariablesFromTerminal() {
		HashMap<String, HashSet<ArrayList<String>>> newRules = new HashMap<String, HashSet<ArrayList<String>>>();
		HashSet<String> newVariables = new HashSet<String>();
		System.out.println("Rules from terminal: ");
		for (String term : terminals)
		{
			String newVar = term.toUpperCase().trim() + "0";
			HashSet<ArrayList<String>> rulesForNewVar = new HashSet<ArrayList<String>>();
			String[] tempArr = {term};
			rulesForNewVar.add(new ArrayList<String>(Arrays.asList(tempArr)));
			System.out.println(newVar + "->" + term);
			newRules.put(newVar, rulesForNewVar);
			newVariables.add(newVar);
		}
		
		for (String var : variables)
		{
			newVariables.add(var);
		}
		
		System.out.println("Update old rules:");
		
		for (String var : variables)
		{
			
				HashSet<ArrayList<String>> newVarRules = new HashSet<ArrayList<String>>();
				HashSet<ArrayList<String>> varRules = rules.get(var);
				boolean changed = false;
				for (ArrayList<String> singleRule : varRules)
				{
					ArrayList<String> newSingleRule = new ArrayList<String>();
					for (int i = 0; i < singleRule.size(); i++)
						if (terminals.contains(singleRule.get(i)))
						{
							changed = true;
							newSingleRule.add(singleRule.get(i).toUpperCase() + "0");
						}
						else
							newSingleRule.add(singleRule.get(i));
					newVarRules.add(newSingleRule);
					if (changed)
					{
						PrintRule(var, newSingleRule);
					}
				}
				newRules.put(var, newVarRules);
		}
		
		rules = newRules;
		variables = newVariables;
	}
	
//	private static String CharToString(char c) {
//		char[] cc = {c};
//		String s = new String(cc);
//		return s;
//	}
	
	private static void PrintRule(String root, ArrayList<String> product)
	{
		System.out.print(root + "->");
		for (int i = 0; i < product.size(); i++)
		{
			System.out.print(product.get(i) + " ");
		}
		System.out.println();
	}

	private static void PrintRule(String root, HashSet<ArrayList<String>> products)
	{
		for (ArrayList<String> product : products)
		{
			PrintRule(root, product);
		}
	}
}
