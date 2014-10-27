package Logic;

import java.util.*;

class TestNearMatchSearch {
	public static void main(String[] agrs) {

		ArrayList<String> input = new ArrayList<String>();
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter number of task: ");
		int numOfStr = sc.nextInt();
		sc.nextLine();

		for (int i = 0; i < numOfStr; i++) {
			String str = sc.nextLine();
			input.add(str);
		}

		while (true) {
			System.out.println("Enter the key for searching: ");
			String key = sc.nextLine();
			ArrayList<String> result = nearMatchSearch(key, input);
			System.out.println("Result for searching '" + key + "'");
			for (int i = 0; i < result.size(); i++) {
				System.out.println(result.get(i));
			}

		}

	}

	private static int editDistance(String sourceString, String destString) {
		int sourceStrLen = sourceString.length();
		int destStrLen = destString.length();

		// sourceString in for vertical axis
		// destString in the horizontal axis
		int[][] editDist = new int[sourceStrLen + 1][destStrLen + 1];

		for (int i = 1; i <= sourceStrLen; i++) {
			editDist[i][0] = i;
		}

		for (int j = 1; j <= destStrLen; j++) {
			editDist[0][j] = j;
		}

		for (int j = 1; j <= destStrLen; j++) {
			for (int i = 1; i <= sourceStrLen; i++) {
				if (sourceString.charAt(i - 1) == destString.charAt(j - 1))
					editDist[i][j] = editDist[i - 1][j - 1];
				else {
					editDist[i][j] = Math
							.min(editDist[i - 1][j] + 1, Math.min(
									editDist[i][j - 1] + 1,
									editDist[i - 1][j - 1] + 1));
				}
			}
		}

		return editDist[sourceStrLen][destStrLen];
	}

	private static Pair searchScore(String keyword, String strToSearch) {
		String[] key = keyword.trim().split("\\s+");
		int strLen = key.length;
		int score = 0;
		int count = 0;
		for (int i = 0; i < strLen; i++) {
			if (isMatch(key[i], strToSearch) != 0)
				count++;
			score += isMatch(key[i], strToSearch);
		}

		return new Pair(count, score);
	}

	// keyword is one word only
	// return 1 if the key appears exactly or approximately in the strToSearch
	// 0 otherwise
	private static int isMatch(String key, String strToSearch) {

		String[] string = strToSearch.trim().split("\\s+");
		int strLen = string.length;
		int max = 0;

		for (int i = 0; i < strLen; i++) {
			int score = approximateMatchScore(key, string[i]);
			if (score > max)
				max = score;
		}

		return max;
	}

	// Criteria to be matched between 2 words, if the
	// editDistance/lenghOfKeyWord is <=0.5
	// 2 strings are considered approximately match
	// return the matchScore
	private static int approximateMatchScore(String keyword, String string) {
		int editDist = editDistance(keyword, string);
		int lenOfKey = keyword.length();

		if (editDist * 1.0 / lenOfKey <= 0.5)
			return 1000 - 1000 * editDist / lenOfKey;
		else
			return 0;

	}

	private static ArrayList<String> nearMatchSearch(String key,
			ArrayList<String> input) {
		ArrayList<String> resultList = new ArrayList<String>();
		ArrayList<Triple> list = new ArrayList<Triple>();

		int numOfTask = input.size();
		String[] str = key.trim().split("\\s+");

		for (int i = 0; i < numOfTask; i++) {
			String task = input.get(i);
			Pair result = searchScore(key, task);
			if (result.getFirst() > str.length / 2
					|| (str.length == 2 && result.getFirst() >= 1)) {
				list.add(new Triple(result.getFirst(), result.getSecond(), task));
			}
		}

		Collections.sort(list);

		for (int i = list.size() - 1; i >= 0; i--) {
			String task = list.get(i).getThird();
			resultList.add(task);
		}

		return resultList;

	}

}

class Triple implements Comparable<Triple> {
	Integer first;
	Integer second;
	String third;

	public Triple(Integer _first, Integer _second, String _third) {
		first = _first;
		second = _second;
		third = _third;
	}

	public Integer getFirst() {
		return first;
	}

	public Integer getSecond() {
		return second;
	}

	public String getThird() {
		return third;
	}

	public int compareTo(Triple triple) {
		if ((int) this.getFirst() != (int) triple.getFirst())
			return (int) this.getFirst() - (int) triple.getFirst();
		else
			return (int) this.getSecond() - (int) triple.getSecond();
	}

}

class Pair {
	Integer first;
	Integer second;

	public Pair(Integer _first, Integer _second) {
		first = _first;
		second = _second;
	}

	public Integer getFirst() {
		return first;
	}

	public Integer getSecond() {
		return second;
	}

}
