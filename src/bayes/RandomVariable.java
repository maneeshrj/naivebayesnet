package bayes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class RandomVariable implements BayesNode {
	
	private String name;
	private List<String> domain;
	public List<BayesNode> parents;
	public List<BayesNode> children;
	public List<List<Double>> probabilities;
	public boolean isRoot = false;
	
	public RandomVariable(String name) {
		this.name = name;
		domain = Arrays.asList("false", "true");
		parents = new ArrayList<BayesNode>();
		children = new ArrayList<BayesNode>();
		probabilities = new ArrayList<List<Double>>();
	}
	
	public RandomVariable(String name, List<String> domain) {
		this.name = name;
		this.domain = domain;
		parents = new ArrayList<BayesNode>();
		children = new ArrayList<BayesNode>();
		probabilities = new ArrayList<List<Double>>();
	}
	
	@Override
	public Double getProbability(String outcome, Map<String, String> givens) {
		int numParents = parents.size();
		int row = 0;
		int col = 0;

		if(outcome.equals(domain.get(1))) col = 1;
		
		if(isRoot) return probabilities.get(0).get(col);
		
		for(int i = 0; i < numParents; i++) {
			BayesNode currParent = parents.get(i);
			String parentOutcome = givens.get(currParent.getName());
			int bitVal = 0;
			
			if(parentOutcome.equals(currParent.getDomain().get(1))) bitVal = 1;
			row += bitVal * Math.pow(2, (numParents-1 - i));
		}
		
		return probabilities.get(row).get(col);
	}
	
	@Override
	public void addChild(BayesNode child) {
		children.add(child);
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<BayesNode> getParents() {
		return parents;
	}

	@Override
	public List<BayesNode> getChildren() {
		return children;
	}

	@Override
	public List<String> getDomain() {
		return domain;
	}
	
}
