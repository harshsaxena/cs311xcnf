import java.io.*;
import java.util.*;

public class translator {
	private static HashMap<String,HashSet<ArrayList<String>>> rules = new HashMap<String,HashSet<ArrayList<String>>>();
	private static HashSet<String> variables = new HashSet<String>();
	private static HashSet<String> terminals = new HashSet<String>();
	private static ArrayList<String> inFile = new ArrayList<String>();

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
			  NewStartingSymbol();
			  RemoveEpsilon();
			  RemoveUnitRules();
			  
			  
			    }catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.toString());
			  }
	}

	private static void RemoveUnitRules() {
		// TODO Auto-generated method stub
		
	}

	private static void RemoveEpsilon() {
		// TODO Auto-generated method stub
		
	}

	private static void NewStartingSymbol() {
		// TODO Auto-generated method stub
		
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
				if (!variablesList.contains(nextVar))
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
