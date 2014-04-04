package game.packet.handler;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import game.GameClient;
import common.Constants;
import common.SocketManager;

public class PacketHandler {
		
	public static void parsePacket(GameClient client, String packet) throws ClassNotFoundException, IOException, 
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException { 
		if(!verify(client, packet))
			return;
		for (Class<?> c : getClasses(Packet.class.getPackage().getName())) {
			for(Method method : c.getDeclaredMethods()) {
				Annotation annotation = method.getAnnotation(Packet.class);
				if(annotation instanceof Packet) {
					 Packet name = (Packet) annotation;
					 if(name.value().equals(packet.substring(0, 2)))	{
						 Object[] param = {client, packet};
						 method.invoke(c, param);
						 break;
					 }
				}
			}		
		}
	}

	private static boolean verify(GameClient client, String packet) {
		if (!client.getFilter().authorizes(Constants.getIp(client.getSession().getRemoteAddress().toString())))
			client.kick();
		
		if(client.getPlayer() != null)
			client.getPlayer().refreshLastPacketTime();
		
		if(packet.length() > 3 && packet.substring(0,4).equalsIgnoreCase("ping"))	{
			SocketManager.GAME_SEND_PONG(client);
			return false;
		}
		if(packet.length() > 4 && packet.substring(0,5).equalsIgnoreCase("qping")) {
			SocketManager.GAME_SEND_QPONG(client);
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("rawtypes")
	private static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
	
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
	
		ArrayList<Class> classes = new ArrayList<Class>();
	
		for (File directory : dirs) 
			classes.addAll(findClasses(directory, packageName));

		return classes.toArray(new Class[classes.size()]);
	}
	
	@SuppressWarnings("rawtypes")
	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
	    List<Class> classes = new ArrayList<Class>();
	    if (!directory.exists()) {
	        return classes;
	    }
	    File[] files = directory.listFiles();
	    for (File file : files) {
	        if (file.isDirectory()) {
	            assert !file.getName().contains(".");
	            classes.addAll(findClasses(file, packageName + "." + file.getName()));
	        } else if (file.getName().endsWith(".class")) {
	            classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
	        }
	    }
	    return classes;
	}
}