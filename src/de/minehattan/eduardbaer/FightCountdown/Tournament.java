package de.minehattan.eduardbaer.FightCountdown;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Tournament {
	private final FightCountdown plugin;

	private Calendar tournamentDate, now;
	private String arena;

	private String host;
	private SimpleDateFormat date, time;
	private int[] tasks;

	Tournament(FightCountdown plugin, String date, String arena, String host) {
		this.plugin = plugin;

		tournamentDate = Calendar.getInstance();
		try {
			tournamentDate.setTime(new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(date));
		} catch (ParseException e) {
			System.err.println("Unable to parse date '" + date + "', wrong format.");
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

	public void startScheduler() {
		String[] schedule = plugin.getConfig().getString("announcementSchedule").split(", ");
		tasks = new int[schedule.length];

		for (int o = 0; o < schedule.length; o++) {
			if (isAfter(Integer.parseInt(schedule[o]))) {
				tasks[o] = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						plugin.broadcast(plugin.getConfig().getString("announceTournament").replaceAll("%time", getTime()).replaceAll("%arena", arena)
								.replaceAll("%host", host));
					}
				}, 20L * secondsFrom(Integer.parseInt(schedule[o])));
			}
		}
	}

	public boolean isAfter(int minutes) {
		now = Calendar.getInstance();
		if (minutes == 0) {
			return tournamentDate.getTime().after(now.getTime());
		} else {
			Calendar tmpDate = (Calendar) tournamentDate.clone();
			date = new SimpleDateFormat("dd-MM-yyyy HH:mm");
			tmpDate.add(Calendar.MINUTE, -minutes);
			return tmpDate.getTime().after(now.getTime());
		}
	}

	private long secondsFrom(int minutes) {
		now = Calendar.getInstance();
		Calendar tmpDate = (Calendar) tournamentDate.clone();
		tmpDate.add(Calendar.MINUTE, -minutes);
		return (tmpDate.getTimeInMillis() - now.getTimeInMillis()) / 1000;
	}

	public void killTasks() {
		for (int i = 0; i < tasks.length; i++) {
			plugin.getServer().getScheduler().cancelTask(tasks[i]);
		}
	}

	public String getDate() {
		date = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
		return date.format(tournamentDate.getTime()) + "h";
	}

	public String getTime() {
		time = new SimpleDateFormat("HH:mm");
		return time.format(tournamentDate.getTime()) + "h";
	}

	public String getUnformatedDate() {
		date = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		return date.format(tournamentDate.getTime());
	}

	public Calendar getTournamentDate() {
		return tournamentDate;
	}

	public String getArena() {
		return arena;
	}

	public void setArena(String arena) {
		this.arena = arena;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
}