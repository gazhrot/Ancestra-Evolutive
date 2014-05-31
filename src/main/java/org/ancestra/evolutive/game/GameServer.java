package org.ancestra.evolutive.game;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.ancestra.evolutive.client.Account;
import org.ancestra.evolutive.client.Player;
import org.ancestra.evolutive.common.SocketManager;
import org.ancestra.evolutive.core.Console;
import org.ancestra.evolutive.core.Log;
import org.ancestra.evolutive.core.Server;
import org.ancestra.evolutive.core.World;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioProcessor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameServer {

	private Map<Long, GameClient> clients = new HashMap<>();
	private ArrayList<Account> waitingClients = new ArrayList<Account>();
	private long startTime;
	private int maxConnections = 0;
	private IoAcceptor acceptor;
	
	public GameServer() {
		acceptor = new NioSocketAcceptor();
		acceptor.getFilterChain().addLast("game-codec-filter", 
				new ProtocolCodecFilter(
				new TextLineCodecFactory(Charset.forName("UTF8"), LineDelimiter.NUL, 
				new LineDelimiter("\n\0"))));
		acceptor.setHandler(new GameHandler());
	}
	
	public void initialize() {
        Logger logger = (Logger)LoggerFactory.getLogger("org.apache.mina");
        try {
			acceptor.bind(new InetSocketAddress(Server.config.getGamePort()));
			startTime = System.currentTimeMillis();
		} catch (IOException e) {
			logger.error("Can t launch server", e);
			System.exit(1);
		}
	}
	
	public void close() {
		 this.acceptor.unbind();
		 for (IoSession session : acceptor.getManagedSessions().values())
			 if (session.isConnected() || !session.isClosing()) 
				 session.close(true);
		 this.acceptor.dispose();
	}
	
	public static String getServerDate() {
		Date actDate = new Date();
		DateFormat dateFormat = new SimpleDateFormat("dd");
		String jour = Integer.parseInt(dateFormat.format(actDate))+"";
		
		while(jour.length() <2)
			jour = "0"+jour;
		
		dateFormat = new SimpleDateFormat("MM");
		String mois = (Integer.parseInt(dateFormat.format(actDate))-1)+"";
		
		while(mois.length() <2)
			mois = "0"+mois;
		
		dateFormat = new SimpleDateFormat("yyyy");
		String annee = (Integer.parseInt(dateFormat.format(actDate))-1370)+"";
		return "BD"+annee+"|"+mois+"|"+jour;
	}
	
	public void scheduleActions() {
		ScheduledExecutorService scheduler = World.data.getScheduler();
		
		scheduler.scheduleWithFixedDelay(new Runnable() {
			public void run() {
				if(!Server.config.isSaving())
					World.data.saveData(-1);
			}
		}, Server.config.getSaveTime(), Server.config.getSaveTime(), TimeUnit.MILLISECONDS);
		
		scheduler.scheduleWithFixedDelay(new Runnable() {
			public void run() {
				World.database.getOtherData().reloadLiveActions();
				Log.addToLog("Les live actions ont ete appliquees");
			}
		}, Server.config.getLoadDelay(), Server.config.getLoadDelay(), TimeUnit.MILLISECONDS);
		
		scheduler.scheduleWithFixedDelay(new Runnable() {
			public void run() {
				World.data.RefreshAllMob();
				Log.addToLog("La recharge des mobs est finie");
			}
		}, Server.config.getReloadMobDelay(), Server.config.getReloadMobDelay(), TimeUnit.MILLISECONDS);
		
		scheduler.scheduleWithFixedDelay(new Runnable() {
			public void run() {
				for(Player player: World.data.getOnlinePersos()) {
                    if(player.getLastPacketTime() + 600000 < System.currentTimeMillis()) {
                        if(player != null && player.getAccount().getGameClient() != null && player.isOnline()) {
                            SocketManager.REALM_SEND_MESSAGE(player.getAccount().getGameClient(), "01|");
                            player.getAccount().getGameClient().getSession().close(true);
                        }
                    }
                }
			}
		}, 60, 60, TimeUnit.SECONDS);
		
		
	}
	
	public Map<Long, GameClient> getClients() {
		return clients;
	}

	public long getStartTime()
	{
		return startTime;
	}
	
	public int getMaxPlayer()
	{
		return maxConnections;
	}
	
	public int getPlayerNumber()
	{
		return clients.size();
	}
	
	public void delClient(GameClient gameClient) {
		clients.remove(gameClient.getSession().getId());
		if(clients.size() > maxConnections)maxConnections = clients.size();
	}

	public synchronized Account getWaitingCompte(int guid) {
		for (int i = 0; i < waitingClients.size(); i++) 
			if(waitingClients.get(i).getUUID() == guid)
				return waitingClients.get(i);
		return null;
	}
	
	public synchronized void delWaitingCompte(Account _compte) {
		waitingClients.remove(_compte);
	}
	
	public synchronized void addWaitingCompte(Account _compte) {
		waitingClients.add(_compte);
	}
	
	public static String getServerTime() {
		Date actDate = new Date();
		return "BT"+(actDate.getTime()+3600000);
	}

	public void updateMaxPlayer() {
		maxConnections = this.clients.size();
	}
}
