package de.drsilence.plugin;

public interface Pluggable {
	
	public boolean start();
	public boolean stop();
	public void setPluginAPI(ApplicationAPI api);

}
