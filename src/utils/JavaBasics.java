package utils;

import java.util.Collections;

public class JavaBasics
{
	public static <T> Iterable<T> nullSafe(Iterable<T> it)
	{
		return it != null ? it : Collections.<T> emptySet();
	}
}
