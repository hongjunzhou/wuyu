package org.wuyu.learning.lambda;

import java.util.Arrays;

public class One {

	public static void main(String[] args) {
		Arrays.asList("a", "b", "d").forEach((String e) -> System.out.println(e));
	}

}
