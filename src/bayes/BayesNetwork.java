package bayes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BayesNetwork {
	private String name;
	private List<RandomVariable> rootNodes = new ArrayList<RandomVariable>();
	private Map<String, RandomVariable> nodeLookup = new HashMap<String, RandomVariable>();
	private InferenceAlgorithm algo;
	
	public BayesNetwork(InferenceAlgorithm algo) {
		this.algo = algo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<RandomVariable> getRootNodes() {
		return rootNodes;
	}

	public Map<String, RandomVariable> getLookupTable() {
		return nodeLookup;
	}

	void addVariable(String name, String[] givenVariables, Double[][] probs) {
		RandomVariable node = nodeLookup.get(name);
		if(node == null) {
			node = new RandomVariable(name); // create new node if it doesn't already exist
			nodeLookup.put(name, node);
		}
		
		if(givenVariables == null || givenVariables.length == 0) {
			rootNodes.add(node); // Node has no parents, must be a root
			node.isRoot = true;
		}
		
		for(String variable : givenVariables) {
			RandomVariable pnode = nodeLookup.get(variable);
			if(pnode == null) {
				pnode = new RandomVariable(variable);
				nodeLookup.put(variable, pnode);
			}
			node.parents.add(pnode);
		}
		
		for(Double[] ps : probs) {
			node.probabilities.add(Arrays.asList(ps));
		}
	}
	
	void addRootVariable(String name, List<Double> probs) {
		RandomVariable node = nodeLookup.get(name);
		if(node == null) {
			node = new RandomVariable(name); // create new node if it doesn't already exist
			nodeLookup.put(name, node);
		}
		
		rootNodes.add(node); // Node has no parents, must be a root
		node.isRoot = true;
		node.probabilities.add(probs);
	}
	
	RandomVariable getVariable(String name) {
		return nodeLookup.get(name);
	}
	
	void createGraph() {
		// Parents are given for each node when the network is created
		// However children are not given
		for(BayesNode node : nodeLookup.values()) {
			for(BayesNode parent : node.getParents()) {
				parent.addChild(node);
			}
		}
	}
	
	public List<BayesNode> getNodesInTopologicalOrder(){
		List<BayesNode> nodes = new ArrayList<BayesNode>();
		Map<String, Boolean> visitedNodeMap = new HashMap<String, Boolean>();

		for(BayesNode root : rootNodes) {
			nodeWalk(root, nodes, visitedNodeMap);
		}
		
		Collections.reverse(nodes);
		return nodes;
	}

	private void nodeWalk(BayesNode node, List<BayesNode> nodes, Map<String, Boolean> visitedNodeMap) {
		visitedNodeMap.put(node.getName(), true);
		for(BayesNode child : node.getChildren()) {
			if(!visitedNodeMap.containsKey(child.getName())){
				nodeWalk(child, nodes, visitedNodeMap);
			}
		}
		nodes.add(node);
	}
	
	public void printNodes(BayesNode node, int level) {
		// Recursively prints children of the given node
		String tabs = level > 0 ? "  ".repeat(level) : "";
		System.out.println(tabs + node.getName());
		level++;
		for(BayesNode child : node.getChildren()) {
			printNodes(child, level);
		}
	}
	
	public List<Double> ask(String query, Map<String, String> evidence) {
		return algo.ask(query, evidence, this);
	}
}