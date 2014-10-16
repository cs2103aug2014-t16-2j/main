package Parser;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

public class Parser {

	private final List<String> addCommandWords = Arrays.asList("add", "schedule", "create", "remember");
	private final List<String> modifyCommandWords = Arrays.asList("modify", "edit", "reschedule", "change");
	private final List<String> deleteCommandWords = Arrays.asList("delete", "remove", "clear");
	private final List<String> searchCommandWords = Arrays.asList("display", "show", "find", "search");
	private final List<String> otherCommandWords = Arrays.asList("exit", "undo", "redo");
	private final List<String> dayWords = Arrays.asList("today", "tomorrow", "yesterday");
	private final List<String> timeOfDayWords = Arrays.asList("morning", "noon", "afternoon", "evening", "night", "midnight");
	private final List<String> dayOfWeekWords = Arrays.asList("monday","mon", "tuesday", "tue", "wednesday", "wed", "thursday", "thu", "friday", "fri", "saturday", "sat", "sunday", "sun");
	private final List<String> datePeriodWords = Arrays.asList("day", "week", "month", "year");
	private final List<String> dateTimeKeyWords = Arrays.asList("this", "next", "by", "last");
	private final List<String> moreDateTimeKeyWords = Arrays.asList("after", "before");
	private final List<String> monthWords = Arrays.asList("jan", "january", "feb", "febuary", "mar", "march", "apr", "april", "may", "jun", "june", "jul", "july", "aug", "august", "sep", "september", "oct", "october", "nov", "november", "dec", "december");
	private final List<String> ordinalNumWords = Arrays.asList("st", "nd", "rd" ,"th");
	private final List<String> timeWords = Arrays.asList("am", "pm");
	private final List<String> uselessWords = Arrays.asList("the", "on", "from", "to", "@", "at");
	
	public Action getAction(String input) {
		
		MyStringList words = new MyStringList();
		words.addAll(Arrays.asList(input.split(" ")));
		return new Action(getCommand(words), getTask(words));
		
	}
	
	private String getCommand(MyStringList words) {
		
		for (String c : addCommandWords) {
			if (words.containsIgnoreCase(c)) {
				words.removeIgnoreCase(c);
				return "add";
			}
		}
		for (String c : modifyCommandWords) {
			if (words.containsIgnoreCase(c)) {
				words.removeIgnoreCase(c);
				return "modify";
			}
		}
		for (String c : deleteCommandWords) {
			if (words.containsIgnoreCase(c)) {
				words.removeIgnoreCase(c);
				return "delete";
			}
		}
		for (String c : searchCommandWords) {
			if (words.containsIgnoreCase(c)) {
				words.removeIgnoreCase(c);
				return "search";
			}
		}
		for (String c : otherCommandWords) {
			if (words.containsIgnoreCase(c)) {
				words.removeIgnoreCase(c);
				return c;
			}
		}
		return "add";
		
	}
	
	private Task getTask(MyStringList words) {
		
		Task t = new Task();
		setDateTime(words, t);
		setContent(words, t);
		return t;
		
	}
	
	private void setDateTime(MyStringList words, Task t) {
		
		GregorianCalendar gc = new GregorianCalendar();
		for (int index = 0; index < words.size(); index++) {
			index = findDateTime(words, t, gc, index);
		}
		
	}

	private int findDateTime(MyStringList words, Task t, GregorianCalendar gc, int index) {
		
		LocalDate ld = null;
		LocalTime lt = null;
		if (words.get(index).isEmpty()) {
			words.remove(index);
		}
		if (words.get(index).length() <= 4) {
			ld = findDateWithDay(words, index, gc);
			if (ld == null) {
				ld = findDateWithYear(words, index, gc);
			}
		}
		if (ld == null) {
			ld = findDateWithMonth(words, index, gc);
		}
		if (ld == null) {
			ld = findDate(words, index, gc);
		}
		if (ld == null) {
			ld = findDateWithWord(words, index, gc);//get date with dayofweek or "next" or "last" or "by"
		}
		if (ld == null) {
			lt = findTime(words, index);
		}
		if (ld == null && lt == null) {
			lt = findTimeWithWord(words, index);if(lt!=null){System.out.println(lt.toString());}
		}
		if (ld != null || lt != null) {
			setDateTimeToTask(t, ld, lt);
			index -= adjustDateTimeOfTask(t, words, index - 1, gc);//adjust date with "after" or "before"
			index -= 1 + removeUselessWord(words, index - 1);
		}
		return index;
		
	}
	
	private void setDateTimeToTask(Task t, LocalDate ld, LocalTime lt) {
		
		if (ld != null) {
			LocalDateTime startDateTime = t.getStartDateTime();
			LocalDateTime endDateTime = t.getEndDateTime();
			if (startDateTime == null && endDateTime == null) {
				t.setStartDateTime(LocalDateTime.of(ld, LocalTime.of(0, 0)));
			} else if (endDateTime == null) {
				if (startDateTime.getYear() == 0) {
					t.setStartDateTime(LocalDateTime.of(ld, LocalTime.of(startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond())));
				} else {
					t.setEndDateTime(LocalDateTime.of(ld, LocalTime.of(0, 0)));
				}
			} else {
				t.setEndDateTime(LocalDateTime.of(ld, LocalTime.of(endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond())));
			}
		} else {
			LocalDateTime startDateTime = t.getStartDateTime();
			LocalDateTime endDateTime = t.getEndDateTime();
			if (startDateTime == null && endDateTime == null) {
				t.setStartDateTime(LocalDateTime.of(LocalDate.of(0, 1, 1), lt));
			} else if (endDateTime == null) {
				if (startDateTime.getHour() == 0 && startDateTime.getMinute() == 0 && startDateTime.getSecond() == 0) {
					t.setStartDateTime(LocalDateTime.of(LocalDate.of(startDateTime.getYear(), startDateTime.getMonthValue(), startDateTime.getDayOfMonth()), lt));
				} else {
					t.setEndDateTime(LocalDateTime.of(LocalDate.of(0, 1, 1), lt));
				}
			} else {
				t.setEndDateTime(LocalDateTime.of(LocalDate.of(endDateTime.getYear(), endDateTime.getMonthValue(), endDateTime.getDayOfMonth()), lt));
			}
		}
		
	}
	
	private int adjustDateTimeOfTask(Task t, MyStringList words, int index, GregorianCalendar gc) {

		int removedWord = 0;
		if (index >= 0) {
			String word = words.get(index).toLowerCase();
			if (dateTimeKeyWords.contains(word)) {
				changeDateTimeOfTask(t, word, gc);
				words.remove(index);
				removedWord++;
				index--;
			}
			if (moreDateTimeKeyWords.contains(word)) {
				removedWord += changeMoreDateTimeOfTask(t, words, index - 1, gc);
				words.remove(index);
				removedWord++;
			}
		}
		return removedWord;
		
	}
	
	private void changeDateTimeOfTask(Task t, String word, GregorianCalendar gc) {
		
		LocalDateTime startDateTime = t.getStartDateTime();
		LocalDateTime endDateTime = t.getEndDateTime();
		switch (word) {
			case "this" :
				if (endDateTime == null && startDateTime.getYear() == 0) {
					t.setStartDateTime(LocalDateTime.of(gc.get(GregorianCalendar.YEAR), gc.get(GregorianCalendar.MONTH) + 1, gc.get(GregorianCalendar.DAY_OF_MONTH), startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
				} else if (endDateTime != null && endDateTime.getYear() == 0) {
					t.setEndDateTime(LocalDateTime.of(gc.get(GregorianCalendar.YEAR), gc.get(GregorianCalendar.MONTH) + 1, gc.get(GregorianCalendar.DAY_OF_MONTH), endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
				}
				break;
			case "next" :
				if (endDateTime != null) {
					try {
						t.setEndDateTime(LocalDateTime.of(endDateTime.getYear(), endDateTime.getMonthValue(), endDateTime.getDayOfMonth() + 7, endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
					} catch (DateTimeException dte) {
						try {
							t.setEndDateTime(LocalDateTime.of(endDateTime.getYear(), endDateTime.getMonthValue() + 1, new GregorianCalendar(endDateTime.getYear(), endDateTime.getMonthValue() - 1, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH) - endDateTime.getDayOfMonth() + 7, endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
						} catch (DateTimeException dte2) {
							t.setEndDateTime(LocalDateTime.of(endDateTime.getYear() + 1, 1, new GregorianCalendar(endDateTime.getYear(), endDateTime.getMonthValue() - 1, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH) - endDateTime.getDayOfMonth() + 7, endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
						}
					}
				} else {
					try {
						t.setEndDateTime(LocalDateTime.of(startDateTime.getYear(), startDateTime.getMonthValue(), startDateTime.getDayOfMonth() + 7, startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
					} catch (DateTimeException dte) {
						try {
							t.setEndDateTime(LocalDateTime.of(startDateTime.getYear(), startDateTime.getMonthValue() + 1, new GregorianCalendar(startDateTime.getYear(), startDateTime.getMonthValue() - 1, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH) - startDateTime.getDayOfMonth() + 7, startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
						} catch (DateTimeException dte2) {
							t.setEndDateTime(LocalDateTime.of(startDateTime.getYear() + 1, 1, new GregorianCalendar(startDateTime.getYear(), startDateTime.getMonthValue() - 1, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH) - startDateTime.getDayOfMonth() + 7, startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
						}
					}
				}
				break;
			case "last" :
				if (endDateTime != null) {
					try {
						t.setEndDateTime(LocalDateTime.of(endDateTime.getYear(), endDateTime.getMonthValue(), endDateTime.getDayOfMonth() - 7, endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
					} catch (DateTimeException dte) {
						try {
							t.setEndDateTime(LocalDateTime.of(endDateTime.getYear(), endDateTime.getMonthValue() - 1, new GregorianCalendar(endDateTime.getYear(), endDateTime.getMonthValue() - 2, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH) + endDateTime.getDayOfMonth() - 7, endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
						} catch (DateTimeException dte2) {
							t.setEndDateTime(LocalDateTime.of(endDateTime.getYear() - 1, 12, new GregorianCalendar(endDateTime.getYear(), endDateTime.getMonthValue() - 2, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH) + endDateTime.getDayOfMonth() - 7, endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
						}
					}
				} else {
					try {
						t.setEndDateTime(LocalDateTime.of(startDateTime.getYear(), startDateTime.getMonthValue(), startDateTime.getDayOfMonth() - 7, startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
					} catch (DateTimeException dte) {
						try {
							t.setEndDateTime(LocalDateTime.of(startDateTime.getYear(), startDateTime.getMonthValue() - 1, new GregorianCalendar(startDateTime.getYear(), startDateTime.getMonthValue() - 2, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH) + startDateTime.getDayOfMonth() - 7, startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
						} catch (DateTimeException dte2) {
							t.setEndDateTime(LocalDateTime.of(startDateTime.getYear() - 1, 12, new GregorianCalendar(startDateTime.getYear(), startDateTime.getMonthValue() - 2, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH) + startDateTime.getDayOfMonth() - 7, startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
						}
					}
				}
				break;
			case "by" :
				if (endDateTime == null) {
					t.setEndDateTime(startDateTime);
					t.setStartDateTime(LocalDateTime.of(gc.get(GregorianCalendar.YEAR), gc.get(GregorianCalendar.MONTH) + 1, gc.get(GregorianCalendar.DAY_OF_MONTH), 0, 0));
				}
				break;
			default :
		}
		
	}
	
	private int changeMoreDateTimeOfTask(Task t, MyStringList words, int index, GregorianCalendar gc) {
		
		if (index >= 0) {
			String word = words.get(index).toLowerCase();
			switch (word) {
				case "after" :
					return changeDateTimeWithPeriodWord(t, words, index - 1, gc, 1);
				case "before" :
					return changeDateTimeWithPeriodWord(t, words, index - 1, gc, -1);
				default :
			}
		}
		return 0;
		
	}
	
	private int changeDateTimeWithPeriodWord(Task t, MyStringList words, int index, GregorianCalendar gc, int addOrMinus) {
		
		if (index >= 0) {
			String word = words.get(index).toLowerCase();
			LocalDateTime startDateTime = t.getStartDateTime();
			LocalDateTime endDateTime = t.getEndDateTime();
			switch (word) {
				case "day" :
					if (endDateTime != null) {
						try {
							t.setEndDateTime(LocalDateTime.of(endDateTime.getYear(), endDateTime.getMonthValue(), endDateTime.getDayOfMonth() + addOrMinus, endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
						} catch (DateTimeException dte) {
							try {
								if (addOrMinus > 0) {
									t.setEndDateTime(LocalDateTime.of(endDateTime.getYear(), endDateTime.getMonthValue() + 1, 1, endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
								} else if (addOrMinus < 0) {
									t.setEndDateTime(LocalDateTime.of(endDateTime.getYear(), endDateTime.getMonthValue() - 1, new GregorianCalendar(endDateTime.getYear(), endDateTime.getMonthValue() - 2, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH), endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
								}
							} catch (DateTimeException dte2) {
								if (addOrMinus > 0) {
									t.setEndDateTime(LocalDateTime.of(endDateTime.getYear() + 1, 1, 1, endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
								} else if (addOrMinus < 0) {
									t.setEndDateTime(LocalDateTime.of(endDateTime.getYear() - 1, 12, new GregorianCalendar(endDateTime.getYear() - 1, 12, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH), endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
								}
							}
						}
					} else {
						try {
							t.setStartDateTime(LocalDateTime.of(startDateTime.getYear(), startDateTime.getMonthValue(), startDateTime.getDayOfMonth() + addOrMinus, startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
						} catch (DateTimeException dte) {
							try {
								if (addOrMinus > 0) {
									t.setStartDateTime(LocalDateTime.of(startDateTime.getYear(), startDateTime.getMonthValue() + 1, 1, startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
								} else if (addOrMinus < 0) {
									t.setStartDateTime(LocalDateTime.of(startDateTime.getYear(), startDateTime.getMonthValue() - 1, new GregorianCalendar(startDateTime.getYear(), startDateTime.getMonthValue() - 2, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH), startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
								}
							} catch (DateTimeException dte2) {
								if (addOrMinus > 0) {
									t.setStartDateTime(LocalDateTime.of(startDateTime.getYear() + 1, 1, 1, startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
								} else if (addOrMinus < 0) {
									t.setStartDateTime(LocalDateTime.of(startDateTime.getYear() - 1, 12, new GregorianCalendar(startDateTime.getYear() - 1, 12, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH), startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
								}
							}
						}
					}
					words.remove(index);
					return 1;
				case "week" :
					if (endDateTime != null) {
						try {
							t.setEndDateTime(LocalDateTime.of(endDateTime.getYear(), endDateTime.getMonthValue(), endDateTime.getDayOfMonth() + 7 * addOrMinus, endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
						} catch (DateTimeException dte) {
							try {
								if (addOrMinus > 0) {
									t.setEndDateTime(LocalDateTime.of(endDateTime.getYear(), endDateTime.getMonthValue() + 1, endDateTime.getDayOfMonth() + 7 - new GregorianCalendar(endDateTime.getYear(), endDateTime.getMonthValue() - 1, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH), endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
								} else if (addOrMinus < 0) {
									t.setEndDateTime(LocalDateTime.of(endDateTime.getYear(), endDateTime.getMonthValue() - 1, new GregorianCalendar(endDateTime.getYear(), endDateTime.getMonthValue() - 2, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH) + endDateTime.getDayOfMonth() - 7, endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
								}
							} catch (DateTimeException dte2) {
								if (addOrMinus > 0) {
									t.setEndDateTime(LocalDateTime.of(endDateTime.getYear() + 1, 1, endDateTime.getDayOfMonth() + 7 - new GregorianCalendar(endDateTime.getYear(), endDateTime.getMonthValue(), 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH), endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
								} else if (addOrMinus < 0) {
									t.setEndDateTime(LocalDateTime.of(endDateTime.getYear() - 1, 12, new GregorianCalendar(endDateTime.getYear() - 1, 12, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH) + endDateTime.getDayOfMonth() - 7, endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
								}
							}
						}
					} else {
						try {
							t.setStartDateTime(LocalDateTime.of(startDateTime.getYear(), startDateTime.getMonthValue(), startDateTime.getDayOfMonth() + 7 * addOrMinus, startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
						} catch (DateTimeException dte) {
							try {
								if (addOrMinus > 0) {
									t.setStartDateTime(LocalDateTime.of(startDateTime.getYear(), startDateTime.getMonthValue() + 1, startDateTime.getDayOfMonth() + 7 - new GregorianCalendar(startDateTime.getYear(), startDateTime.getMonthValue() - 1, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH), startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
								} else if (addOrMinus < 0) {
									t.setStartDateTime(LocalDateTime.of(startDateTime.getYear(), startDateTime.getMonthValue() - 1, new GregorianCalendar(startDateTime.getYear(), startDateTime.getMonthValue() - 2, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH) + startDateTime.getDayOfMonth() - 7, startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
								}
							} catch (DateTimeException dte2) {
								if (addOrMinus > 0) {
									t.setStartDateTime(LocalDateTime.of(startDateTime.getYear() + 1, 1, startDateTime.getDayOfMonth() + 7 - new GregorianCalendar(startDateTime.getYear(), startDateTime.getMonthValue(), 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH), startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
								} else if (addOrMinus < 0) {
									t.setStartDateTime(LocalDateTime.of(startDateTime.getYear() - 1, 12, new GregorianCalendar(startDateTime.getYear() - 1, 12, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH) + startDateTime.getDayOfMonth() - 7, startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
								}
							}
						}
					}
					words.remove(index);
					return 1;
				case "month" :
					if (endDateTime != null) {
						try {
							t.setEndDateTime(LocalDateTime.of(endDateTime.getYear(), endDateTime.getMonthValue(), endDateTime.getDayOfMonth() + 7 * addOrMinus, endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
						} catch (DateTimeException dte) {
							try {
								if (addOrMinus > 0) {
									t.setEndDateTime(LocalDateTime.of(endDateTime.getYear(), endDateTime.getMonthValue() + 1, endDateTime.getDayOfMonth() + 7 - new GregorianCalendar(endDateTime.getYear(), endDateTime.getMonthValue() - 1, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH), endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
								} else if (addOrMinus < 0) {
									t.setEndDateTime(LocalDateTime.of(endDateTime.getYear(), endDateTime.getMonthValue() - 1, new GregorianCalendar(endDateTime.getYear(), endDateTime.getMonthValue() - 2, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH) + endDateTime.getDayOfMonth() - 7, endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
								}
							} catch (DateTimeException dte2) {
								if (addOrMinus > 0) {
									t.setEndDateTime(LocalDateTime.of(endDateTime.getYear() + 1, 1, endDateTime.getDayOfMonth() + 7 - new GregorianCalendar(endDateTime.getYear(), endDateTime.getMonthValue(), 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH), endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
								} else if (addOrMinus < 0) {
									t.setEndDateTime(LocalDateTime.of(endDateTime.getYear() - 1, 12, new GregorianCalendar(endDateTime.getYear() - 1, 12, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH) + endDateTime.getDayOfMonth() - 7, endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
								}
							}
						}
					} else {
						try {
							t.setStartDateTime(LocalDateTime.of(startDateTime.getYear(), startDateTime.getMonthValue() + addOrMinus, startDateTime.getDayOfMonth(), startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
						} catch (DateTimeException dte) {
							try {
								t.setStartDateTime(LocalDateTime.of(startDateTime.getYear(), startDateTime.getMonthValue() + addOrMinus, new GregorianCalendar(startDateTime.getYear(), startDateTime.getMonthValue() - 1 + addOrMinus, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH), startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
							} catch (DateTimeException dte2) {
								try {
									if (addOrMinus > 0) {
										t.setStartDateTime(LocalDateTime.of(startDateTime.getYear() + 1, 1, startDateTime.getDayOfMonth(), startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
									} else if (addOrMinus < 0) {
										t.setStartDateTime(LocalDateTime.of(startDateTime.getYear() - 1, 12, startDateTime.getDayOfMonth(), startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
									}
								} catch (DateTimeException dte3) {
									if (addOrMinus > 0) {
										t.setStartDateTime(LocalDateTime.of(startDateTime.getYear() + 1, 1, new GregorianCalendar(startDateTime.getYear() + 1, 1, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH), startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
									} else if (addOrMinus < 0) {
										t.setStartDateTime(LocalDateTime.of(startDateTime.getYear() - 1, 12, new GregorianCalendar(startDateTime.getYear() - 1, 12, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH), startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
									}
								}
							}
						}
					}
					words.remove(index);
					return 1;
				case "year" :
					if (endDateTime != null) {
						try {
							t.setEndDateTime(LocalDateTime.of(endDateTime.getYear() + addOrMinus, endDateTime.getMonthValue(), endDateTime.getDayOfMonth(), endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
						} catch (DateTimeException dte) {
							t.setEndDateTime(LocalDateTime.of(endDateTime.getYear() + addOrMinus, endDateTime.getMonthValue(), new GregorianCalendar(endDateTime.getYear() + addOrMinus, endDateTime.getMonthValue() - 1, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH), endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond()));
						}
					} else {
						try {
							t.setStartDateTime(LocalDateTime.of(startDateTime.getYear() + addOrMinus, startDateTime.getMonthValue(), startDateTime.getDayOfMonth(), startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
						} catch (DateTimeException dte) {
							t.setStartDateTime(LocalDateTime.of(startDateTime.getYear() + addOrMinus, startDateTime.getMonthValue(), new GregorianCalendar(startDateTime.getYear() + addOrMinus, startDateTime.getMonthValue() - 1, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH), startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond()));
						}
					}
					words.remove(index);
					return 1;
				default :
			}
		}
		return 0;
		
	}
	
	private LocalDate findDateWithDay(MyStringList words, int index, GregorianCalendar gc){
		
		if (index + 1 < words.size()) {
			int day = getDay(words.get(index));
			if (day > 0) {
				int mth = getMonth(words.get(index + 1));
				if (mth > 0) {
					try {
						int yr = getYear(words.get(index + 2));
						if (yr > 0) {
							words.remove(index + 2);
							words.remove(index + 1);
							words.remove(index);
							return LocalDate.of(yr, mth, day);
						}
					} catch (Exception e) {
					}
					words.remove(index + 1);
					words.remove(index);
					return LocalDate.of(gc.get(GregorianCalendar.YEAR), mth, day);
				}
			}
		}
		return null;
		
	}
	
	private LocalDate findDateWithMonth(MyStringList words, int index, GregorianCalendar gc){

		if (index + 1 < words.size()) {
			int mth = getMonth(words.get(index));
			if (mth > 0) {
				int day = getDay(words.get(index + 1));
				if (day > 0) {
					try {
						int yr = getYear(words.get(index + 2));
						if (yr > 0) {
							words.remove(index + 2);
							words.remove(index + 1);
							words.remove(index);
							return LocalDate.of(yr, mth, day);
						}
					} catch (Exception e) {
					}
					words.remove(index + 1);
					words.remove(index);
					return LocalDate.of(gc.get(GregorianCalendar.YEAR), mth, day);
				}
			}
		}
		return null;
		
	}
	
	private LocalDate findDateWithYear(MyStringList words, int index, GregorianCalendar gc){

		if (index + 2 < words.size()) {
			int yr = getYear(words.get(index));
			if (yr > 0) {
				int mth = getMonth(words.get(index + 1));
				if (mth > 0) {
					int day = getDay(words.get( + 2));
					if (day > 0) {
						words.remove(index + 2);
						words.remove(index + 1);
						words.remove(index);
						return LocalDate.of(yr, mth, day);
					}
				} else {
					int day = getDay(words.get(index + 1));
					if (day > 0) {
						mth = getMonth(words.get(index + 2));
						if (mth > 0) {
							words.remove(index + 2);
							words.remove(index + 1);
							words.remove(index);
							return LocalDate.of(yr, mth, day);
						}
					}
				}
			}
		}
		return null;
		
	}
	
	private LocalDate findDate(MyStringList words, int index, GregorianCalendar gc) {

		MyStringList s = new MyStringList();
		LocalDate ld = null;
		if (words.get(index).contains("/")) {
			s.addAll(Arrays.asList(words.get(index).split("/")));
		} else if (words.get(index).contains("\\")) {
			s.addAll(Arrays.asList(words.get(index).split("\\")));
		} else if (words.get(index).contains("-")) {
			s.addAll(Arrays.asList(words.get(index).split("-")));
		}
		ld = findDateWithDay(s, 0, gc);
		if (ld == null) {
			ld = findDateWithMonth(s, 0, gc);
		}
		if (ld == null) {
			ld = findDateWithYear(s, 0, gc);
		}
		if (ld != null) {
			words.remove(index);
		}
		return ld;
		
	}
	
	private LocalTime findTime(MyStringList words, int index) {
		
		String time = words.get(index);
		MyStringList s = new MyStringList();
		int hrToAdd = 0;
		boolean noAmPm = true;
		for (String tw : timeWords) {
			if (time.toLowerCase().endsWith(tw)) {
				noAmPm = false;
				if (tw.equals("pm")) {
					hrToAdd = 12;
				}
				time = time.substring(0, time.toLowerCase().lastIndexOf(tw));
				break;
			}
		}
		if (noAmPm && index + 1 < words.size()) {
			for (String tw : timeWords) {
				if (words.get(index + 1).equalsIgnoreCase(tw)) {
					if (tw.equals("pm")) {
						hrToAdd = 12;
					}
					words.remove(index + 1);
					break;
				}
			}
		}
		if (time.contains(":")) {
			s.addAll(Arrays.asList(time.split(":")));
		} else if (time.contains(".")) {
			s.addAll(Arrays.asList(time.split(".")));
		} else {
			s.add(time);
		}
		LocalTime lt = getTime(s, hrToAdd);
		if (lt != null) {
			words.remove(index);
		}
		return lt;
		
	}
	
	private LocalDate findDateWithWord(MyStringList words, int index, GregorianCalendar gc) {
		
		String word = words.get(index).toLowerCase();
		if (dayWords.contains(word)) {
			return getDateWithDayWord(words, index, gc);
		} else if (dayOfWeekWords.contains(word)){
			return getDateWithDayOfWeekWord(words, index, gc);
		}
		return null;
		
	}
	
	private LocalTime findTimeWithWord(MyStringList words, int index) {
		
		String word = words.get(index).toLowerCase();
		if (timeOfDayWords.contains(word)) {
			words.remove(index);
			return getTimeWithTimeOfDayWord(word);
		}
		return null;
		
	}
	
	private LocalDate getDateWithDayWord(MyStringList words, int index, GregorianCalendar gc) {
		
		String word = words.get(index);
		switch (word) {
			case "today" :
				words.remove(index);
				return LocalDate.of(gc.get(GregorianCalendar.YEAR), gc.get(GregorianCalendar.MONTH) + 1, gc.get(GregorianCalendar.DAY_OF_MONTH));
			case "tomorrow" :
				words.remove(index);
				try {
					return LocalDate.of(gc.get(GregorianCalendar.YEAR), gc.get(GregorianCalendar.MONTH) + 1, gc.get(GregorianCalendar.DAY_OF_MONTH) + 1);
				} catch (DateTimeException dte) {
					try {
						return LocalDate.of(gc.get(GregorianCalendar.YEAR), gc.get(GregorianCalendar.MONTH) + 2, 1);
					} catch (DateTimeException dte2) {
						return LocalDate.of(gc.get(GregorianCalendar.YEAR) + 1, 1, 1);
					}
				}
			case "yesterday" :
				words.remove(index);
				try {
					return LocalDate.of(gc.get(GregorianCalendar.YEAR), gc.get(GregorianCalendar.MONTH) + 1, gc.get(GregorianCalendar.DAY_OF_MONTH) - 1);
				} catch (DateTimeException dte) {
					try {
						return LocalDate.of(gc.get(GregorianCalendar.YEAR), gc.get(GregorianCalendar.MONTH), new GregorianCalendar(gc.get(GregorianCalendar.YEAR), gc.get(GregorianCalendar.MONTH) - 1, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
					} catch (DateTimeException dte2) {
						return LocalDate.of(gc.get(GregorianCalendar.YEAR) + 1, 12, new GregorianCalendar(gc.get(GregorianCalendar.YEAR) - 1, 11, 1).getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
					}
				}
			default :
				return null;
		}
		
	}
	
	private LocalDate getDateWithDayOfWeekWord(MyStringList words, int index, GregorianCalendar gc) {
		
		int day = getNumeric(words.get(index).toLowerCase()); System.out.println(day);
		int dayToAdd = day - gc.get(GregorianCalendar.DAY_OF_WEEK) + 1;
		if (dayToAdd < 0) {
			dayToAdd += 7;
		}
		words.remove(index);
		try {
			return LocalDate.of(gc.get(GregorianCalendar.YEAR), gc.get(GregorianCalendar.MONTH) + 1, gc.get(GregorianCalendar.DAY_OF_MONTH) + dayToAdd);
		} catch (DateTimeException dte) {
			try {
				return LocalDate.of(gc.get(GregorianCalendar.YEAR), gc.get(GregorianCalendar.MONTH) + 2, gc.get(GregorianCalendar.DAY_OF_MONTH) + dayToAdd - gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
			} catch (DateTimeException dte2) {
				return LocalDate.of(gc.get(GregorianCalendar.YEAR) + 1, 1, gc.get(GregorianCalendar.DAY_OF_MONTH) + dayToAdd - gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
			}
		}
		
	}
	
	private LocalTime getTimeWithTimeOfDayWord(String word) {
		
		switch (word) {
			case "morning" :
				return LocalTime.of(5, 0);
			case "noon" :
				return LocalTime.of(12, 0);
			case "afternoon" :
				return LocalTime.of(13, 0);
			case "evening" :
				return LocalTime.of(17, 0);
			case "night" :
				return LocalTime.of(19, 0);
			case "midnight" :
				return LocalTime.of(23, 59, 59);
			default :
				return null;
		}
		
	}
	
	private int getDay(String s) {
		
		if (s.length() <= 4) {
			try {
				int num = Integer.parseInt(s);
				if (num <= 31 && num > 0){
					return num;
				}
			} catch (Exception e) {
				for (String onw : ordinalNumWords) {
					if (s.endsWith(onw)) {
						return getDay(s.substring(0, s.lastIndexOf(onw)));
					}
				}
			}
		}
		return 0;
		
	}
	
	private int getMonth(String s) {
		
		try {
			int num = Integer.parseInt(s);
			if (num <= 12 && num > 0){
				return num;
			}
		} catch (Exception e) {
			for (String m : monthWords) {
				if (s.equalsIgnoreCase(m)) {
					return getNumeric(m);
				}
			}
		}
		return 0;
		
	}
	
	private int getYear(String s) {
		
		try {
			int num = Integer.parseInt(s);
			if (num < 10000 && num > 999){
				return num;
			}
		} catch (Exception e) {
		}
		return 0;
		
	}
	
	private LocalTime getTime(MyStringList s, int hrToAdd) {
		
		if (!s.isEmpty()) {
			try {
				int hr = 0;
				int min = 0;
				int sec = 0;
				switch (s.size()) {
					case 3 :
						sec = Integer.parseInt(s.get(2));
					case 2 :
						min = Integer.parseInt(s.get(1));
					case 1 :
						hr = Integer.parseInt(s.get(0));
				}
				if (hr != 12 && hr + hrToAdd <= 24) {
					hr += hrToAdd;
				}
				return LocalTime.of(hr, min, sec);
			} catch (Exception e) {
			}
		}
		return null;
		
	}
	
	private int removeUselessWord(MyStringList words, int index) {
		
		if (index >= 0) {
			for (String uw : uselessWords) {
				if(words.get(index).equalsIgnoreCase(uw)) {
					words.remove(index);
					return 1;
				}
			}
		}
		return 0;
		
	}
	
	private void setContent(MyStringList words, Task t) {
		
		StringBuilder sb = new StringBuilder();
		for (String w : words) {
			sb.append(w + " ");
		}
		t.setContent(sb.toString());
		
	}
	
	private int getNumeric(String w) {
		
		switch(w){
			case "mon" :
			case "monday" :
			case "jan" :
			case "january" :
				return 1;
			case "tue" :
			case "tuesday" :
			case "feb" :
			case "febuary" :
				return 2;
			case "wed" :
			case "wednesday" :
			case "mar" :
			case "march" :
				return 3;
			case "thu" :
			case "thursday" :
			case "apr" :
			case "april" :
				return 4;
			case "fri" :
			case "friday" :
			case "may" :
				return 5;
			case "sat" :
			case "saturday" :
			case "jun" :
			case "june" :
				return 6;
			case "sun" :
			case "sunday" :
			case "jul" :
			case "july" :
				return 7;
			case "aug" :
			case "august" :
				return 8;
			case "sep" :
			case "september" :
				return 9;
			case "oct" :
			case "october" :
				return 10;
			case "nov" :
			case "november" :
				return 11;
			case "dec" :
			case "december" :
				return 12;
			default :
				return 0;
		}
		
	}
}

class MyStringList extends ArrayList<String>{
	
	public boolean containsIgnoreCase(String param) {
		
		for (String s : this) {
			if (param.equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
		
	}
	
	public int indexOfIgnoreCase(String param) {
		
		for (String s : this) {
			if (param.equalsIgnoreCase(s)) {
				return this.indexOf(s);
			}
		}
		return -1;
		
	}
	
	public void removeIgnoreCase(String param) {
		
		for (String s : this) {
			if (param.equalsIgnoreCase(s)) {
				this.remove(s);
				break;
			}
		}
	
	}
	
}