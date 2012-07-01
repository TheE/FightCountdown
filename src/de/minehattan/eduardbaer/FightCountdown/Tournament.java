package de.minehattan.eduardbaer.FightCountdown;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Tournament {
	private Calendar tournamentDate;
	private String arena, host;
	private SimpleDateFormat date, time;
	
	Tournament(String date, String arena, String host){
		tournamentDate = Calendar.getInstance();
		try {
			tournamentDate.setTime(new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(date));
		} catch (ParseException e) {
			System.err.println("Unable to parse date '"+ date + "', wrong format.");
			e.printStackTrace();
		}
		if (arena != null) {
			this.arena = arena;
		} else
			this.arena = "";
		if (host != null) {
			this.host = host;
		} else
			this.host = "";
	}
	
	public String getDate () {
		date = new SimpleDateFormat ("dd.MM.yyyy, HH.mm");
		return date.format(tournamentDate.getTime()) + "h";
	}
	public String getTime(){
		time = new SimpleDateFormat ("HH.mm");
		return time.format(tournamentDate.getTime()) + "h";
	}
	public Calendar getFullDate (){
		return tournamentDate;
	}
	
	public long secondsBetween(Calendar startDate, Calendar endDate) {
		int MILLIS_IN_SECOND = 1000;
		long endInstant = endDate.getTimeInMillis();
		int presumedSeconds = (int) ((endInstant - startDate.getTimeInMillis()) / MILLIS_IN_SECOND);
		Calendar cursor = (Calendar) startDate.clone();
		cursor.add(Calendar.SECOND, presumedSeconds);
		long instant = cursor.getTimeInMillis();
		if (instant == endInstant)
			return presumedSeconds;
		final int step = instant < endInstant ? 1 : -1;
		do {
			cursor.add(Calendar.SECOND, step);
			presumedSeconds += step;
		} while (cursor.getTimeInMillis() != endInstant);
		return presumedSeconds;
	}

	public boolean isAfter(Calendar date, int minutes){
		Calendar tmpDate = (Calendar) tournamentDate.clone();
		tmpDate.add(Calendar.MINUTE, -minutes);
		return tmpDate.getTime().after(date.getTime());
	}
	
	public boolean isAfter(Calendar date){
		return tournamentDate.getTime().after(date.getTime());
	}
	
	public boolean isBefore(Calendar date){
		return tournamentDate.getTime().before(date.getTime());
	}
	
	public long secondsFrom(Calendar StartDate, int minutes){
		Calendar tmpDate = (Calendar) tournamentDate.clone();
		tmpDate.add(Calendar.MINUTE, -minutes);
		return secondsBetween(StartDate, tmpDate);
	}
	
	public String getArena(){
		return arena;
	}
	
	public String getHost(){
		return host;
	}

}
