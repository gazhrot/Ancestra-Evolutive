package org.ancestra.evolutive.core;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Calendar;

public class Log {

    private static Logger gameLogger = (Logger) LoggerFactory.getLogger("Game");
    private static Logger logger = (Logger) LoggerFactory.getLogger("Log");
    private static Logger mjLogger = (Logger) LoggerFactory.getLogger("MJ");
    private static Logger shopLogger = (Logger) LoggerFactory.getLogger("shop");
	public static void initLogs() {

	}
	
	public synchronized static void addToSockLog(String str) {
		if(Server.config.isCanLog()) {
			gameLogger.info(str);
		}
	}

	public synchronized static void addToLog(String str) {
		if(Server.config.isCanLog()) {
			logger.info(str);
		}
	}

	public static void addToMjLog(String str) {
		if(!Server.config.isCanLog()) {
			mjLogger.info(str);
		}
	}

	public static void addToShopLog(String str)	{
		if(!Server.config.isCanLog()) {
			shopLogger.info(str);
		}
	}
}
