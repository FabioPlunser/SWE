package at.ac.uibk.plant_health.util;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public class ArgumentGenerator {
		public static Stream<Arguments> booleans (int width) {
			return IntStream.range (0, (int) Math.pow (2, width))
				.mapToObj (Integer::toBinaryString)
				.map (s -> String.format ("%1$" + width + "s", s))
				.map (s -> {
					Boolean[] asBoolArray = new Boolean[width];
					for (int i = 0; i < width; i++) {
						asBoolArray[i] = i < s.length () && s.charAt (i) == '1';
					}
					return asBoolArray;
				})
				.map (
					b
					-> arguments (
						(Object[]) Arrays.stream (b).toArray (Boolean[] ::new)
					)
				);
		}
}
