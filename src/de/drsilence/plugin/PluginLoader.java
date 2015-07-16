package de.drsilence.plugin;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class PluginLoader {

	public static List<Pluggable> loadPlugins(File pluginDir) {
	
		try {
			
			if( ! (pluginDir.canRead() && pluginDir.isDirectory()) ) {
				return null;
			}
			
			// Get Jar Files:
			File pluginJars[] = getJarFiles(pluginDir);
			
			// Load Classes in JarFiles:
			ClassLoader cl = new URLClassLoader(fileArrayToURLArray(pluginJars));
			
			// Get Pluggable Classes from JARs:
			List<Class<Pluggable>> plugClasses = PluginLoader.extractClassesFromJARs(pluginJars, cl);
			
			// Generate Pluggable Objects from Classfiles in Jars:
			List<Pluggable> plugObjs = createPluggableObjects(plugClasses);
			
			return plugObjs;
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static File[] getJarFiles(File pluginPath) {
		return pluginPath.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().toLowerCase().endsWith(".jar");
			}
		});
	}
	
	private static URL[] fileArrayToURLArray(File[] files) throws MalformedURLException {
		URL[] urls = new URL[files.length];
		for( int i=0; i<files.length; i++ ) {
			urls[i] = files[i].toURI().toURL();
		}
		return urls;
	}
	
	private static List<Class<Pluggable>> extractClassesFromJARs(File[] pluginJars, ClassLoader cl) throws IOException {
		List<Class<Pluggable>> classes = new ArrayList<Class<Pluggable>>();
		for( File jar : pluginJars ) {
			classes.addAll(extractClassesFromJAR(jar, cl));
		}
		return classes;
	}
	
	@SuppressWarnings("unchecked")
	private static List<Class<Pluggable>> extractClassesFromJAR(File jar, ClassLoader cl) throws IOException {
		List<Class<Pluggable>> classes = new ArrayList<Class<Pluggable>>();
		JarInputStream jaris = new JarInputStream(new FileInputStream(jar));
		JarEntry ent = null;
		while( (ent=jaris.getNextJarEntry())!=null ) {
			if( ent.getName().toLowerCase().endsWith(".class") ) {
				try {
					Class<?> cls = cl.loadClass(ent.getName().substring(0, ent.getName().length() - 6).replace('/', '.'));
					if( isPluggableClass( cls ) ) {
						classes.add( (Class<Pluggable>)cls );
					}
				} catch (ClassNotFoundException e) {
					System.err.println("Can't load Class " + ent.getName());
					e.printStackTrace();
				}
			}
		}
		jaris.close();
		return classes;
	}
	
	private static boolean isPluggableClass(Class<?> cls) {
		for(Class<?> i : cls.getInterfaces()) {
			if( i.equals(Pluggable.class) ) {
				return true;
			}
		}
		return false;
	}
	
	private static List<Pluggable> createPluggableObjects(List<Class<Pluggable>> pluggables) {
		List<Pluggable> plugs = new ArrayList<Pluggable>(pluggables.size());
		for( Class<Pluggable> plug : pluggables) {
			try {
				plugs.add(plug.newInstance());
			} catch (InstantiationException e) {
				System.err.println("Can't instantiate plugin: " + plug.getName());
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				System.err.println("IllegalAccess for plugin: " + plug.getName());
				e.printStackTrace();
			}
		}
		return plugs;
	}
	
}
