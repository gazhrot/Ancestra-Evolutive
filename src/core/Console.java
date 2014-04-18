package core;

import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.Scanner;

import client.Player;

import tool.command.CommandParser;

public class Console extends Thread {
	public static Console instance;
	private Scanner scanner = new Scanner(System.in);
	
	public void initialize() {
		super.setDaemon(true);
		super.start();
	}
	
	@Override
	public void run() {
		while(Server.config.isRunning()) {
			try {
				write("Console > \n");
				String line = scanner.next();
				CommandParser.parse(line, this);
			} catch (NoSuchElementException ignored) { }
		}
		super.interrupt();
	}
	
	public void println(String string) {
		if(string.isEmpty())
			return;
		String date = Calendar.HOUR_OF_DAY+":"+Calendar.MINUTE+":"+Calendar.SECOND;
		string = "["+date+"] : "+string;
		if(Server.config.isDebug())
			System.out.println(string);
		Log.addToLog(string);
	}
	
	public void print(String string) {
		if(string.isEmpty())
			return;
		String date = Calendar.HOUR_OF_DAY+":"+Calendar.MINUTE+":"+Calendar.SECOND;
		string = "["+date+"] : "+string;
		if(Server.config.isDebug())
			System.out.print(string);
		Log.addToLog(string);
	}
	
	public void write(String string) {
		if(string.isEmpty())
			return;
		String date = Calendar.HOUR_OF_DAY+":"+Calendar.MINUTE+":"+Calendar.SECOND;
		string = "["+date+"] : "+string;
		System.out.print(string);
		Log.addToLog(string);
	}
	
	public void writeln(String string) {
		if(string.isEmpty())
			return;
		String date = Calendar.HOUR_OF_DAY+":"+Calendar.MINUTE+":"+Calendar.SECOND;
		string = String.valueOf("["+date+"] : "+string);
		System.out.println(string);
		Log.addToLog(string);
	}
	
	public void print(String string, Object object) {
		if(object instanceof Player)
			((Player) object).sendText(string);
		else if (object instanceof Console)
			write(string);
	}
}