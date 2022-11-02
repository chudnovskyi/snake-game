package application;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public abstract class Scoreboard {
	public static void append(Long duration, Integer points) {
		try (PrintStream writer = new PrintStream(new FileOutputStream(MainFX.getScoreboardFile(), true))) {
			writer.println(String.format("%d,%d", duration, points));
		} catch (IOException ioe) {
		}
	}

	public static String[] getScoreboard() {
		List<String> scoreboard = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(MainFX.getScoreboardFile())))) {
			while (reader.ready()) {
				scoreboard.add(reader.readLine());
			}
		} catch (IOException ioe) {
		}
		sortScoreboard(scoreboard);
		return parseToArray(scoreboard);
	}
	
	private static void sortScoreboard(List<String> list) {
		list.sort((point1, point2) -> {
			String[] arr1 = point1.split(",");
			String[] arr2 = point2.split(",");
			long time1 = Long.parseLong(arr1[0]);
			long time2 = Long.parseLong(arr2[0]);
			int amount1 = Integer.parseInt(arr1[1]);
			int amount2 = Integer.parseInt(arr2[1]);
			if (amount1 > amount2)
				return -1;
			else if (amount1 < amount2)
				return 1;
			else {
				if (time1 > time2)
					return 1;
				else if (time1 < time2)
					return -1;
				else
					return 0;
			}
		});
	}
	
	private static String[] parseToArray(List<String> scoreboard) {
		String[] newScoreboard = new String[scoreboard.size()];
		int i = 0;
		for (String result : scoreboard) {
			String[] term = result.split(",");
			long ms = Long.parseLong(term[0]);
			int points = Integer.parseInt(term[1]);
			newScoreboard[i] = String.format("%d points | %d.%d sec", points, ms / 1000, (ms % 1000) / 100);
			i++;
		}
		return newScoreboard;
	}

	public static void clear() {
		try (PrintStream writer = new PrintStream(new FileOutputStream(MainFX.getScoreboardFile()))) {
		} catch (IOException ioe) {
		}
	}
}
