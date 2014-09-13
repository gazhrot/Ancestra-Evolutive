package org.ancestra.evolutive.tool.plugin;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader {
	
	private Plugin plugin;
	private File file;
	private String main;
	private boolean enabled = false;
	
	public PluginLoader(File file) throws Exception {
		if(file == null)
			throw new Exception("file doesn't exist.");

		JarFile jarFile = new JarFile(new File(file.getPath())); 
		
		ClassLoader classLoader = URLClassLoader.newInstance(
			    new URL[] { file.toURI().toURL() },
			    getClass().getClassLoader()
		);

		this.file = file;

		String nameClass;
        try {
            ArrayList<JarEntry> entries = Collections.list(jarFile.entries());
            for(JarEntry jar : entries)
                System.out.println(jar);


            InputStream in = jarFile.getInputStream(jarFile.getEntry("plugin"));
			nameClass = this.main = readEntry(in);
		} catch(Exception e) {
            e.printStackTrace();
			throw new Exception("cannot read the plugin file.");
		}	
		
		if(nameClass == null)
			throw new Exception("the name class cannot be null");
		if(nameClass.startsWith("org.ancestra.evolutive."))
			throw new Exception("the package of main class must not start with : org.ancestra.evolutive.");
		if(nameClass.contains(".java"))
			throw new Exception("please check your plugin file and delete the extension .java");

		Class<?> mainClass;
		try {
			mainClass = Class.forName(nameClass, true, classLoader);
		} catch(ClassNotFoundException e) {
            e.printStackTrace();
			throw new Exception("cannot find the main class.");
		}

		Class<? extends Plugin> pluginClass;
		try {
			pluginClass = mainClass.asSubclass(Plugin.class);
		} catch(ClassCastException e) {
			throw new Exception("the main class haven't a plugin class.");
		}

		try {
			this.plugin = pluginClass.newInstance();
		} catch(InstantiationException e) {
			throw new Exception("the specified class object cannot be instantiated.");
		} catch(IllegalAccessException e) {
			throw new Exception("the constructor doesn't public.");
		}

		this.enable();
	}
	
	public Plugin getPlugin() {
		return plugin;
	}
	
	public File getFile() {
		return file;
	}
	
	public String getMain() {
		return main;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void enable() {
		this.getPlugin().onEnable();
		this.setEnabled(true);
	}

	public void disable() {
		this.getPlugin().onDisable();
		this.setEnabled(false);
	}
	
	public void reload() {
		this.setEnabled(false);
		this.getPlugin().onReload();
		this.setEnabled(true);
	}
	
	private String readEntry(InputStream input) throws IOException {
		InputStreamReader isr = new InputStreamReader(input);
		BufferedReader reader = new BufferedReader(isr);
		String line = reader.readLine();
		reader.close();
		return line;
    }
}