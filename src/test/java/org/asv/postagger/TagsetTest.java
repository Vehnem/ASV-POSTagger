package org.asv.postagger;

import java.io.File;

import org.junit.Test;

public class TagsetTest {

	@Test
	public void test() {
		File file = new File("test2/");
		
		System.out.println(file.getPath());
		
		if(file.exists()) {
			System.out.println("exist");
		} else {
			System.out.println("exists not");
		}
	}
}
