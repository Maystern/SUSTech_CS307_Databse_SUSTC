package cn.edu.sustech.cs307.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class CalendarUtils {
	
	private static final ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
		@Override
		public DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};
	
	private CalendarUtils() {}
	
	public static Calendar getCalendar(String formatTime) throws ParseException {
		return getCalendar(dateFormat.get().parse(formatTime));
	}
	
	public static void removeThreadLocal() {
		dateFormat.remove();
	}
	
	public static Calendar getCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}
	
}
