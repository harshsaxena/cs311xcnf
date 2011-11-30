import java.io.*;
import java.util.*;

public class translator {
	private static HashMap<String,HashSet<String>> rules = new HashMap<String,HashSet<String>>();
	private static HashSet<String> variables = new HashSet<String>();
	private static HashSet<String> terminals = new HashSet<String>();
	private static ArrayList<String> inFile = new ArrayList<String>();

	public static void main(String[] args) {
		try{
			  FileInputStream fstream = new FileInputStream("C:\\Users\\Onirz\\workspace\\CNF Translator\\bin\\input.txt");
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine;
			  System.out.println("Reading inputfile for rules: ");
			  
			  while ((strLine = br.readLine()) != null)   {
				  inFile.add(strLine);
				  String[] rootAndProduct = strLine.split("->");
				  variables.add(rootAndProduct[0].trim());
				  String[] individualProduct = rootAndProduct[1].split("-");
				  HashSet<String> productsFromSameRoot = new HashSet<String>();
				  
				  for (String s : individualProduct)
				  {
					  String[] productUnit = s.split(" ");
					  String singleRule = new String();
					  for (String s1 : productUnit)
					  {
						  if (s1.length() == 1)
							  terminals.add(s1);
						  else if (s1.length() == 2) 
							  variables.add(s1);
						  singleRule += s1 + " ";
					  }
					  productsFromSameRoot.add(singleRule.trim());					  
				  }
				  rules.put(rootAndProduct[0].trim(), productsFromSameRoot);
				  for (String rule : productsFromSameRoot)
				  {
					  System.out.println(rootAndProduct[0] + "->" + rule);
				  }
			  }
			  in.close();
			  
			  VariablesFromTerminal();
			  NoMoreLongRules();
			  NewStartingSymbol();
			  RemoveEpsilon();
			  RemoveUnitRules();
			  
			  
			    }catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
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

	private static void NoMoreLongRules() {
		HashMap<String, HashSet<String>> newRules = new HashMap<String, HashSet<String>>();
		HashSet<String> newVariables = new HashSet<String>();
		boolean noMoreLongRules = true;
		for (String var : variables)
		{
			newVariables.add(var);
			HashSet<String> varRules = rules.get(var);
			for (String singleRule : varRules)
			{
				if (singleRule.length() > 5)
				{
					noMoreLongRules = false;
				}
			}
		}
	}

	private static void VariablesFromTerminal() {
		HashMap<String, HashSet<String>> newRules = new HashMap<String, HashSet<String>>();
		HashSet<String> newVariables = new HashSet<String>();
		System.out.println("Rules from terminal: ");
		for (String term : terminals)
		{
			String newVar = term.toUpperCase().trim() + "0";
			HashSet<String> rulesForNewVar = new HashSet<String>();
			rulesForNewVar.add(term);
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
			
				HashSet<String> newVarRules = new HashSet<String>();
				HashSet<String> varRules = rules.get(var);
				boolean changed = false;
				for (String singleRule : varRules)
				{
					String newSingleRule = new String();
					for (int i = 0; i < singleRule.length(); i++)
						if (terminals.contains(CharToString(singleRule.charAt(i))))
						{
							changed = true;
							newSingleRule += CharToString(singleRule.charAt(i)).toUpperCase() + "0";
						}
						else
							newSingleRule += CharToString(singleRule.charAt(i));
					newVarRules.add(newSingleRule);
					if (changed)
						System.out.println(var + "->" + newSingleRule);
				}
				newRules.put(var, newVarRules);
		}
		
		rules = newRules;
	}
	
	private static String CharToString(char c) {
		char[] cc = {c};
		String s = new String(cc);
		return s;
	}

}
