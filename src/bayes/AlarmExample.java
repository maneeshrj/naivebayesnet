package bayes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlarmExample implements BayesTestable {
	
	private BayesNetwork net;
	
	public AlarmExample(BayesNetwork net) {
		this.net = net;
		net.setName("Alarm Example");
		net.addVariable("B", 
						new String[] {}, 
						new Double[][]
						{
							{0.999, 0.001}
						});
		net.addVariable("E", 
						new String[] {}, 
						new Double[][]
						{
							{0.998, 0.002}
						});
		net.addVariable("J", 
						new String[] {"A"}, 
						new Double[][]
						{
							{0.95, 0.05}, 
							{0.1, 0.9}	
						});
		net.addVariable("M", 
						new String[] {"A"}, 
						new Double[][]
						{
							{0.99, 0.01}, 
							{0.3, 0.7}	
						});
		net.addVariable("A", 
						new String[] {"B", "E"}, 
						new Double[][]
						{
							{0.999, 0.001},
							{0.71, 0.29},
							{0.06, 0.94},
							{0.05, 0.95} 
						});
		
		net.createGraph();
	}

	@Override
	public void test() {
		Map<String, String> evidence = new HashMap<String, String>();
		evidence.put("B", "true");
		evidence.put("E", "false");
		String query = "J";
		List<Double> prob = net.ask(query, evidence);
		
		System.out.println("Given evidence:");
		for (String e : evidence.keySet()) {
			System.out.println("\t" + e + " = " + evidence.get(e));
		}
		
		// Given B=true and E=false, P(J=true) should be 0.849 and P(J=false) should be 0.151
		System.out.println("Probability distribution of " + query + ":");
		System.out.println("\ttrue = " + prob.get(1));
		System.out.println("\tfalse = " + prob.get(0));
	}
	
	@Override
	public String getName() {
		return net.getName();
	}
	
}
