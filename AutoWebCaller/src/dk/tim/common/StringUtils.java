package dk.tim.common;

public class StringUtils {

	public static boolean isEmpty(String s) {
		if (s == null || s.trim().equals("")) {
			return true;
		}
		return false;
	}

	public static boolean isNotEmpty(String s) {
		return !isEmpty(s);
	}
}
