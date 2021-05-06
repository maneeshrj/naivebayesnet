package bayes;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public class StrokeExample implements BayesTestable {
	
	private BayesNetwork net;
	private List<Map<String, String>> testData;
	private StrokeDataLoader dataloader;
	
	public StrokeExample(BayesNetwork net) {
		this.net = net;
		net.setName("Stroke Example");
		
		try {
			dataloader = new StrokeDataLoader();
			dataloader.load("healthcare-dataset-stroke-data.csv", net);
			testData = dataloader.testRecords;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
		
		net.createGraph();
	}

	@Override
	public void test() {
		for(int i = 0; i < testData.size(); i++) {
			System.out.println("\nSubject " + i + ":");
			Map<String, String> evidence = testData.get(i);
			
			System.out.println("Given evidence:");
			for (String e : evidence.keySet()) {
				System.out.print(e + "=" + evidence.get(e) + "    ");
			}
			
			
			for (String var : evidence.keySet()) {
				String val = evidence.get(var);
				val = dataloader.ones.stream().anyMatch(val::equalsIgnoreCase) ? "true" : dataloader.zeroes.stream().anyMatch(val::equalsIgnoreCase) ? "false" : null;
				
				evidence.put(var, val);
			}
			
			String query = "stroke";
			List<Double> prob = net.ask(query, evidence);
			System.out.println("\nProbability distribution of " + query + ":");
			System.out.println("\tfalse = " + prob.get(0));
			System.out.println("\ttrue = " + prob.get(1));
			System.out.println("True value of stroke=" + dataloader.testLabels.get(i));
		}	
	}
	
	@Override
	public String getName() {
		return net.getName();
	}
	
}

