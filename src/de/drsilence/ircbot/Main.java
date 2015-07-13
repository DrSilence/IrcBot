/**
 * 
 */
package de.drsilence.ircbot;

import java.util.regex.Matcher;

import de.drsilence.irclib.IrcActionEvent;
import de.drsilence.irclib.IrcConnection;
import de.drsilence.irclib.IrcMessages;


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
		
		final String server 	= "irc.twitch.tv";
		final String pass   	= "oauth:7appgukzxulffvv59u93vbawcuncm5";
		final String nick 		= "da_checker";
		final String login 		= "da_checker";
        final String channel 	= "#sniddyus";

		IrcConnection irc = new IrcConnection();
		//test:
		irc.addIrcActionEvent(new IrcActionEvent() {
			@Override
			public void eventMsg(IrcConnection ircio, IrcMessages msg, Matcher m) {
				// TODO Auto-generated method stub
				super.eventMsg(ircio, msg, m);
				switch (msg) {
					case MSG_PRIVMSG:
						System.out.println(m.group("nick")+": "+m.group("text"));
						if(
							m.group("nick").toLowerCase().equals("dr_silence") &&
							m.group("text").toLowerCase().equals("!quit")
						) {
							ircio.irc_cmd_msg(channel, "OK Master!");
							ircio.irc_send_raw( "QUIT : Bye!\r\n" );
						}
						break;

					default:
						break;
				}
			}
		});
		irc.connect(server, nick, login, pass);
		irc.irc_cmd_join( channel );
		irc.run( ); // BLOCKING !
        
	}

}
