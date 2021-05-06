package bayes;

import java.util.List;

public class BayesTest {

	public static void main(String[] args) {
		BayesNetwork net = new BayesNetwork(new InferenceByEnumeration());
		
		// Replace StrokeExample with WetGrassExample or AlarmExample to test those networks
		BayesTestable example = new StrokeExample(net);
		
		System.out.println();
		// Prints each root and its children
		printGraph(net);
		
		List<BayesNode> nodes = net.getNodesInTopologicalOrder();
		// Prints all nodes in topological order
		printTopological(nodes);
		
		System.out.println("BayesNetwork Demo - " + example.getName());
		example.test();
	}
	
	public static void printTopological(List<BayesNode> nodes) {
		System.out.println("Nodes in topological order:");
		System.out.print("[");
		for(BayesNode node : nodes)
		{
			System.out.print(" " + node.getName() + " ");
		}
		System.out.println("]\n");
	}
	
	public static void printGraph(BayesNetwork net) {
		System.out.println("Nodes in network:");
		for(BayesNode root : net.getRootNodes()) {
			net.printNodes(root, 0);
		}
		System.out.println();
	}
	
}