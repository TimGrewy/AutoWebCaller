package dk.tim.file;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReadFile {
	public String getFileContent(Path path) {
		try {
			String result = "";
			FileInputStream fstream = new FileInputStream(path.toString());
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				result += strLine;
			}
			in.close();
			return result;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public List<String> getLines(Path path) {
		try {
			List<String> result = new ArrayList<String>();
			FileInputStream fstream = new FileInputStream(path.toString());
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				result.add(strLine);
			}
			in.close();
			return result;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void removeLine(String lineContent, String path) throws IOException
	{
	    File file = new File(path);
	    List<String> out = Files.lines(file.toPath())
	                        .filter(line -> !line.contains(lineContent))
	                        .collect(Collectors.toList());
	    Files.write(file.toPath(), out, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
	}
	public static void addLine(String completedListLocation, String string) throws IOException {
		File file = new File(completedListLocation);
		Files.write(file.toPath(), Arrays.asList(string), StandardOpenOption.APPEND);
	}
}
