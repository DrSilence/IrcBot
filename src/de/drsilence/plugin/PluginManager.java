package de.drsilence.plugin;

import java.io.File;
import java.util.List;

public class PluginManager {
	
	private File			_pulginDir	= new File("./plugins");
	private ApplicationAPI	_api 		= null;
	private List<Pluggable> _plugins 	= null;
	
	public void loadPlugins() {
		this._plugins = PluginLoader.loadPlugins(_pulginDir);
		for( Pluggable p : _plugins) {
			p.setPluginAPI(_api);
		}
	}
	
	public void startPlugins() {
		for( Pluggable p : _plugins) {
			p.start();
		}
	}
	
	public void stopPlugins() {
		for( Pluggable p : _plugins) {
			p.stop();
		}
	}

}
