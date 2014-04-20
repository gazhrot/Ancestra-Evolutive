package tool.plugin;

public abstract class Plugin {
	
	public void onEnable() {}
	
	public void onDisable() {}
	
	public void onReload() {}
	
	public <T extends Plugin> T getPlugin(Class<T> clazz) {
		if(!Plugin.class.isAssignableFrom(clazz))
			throw new IllegalArgumentException(clazz + " ne contient pas l'extension "+ Plugin.class +" !");
		clazz.asSubclass(clazz);
		return null;
	}
}