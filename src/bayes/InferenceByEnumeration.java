package bayes;
import java.util.*;

public class InferenceByEnumeration implements InferenceAlgorithm {
	
	private static List<Double> enumerateAsk(String query, Map<String, String> evidence, BayesNetwork net) {
		RandomVariable queryVar = net.getVariable(query);
		List<Double> queryProbability = new ArrayList<Double>();
		
		List<BayesNode> nodes = net.getNodesInTopologicalOrder();
		
		for(int i = 0; i < 2; i++) {
			Map<String, String> extendedEvidence = new HashMap<String, String>(evidence);
			extendedEvidence.put(query, queryVar.getDomain().get(i));
			queryProbability.add(enumerateAll(nodes, extendedEvidence));
		}
		
		normalize(queryProbability);
		return queryProbability;
	}
	
	private static Double enumerateAll(List<BayesNode> variables, Map<String, String> evidence) {
		if(variables.isEmpty()) return 1.0;
		
		List<BayesNode> newVars = new ArrayList<>(variables);
		BayesNode currNode = newVars.remove(0);
		
		String currOutcome = evidence.get(currNode.getName()); // check if this node is part of evidence
		if(currOutcome != null) {
			// known evidence
			return currNode.getProbability(currOutcome, evidence) * enumerateAll(newVars, evidence);
		} else {
			// "hidden" random variable, enumerate over all possibilities
			Double sumHidden = 0.0;
			for(String possibleOutcome : currNode.getDomain()) {
				Double possibleProb = currNode.getProbability(possibleOutcome, evidence);
				
				Map<String, String> extendedEvidence = new HashMap<String, String>(evidence);
				extendedEvidence.put(currNode.getName(), possibleOutcome); // extending the evidence
				
				sumHidden += possibleProb * enumerateAll(newVars, extendedEvidence);
			}
			
			return sumHidden;
		}
	}
	
	public static List<Double> normalize(List<Double> probs) {
		// Takes a probability distribution and scales it so that the sum is 1.0
		Double sum = 0.0;
		for(Double prob : probs) {
			sum += prob;
		}
		
		for(int i = 0; i < probs.size(); i++) {
			probs.set(i, probs.get(i)/sum);
		}
		
		return probs;
	}

	@Override
	public List<Double> ask(String query, Map<String, String> evidence, BayesNetwork net) {
		return enumerateAsk(query, evidence, net);
	}
	
}
