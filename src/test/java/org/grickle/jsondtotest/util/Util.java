package org.grickle.jsondtotest.util;

import java.io.BufferedReader;
import java.io.IOException;

public class Util {
	public static String ReadEntireBuffer(BufferedReader reader) throws IOException {
		StringBuilder builder = new StringBuilder();
		String aux = "";
		while ((aux = reader.readLine()) != null) {
			builder.append(aux);
		}
		return builder.toString();
	}
}
