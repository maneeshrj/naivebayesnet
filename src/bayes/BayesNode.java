package bayes;

import java.util.List;
import java.util.Map;

public interface BayesNode {
	public String getName();
	public List<String> getDomain();
	public Double getProbability(String outcome, Map<String, String> givens);
	public void addChild(BayesNode child);
	public List<BayesNode> getParents();
	public List<BayesNode> getChildren();
}
