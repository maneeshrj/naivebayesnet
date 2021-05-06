package bayes;

import java.util.List;
import java.util.Map;

public interface InferenceAlgorithm {
	public List<Double> ask(String query, Map<String, String> evidence, BayesNetwork net);
}
