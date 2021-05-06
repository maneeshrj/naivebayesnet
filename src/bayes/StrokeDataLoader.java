package bayes;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class StrokeDataLoader {
	
	List<String> variables = Arrays.asList("gender", "hypertension", "heart_disease", "ever_married", "Residence_type");
	List<String> ones    = Arrays.asList("1", "true", "yes", "Urban", "Male");
	List<String> zeroes  = Arrays.asList("0", "false", "no", "Rural", "Female");
	
	List<Map<String, String>> testRecords;
	List<String> testLabels;
	
	Map<String, List<String>> domains;
	
	Map<String, Map<String, Integer>> counts = new HashMap<String, Map<String, Integer>>();
	
	public void load(String file, BayesNetwork net) throws FileNotFoundException {
		System.out.println("Loading Stroke Prediction Dataset...");
		
		Reader in = new FileReader(file);
		testRecords = new ArrayList<>();
		testLabels = new ArrayList<>();
		
		Iterable<CSVRecord> records = null;
		try {
			records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Map<String, Integer> lbl0Count = new HashMap<>();
		Map<String, Integer> lbl1Count = new HashMap<>();
		
		for(int i = 0; i < Math.pow(2, variables.size()); i++) {
			lbl0Count.put(String.valueOf(i), 0);
			lbl1Count.put(String.valueOf(i), 0);
		}
		
		counts.put("stroke0", lbl0Count);
		counts.put("stroke1", lbl1Count);
		
		for (CSVRecord record : records) {
			if(testRecords.size() < 10) {
				if(testRecords.size() < 5 && record.get("stroke").equals("1")) {
					Map<String, String> recordMap = new HashMap<>();;
					for(String v : variables) {
						recordMap.put(v, record.get(v));
					}
					testRecords.add(recordMap);
					testLabels.add(record.get("stroke"));
					continue;
				} else if(testRecords.size() < 10 && record.get("stroke").equals("0")) {
					Map<String, String> recordMap = new HashMap<>();;
					for(String v : variables) {
						recordMap.put(v, record.get(v));
					}
					testRecords.add(recordMap);
					testLabels.add(record.get("stroke"));
					continue;
				}
			}
			
			int lblKey = 0;
			for(int j = 0; j < variables.size(); j++) {
				String rv = variables.get(j);
				String val = record.get(rv);
				if(val != null) {
					val = val.trim();
					if(ones.stream().anyMatch(val::equalsIgnoreCase)) {
						val = "1";
					} else if(zeroes.stream().anyMatch(val::equalsIgnoreCase)) {
						val = "0";
					} else {
						val = null;
					}
				}
				
				if(val == null) {
					lblKey = -1;
					continue;
				}
				
				if(val != null) {
					lblKey += Integer.parseInt(val) * Math.pow(2, (variables.size() - 1 - j));
					
					Map<String, Integer> items = counts.get(rv);
					if(items == null) {
						items = new HashMap<String, Integer>();
						counts.put(rv, items);
					}
					Integer count = items.get(val);
					if(count == null) {
						count = Integer.valueOf(0);
					}
					items.put(val, (count + 1));
					
				}
		    	}
			
			if(lblKey == -1) continue;
			String lblVal = record.get("stroke");
			lblVal = lblVal == null ? null : lblVal.trim();
			Map<String, Integer> currLblCount = counts.get("stroke" + lblVal);
			
			Integer currCount = currLblCount.get(String.valueOf(lblKey));
			currLblCount.put(String.valueOf(lblKey), currCount + 1);
		}
		
		// Calculating probability distributions for feature variables
		System.out.println("\nFeatures:");
		for (String key : variables) {
		    System.out.println(key);
		    
		    double total = Integer.valueOf(0);
		    for (Entry<String, Integer> entry : counts.get(key).entrySet()) {
		    	total += entry.getValue();
		    }
		    
		    List<Double> probs = new ArrayList<Double>();
		    for (Entry<String, Integer> entry : counts.get(key).entrySet()) {
		    	System.out.println("  Value " + entry.getKey() + " : count=" + entry.getValue().toString() + ", prob=" + entry.getValue()/total);
		    	probs.add(entry.getValue()/total);
		    }

			net.addRootVariable(key, probs);
		}
		
		System.out.println("\nLabel:");
		for(int i = 0; i < 2; i++) {
			String key = "stroke" + i;
			int sum = 0;
			for (int j = 0; j < Math.pow(2, variables.size()); j++) {
				String entryKey = String.valueOf(j);
				int entryCount = counts.get(key).get(entryKey);
		    	sum += entryCount;
		    }
			System.out.println(key + " count=" + sum);			
		}
		
		Double[][] probs = new Double[(int)Math.pow(2, variables.size())][2]; 
		for(int i = 0; i < Math.pow(2, variables.size()); i++) {
			int total = 0;
			for(int j = 0; j < 2; j++) {
				String key = "stroke" + j;
				String entryKey = String.valueOf(i);
				total += counts.get(key).get(entryKey);
			}
			for(int k = 0; k < 2; k++) {
				String key = "stroke" + k;
				String entryKey = String.valueOf(i);
				probs[i][k] = counts.get(key).get(entryKey) / (double)total;
			}
		}
		String[] vars = variables.toArray(new String[0]);			
		net.addVariable("stroke", vars, probs);
	}
	
	public static void main(String[] args) {
		// Tests data loader without testing Bayesian network
		try {
			BayesNetwork net = new BayesNetwork(null);
			StrokeDataLoader loader = new StrokeDataLoader();
			loader.load("healthcare-dataset-stroke-data.csv", net);
			System.out.println("\nTest subjects selected: " + loader.testRecords.size());
			
			System.out.println("\nBayes network graph:");
			net.createGraph();
			for(BayesNode root : net.getRootNodes()) {
				net.printNodes(root, 0);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
