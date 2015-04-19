/**
 * 
 */
package de.drsilence.ircbot;

import java.io.IOException;
import java.net.UnknownHostException;

import de.drsilence.irclib.IrcConnection;

/**
 * @author drsilence
 *
 */
public class Main {

	/**
	 * 
	 */
	public Main() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			IrcConnection.init();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
