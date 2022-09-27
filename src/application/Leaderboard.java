package application;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public abstract class Leaderboard {
	public static void append(Long duration, Integer points) {
		try (PrintStream writer = new PrintStream(new FileOutputStream(MainFX.getLeaderboard(), true))) {
			writer.println(String.format("%d,%d", duration, points));
		} catch (IOException ioe) {
		}
	}

	public static List<String> getLeaderboard() {
		List<String> list = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(MainFX.getLeaderboard())))) {
			while (reader.ready()) {
				list.add(reader.readLine());
			}
		} catch (IOException ioe) {
		}
		sortLeaderboard(list);
		return list;
	}

	private static void sortLeaderboard(List<String> list) {
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
	
	public static void clear() {
		try (PrintStream writer = new PrintStream(new FileOutputStream(MainFX.getLeaderboard()))) {
		} catch (IOException ioe) {
		}
	}
}
