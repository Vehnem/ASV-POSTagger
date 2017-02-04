package org.asv.postagger;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Test;

public class EvalTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void test() throws IOException {
		Tagset tagset = new Tagset();
		
		tagset.removeTags("./test_test", "./test_test_wot", "/");
		
		Tagger tagger = new Tagger();
		
		tagger.tagfile("/test.RDR","./test.DICT", "/test_test_wot");
		
		Evaluation evaluation = new Evaluation();
		HashMap<String, Long> map = new HashMap<String, Long>();
		map = evaluation.evaluate("./test_test", "test_test_wot.TAGGED");
		
		float fa = map.get("false");
		float all = map.get("all");
		
		float per = 1 - (fa / all);
		
		System.out.println(per);
	}

}
