package org.ancestra.evolutive.kernel;

import java.util.NoSuchElementException;
import java.util.Scanner;

import org.ancestra.evolutive.login.LoginClient;
import org.ancestra.evolutive.object.Player;
import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class Console extends Thread {
	
	public static Console instance;
	private static Logger logger = (Logger) LoggerFactory.getLogger("Log");
	private Scanner scanner = new Scanner(System.in);
	
	public void initialize() {
		super.setDaemon(true);
		super.start();
	}
	
	@Override
	public void run() {
		while(Main.config.isRunning()) {
			try {
				write("Console > \n");
				
				parse(scanner.nextLine());
			} catch (NoSuchElementException ignored) { }
		}
		super.interrupt();
	}
	
	public void parse(String line) {
		String[] infos = line.split("\\ ");
		switch(infos[0].toUpperCase()) {
		case "UPTIME":
			this.write(EmulatorInfos.uptime());
			break;
		case "SEND":
			LoginClient client = LoginClient.clients.get(Long.parseLong(infos[1]));
			client.send(line.substring(5).replace(infos[1] + " ", ""));
			this.write("Send : " + line.substring(5).replace(infos[1] + " ", ""));
			break;
			
		case "TEST":
			IoBuffer buffer = IoBuffer.allocate(2048);				
			buffer.put(String.valueOf("MS" + 13).getBytes());
			buffer.putObject(new Player(1, 13));
			buffer.flip();
			
			//byte[] array = new byte[2];
			//buffer.get(array, 0, 2);
		
			break;
		}
	}
	
	public void write(String msg) {
		if(msg.isEmpty())
			return;
		logger.info(msg);
	}
}