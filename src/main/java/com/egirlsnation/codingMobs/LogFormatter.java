package com.egirlsnation.codingMobs;

public class LogFormatter {

	// ANSI escape code
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	public static enum priority {
		LOW, MEDIUM, HIGH
	}

	public static String format(priority priority, String header, String message) {

		StringBuilder builder = new StringBuilder();

		switch (priority) {

		case LOW:
			builder.append("[");
			builder.append(ANSI_GREEN);
			builder.append(header);
			builder.append(ANSI_RESET);
			builder.append("] ");
			break;
		case MEDIUM:
			builder.append("[");
			builder.append(ANSI_YELLOW);
			builder.append(header);
			builder.append(ANSI_RESET);
			builder.append("] ");
			break;
		case HIGH:
			builder.append("[");
			builder.append(ANSI_RED);
			builder.append(header);
			builder.append(ANSI_RESET);
			builder.append("] ");
			break;

		}

		builder.append(message);

		return builder.toString();

	}

}
