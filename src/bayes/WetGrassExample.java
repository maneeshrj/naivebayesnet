package bayes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WetGrassExample implements BayesTestable {
	
	private BayesNetwork net;

	public WetGrassExample(BayesNetwork net) {
		this.net = net;
		net.setName("Wet Grass Example");
		net.addVariable("C", 
						new String[] {}, 
						new Double[][]
						{
							{0.5, 0.5}
						});
		net.addVariable("S", 
						new String[] {"C"}, 
						new Double[][]
						{
							{0.5, 0.5},
							{0.9, 0.1} 
							
						});
		net.addVariable("R", 
						new String[] {"C"}, 
						new Double[][]
						{
							{0.9, 0.1}, 
							{0.2, 0.8}
						});
		net.addVariable("W", 
						new String[] {"R", "S"}, 
						new Double[][]
						{
							{0.9, 0.1},
							{0.1, 0.9},
							{0.1, 0.9},
							{0.05, 0.95},
						});
		
		net.createGraph();
	}

	@Override
	public void test() {
		Map<String, String> evidence = new HashMap<String, String>();
		evidence.put("C", "true");
		evidence.put("R", "false");
		String query = "W";
		List<Double> prob = net.ask(query, evidence);
		
		System.out.println("Given evidence:");
		for (String e : evidence.keySet()) {
			System.out.println("\t" + e + " = " + evidence.get(e));
		}
		
		// Given C=true and R=false, P(W=true) should be 0.18 and P(W=false) should be 0.82
		System.out.println("Probability distribution of " + query + ":");
		System.out.println("\ttrue = " + prob.get(1));
		System.out.println("\tfalse = " + prob.get(0));
	}
	
	@Override
	public String getName() {
		return net.getName();
	}
	
}