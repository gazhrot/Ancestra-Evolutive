package tool.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;

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
			nameClass = this.main = readEntry(jarFile.getInputStream(jarFile.getEntry("plugin")));
		} catch(IOException e) {
			throw new Exception("cannot read the plugin file.");
		}	
		
		Class<?> mainClass;
		try {
			mainClass = Class.forName(nameClass, true, classLoader);
		} catch(ClassNotFoundException e) {
			throw new Exception("cannot find the main class.");
		}
		
		Class<? extends Plugin> pluginClass;
		try {
			pluginClass = mainClass.asSubclass(Plugin.class);
		} catch(ClassCastException e) {
			throw new Exception("the main class haven't a plugin class");
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
	
	private String readEntry(InputStream input) throws IOException {
		InputStreamReader isr = new InputStreamReader(input);
		BufferedReader reader = new BufferedReader(isr);
		String line = null;
		while ((line = reader.readLine()) != null) {}
		reader.close();
		return line;
    }
}