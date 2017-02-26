package org.asv.postagger;

import java.io.IOException;
import java.util.HashMap;
import org.junit.Test;

/**
 * Evaluation test class
 * 
 * @author marvin, robert
 *
 */
public class EvalTest {

	//params
	private static final String pathtogoldCorpus ="";
	private static final String pathtotaggedCorpus ="";
	
	@Test
	public void test() throws IOException {
		
		Evaluation evaluation = new Evaluation();
		HashMap<String, Long> map = new HashMap<String, Long>();
		map = evaluation.evaluate(pathtogoldCorpus, pathtotaggedCorpus);
		
		float fa = map.get("false");
		float all = map.get("all");
		
		float per = 1 - (fa / all);
		
		System.out.println(per);
	}
}
