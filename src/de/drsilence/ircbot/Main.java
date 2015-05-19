/**
 * 
 */
package de.drsilence.ircbot;

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
	 * @param args Start params.
	 */
	public static void main(String[] args) {
		
		String server 	= "irc.twitch.tv";
        String pass   	= "oauth:7appgukzxulffvv59u93vbawcuncm5";
        String nick 	= "da_checker";
        String login 	= "da_checker";
        String channel 	= "#da_checker";

		IrcConnection irc = new IrcConnection();
		irc.connect(server, nick, login, pass);
		irc.irc_cmd_join( channel );
		irc.run( );
        
	}

}
