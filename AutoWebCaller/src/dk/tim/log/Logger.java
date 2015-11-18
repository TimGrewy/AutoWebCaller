package dk.tim.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import dk.tim.file.FileTool;

public class Logger {
	SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public static Logger systemLog;

	private String logFilePath;
	private BufferedWriter out;
	private long MAX_LOGFILE_SIZE = 10485760; //10485760 = 10 mb

	public Logger(String logFile) {
		this.logFilePath = logFile;
		initialize();
		if (isValidLogFile()) {
			cleanupIfFileIsTooBig(MAX_LOGFILE_SIZE);
		}
	}

	private boolean isValidLogFile() {
		return logFilePath != null && !logFilePath.equals("");
	}

	private void initialize() {
		try {
			if (isValidLogFile()) {
				FileWriter fstream = new FileWriter(logFilePath, true);
				out = new BufferedWriter(fstream);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void log(String x) {
		logStatement(x);
	}

	public static void logToSystemLog(String x) {
		systemLog.logStatement(x);
	}

	public static void logToSystemLogAndSystemOut(String x) {
		systemLog.logStatement(x);
		System.out.println(x);
	}

	private void logStatement(String statement) {
		try {
			String timestamp = createTimeStamp();

			String decoratedStatement = String.format("[%s] : %s", timestamp, statement);
			if (isValidLogFile()) {
				String lineSeperator = System.getProperty("line.separator");
				;
				out.append(decoratedStatement + lineSeperator);
			}
			System.out.println(decoratedStatement);

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (isValidLogFile()) {
					out.flush();
				}
			} catch (IOException e) {
				System.err.println(e);
				e.printStackTrace();
			}

		}
	}

	public static void closeLogger() {
		try {
			systemLog.internalCloseLogger();
		} catch (Exception e) {
			System.out.println("Failed to close logger. " + e.getMessage());
		}
	}

	private void internalCloseLogger() {
		try {
			if (isValidLogFile()) {
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void cleanupIfFileIsTooBig(long maxSize) {
		try {
			File logFile = new File(logFilePath);
			FileTool fileTool = new FileTool();
			long fileByteSize = fileTool.getFileByteSize(logFile);
			if (fileByteSize > maxSize) {
				clearLogFile(logFile, fileTool);
			}
		} catch (Exception e) {

		}
	}

	private void clearLogFile(File logFile, FileTool fileTool) {
		try {
			FileWriter fstreamNoAppend = new FileWriter(logFilePath, false);
			BufferedWriter clearLogFileStream = new BufferedWriter(fstreamNoAppend);
			out.flush();
			clearLogFileStream.close();

			System.out.println("Log cleared");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * Flushes the stack trace to a writer and returns the writer as a String
	 */
	public static String parseStackTraceToString(Exception e) {
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		e.printStackTrace(printWriter);
		printWriter.flush();
		return writer.toString();
	}

	private String createTimeStamp() {
		Locale locale = new Locale("da", "DK");
		Calendar cal = new GregorianCalendar(locale);
		Date date = cal.getTime();
		return df.format(date);
	}
}
