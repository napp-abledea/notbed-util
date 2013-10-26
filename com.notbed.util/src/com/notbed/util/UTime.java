/**
 *
 */
package com.notbed.util;

import static java.lang.Integer.parseInt;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.concurrent.TimeUnit.HOURS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Alexandru Bledea
 * @since Oct 10, 2013
 */
public class UTime {
	public static final Pattern VALID_TIME_12H_NO_SPACES = Pattern.compile("^([1-9]|1[0-2]|0[1-9]){1}(:[0-5][0-9][aApP][mM]){1}$");
	public static final Pattern VALID_TIME_12H = Pattern.compile("^([1-9]|1[0-2]|0[1-9]){1}(( )?:( )?[0-5][0-9]( )?[aApP][mM]){1}$");
	public static final Pattern VALID_TIME_24H_NO_SPACES = Pattern.compile("^(([0-9])|([0-1][0-9])|([2][0-3])):(([0-5][0-9]))$");
	public static final Pattern VALID_TIME_24H = Pattern.compile("^(([0-9])|([0-1][0-9])|([2][0-3]))( )?:( )?(([0-5][0-9]))$");

	/**
	 * @return
	 */
	public static Calendar getEmptyCalendar() {
		Calendar instance = Calendar.getInstance();
		instance.setTimeInMillis(0);
		return instance;
	}

	/**
	 * @author Alexandru Bledea
	 * @since Oct 10, 2013
	 */
	public static class HourSpan {

		private final HourInfo t1;
		private final HourInfo t2;

		private final boolean ascSpan;

		/**
		 * @param time1
		 * @param time2
		 * @param allowSpaces
		 */
		public HourSpan(String time1, String time2, boolean allowSpaces) {
			t1 = new HourInfo(time1, allowSpaces);
			t2 = new HourInfo(time2, allowSpaces);

			int compareResult = t1.compareTo(t2);
			if (compareResult == 0) {
				throw new IllegalArgumentException("Cannot have a hourly span that starts and ends on the same date.");
			}

			ascSpan = compareResult == -1;
		}

		/**
		 * @param date
		 * @return
		 */
		public boolean isDateInside(Date date) {
			HourInfo hourInfo = new HourInfo(date);
			int result1 = t1.compareTo(hourInfo);
			int result2 = t2.compareTo(hourInfo);
			return (result1 < result2) == ascSpan;
		}

		/**
		 * @param span
		 * @return
		 */
		public boolean overlaps(
				HourSpan span) {
			if (span == null) {
				return false;
			}
			return t1.getAllMinutes() <= span.t2.getAllMinutes() && span.t1.getAllMinutes() <= t2.getAllMinutes();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return t1.hour24 + " - " + t2.hour24;
		}

		/**
		 * AAA doesn't work very well with overflowing times
		 * @param hourSpans
		 * @return
		 */
		public static boolean areSpansOverlapping(HourSpan... hourSpans) {
			if (hourSpans == null || hourSpans.length == 0) {
				return false;
			}
			List<HourSpan> list = new ArrayList<HourSpan>(Arrays.asList(hourSpans));
			while (list.size() > 1) {
				HourSpan removed = list.remove(0);
				for (HourSpan hourSpan : list) {
					if (removed.overlaps(hourSpan)) {
						System.out.println(removed + " overlaps " + hourSpan);
						return true;
					}
				}
			}
			return false;
		}
	}

	/**
	 * @author Alexandru Bledea
	 * @since Oct 10, 2013
	 */
	private static class HourInfo implements Comparable<HourInfo> {

		private final String trimmedTime;
		private final int hour24;
		private final int minute;

		/**
		 *
		 */
		public HourInfo(Date date) {
			this(getTime(date), false);
		}

		/**
		 * @param time
		 */
		public HourInfo(String time, boolean allowSpaces) {
			if (time == null) {
				throw new IllegalArgumentException("No time provided!");
			}
			trimmedTime = time.trim();

			Pattern pattern12h;
			Pattern pattern24h;
			if (allowSpaces) {
				pattern12h = VALID_TIME_12H;
				pattern24h = VALID_TIME_24H;
			} else {
				pattern12h = VALID_TIME_12H_NO_SPACES;
				pattern24h = VALID_TIME_24H_NO_SPACES;
			}
			boolean format24h = matches(pattern24h, trimmedTime);
			if (!format24h && !matches(pattern12h, trimmedTime)) {
				throw new IllegalStateException(String.format("Invalid time: '%s'", trimmedTime));
			}
			List<String> tokens = UString.breakAndTrimLines(trimmedTime, ":");
			int hour = parseInt(tokens.get(0));
			String minuteToken = tokens.get(1);
			String amToken = null;
			if (format24h) {
				minute = parseInt(minuteToken);
			} else {
				if (tokens.size() == 2) {
					minute = parseInt(minuteToken.substring(0, 2));
					amToken = minuteToken.substring(2, 4);
				} else {
					minute = parseInt(minuteToken);
					amToken = tokens.get(2);
				}
				boolean am = amToken.toLowerCase().equals("am");
				if (hour == 12) {
					if (am) {
						hour = 0;
					}
				}
				if (hour < 12 && !am) {
					hour += 12;
					hour %= 24;
				}
			}
			hour24 = hour;
		}

		/**
		 * @param p
		 * @param s
		 * @return
		 */
		private boolean matches(Pattern p, String s) {
			return p.matcher(s).matches();
		}

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(HourInfo o) {
			if (o == null) {
				return 0;
			}
			int result = compare(hour24, o.hour24);
			if (result != 0) {
				return result;
			}
			return compare(minute, o.minute);
		}

		/**
		 * @return
		 */
		public int getAllMinutes(){
			return (int) (HOURS.toMinutes(hour24) + minute);
		}

		/**
		 * @param i1
		 * @param i2
		 * @return
		 */
		private int compare(int i1, int i2) {
			return new Integer(i1).compareTo(new Integer(i2));
		}

		/**
		 * @param date
		 * @return
		 */
		private static String getTime(Date date) {
			if (date == null) {
				throw new IllegalStateException("No time provided.");
			}
			Calendar instance = Calendar.getInstance();
			instance.setTime(date);
			int h24 = instance.get(HOUR_OF_DAY);
			int minute = instance.get(MINUTE);
			StringBuilder time = new StringBuilder();
			time.append(h24).append(":");
			if (minute < 10) {
				time.append(0);
			}
			time.append(minute);
			return time.toString();
		}
	}
}