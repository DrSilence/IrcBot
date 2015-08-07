/**
 * 
 */
package de.drsilence.ircbot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;

import de.drsilence.ircbot.eventhandlers.UserLists;
import de.drsilence.irclib.IrcActionEvent;
import de.drsilence.irclib.IrcConnection;
import de.drsilence.irclib.IrcMessages;
import de.drsilence.irclib.IrcReplies;


/**
 * @author drsilence
 *
 */
public class Main {

	/**
	 * @param args Start params.
	 */
	public static void main(String[] args) {
		
		final String server 	= "irc.twitch.tv";
		final String pass   	= "oauth:7appgukzxulffvv59u93vbawcuncm5";
		final String nick 		= "da_checker";
		final String login 		= "da_checker";
        final String channel 	= "#da_checker";

		IrcConnection irc = new IrcConnection();
		//test:
		irc.addIrcActionEvent(new UserLists());
		irc.addIrcActionEvent(new IrcActionEvent() {
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			@Override
			public void eventMsg(IrcConnection ircio, IrcMessages msg, Matcher m) {
				super.eventMsg(ircio, msg, m);
				switch (msg) {
					case MSG_PRIVMSG:
						String line = String.format( 	"[%s] %20s : %s",
														dateFormat.format(new java.util.Date()),
														m.group("nick"),
														m.group("text")
														);
						System.out.println(line);
						if(
							m.group("nick").toLowerCase().trim().equals("dr_silence") &&
							m.group("text").toLowerCase().trim().equals("!quit")
						) {
							ircio.irc_cmd_msg(channel, "Ok Master! I'll take my leave...");
							ircio.irc_cmd_quit("Bye Bye!");
						}
						break;
						
					case MSG_UNKNOWN:
						System.err.println("Error: MSG_UNKOWN -> '"+m.group("rawmessage")+"'");

					default:
						break;
				}
			}

			@Override
			public void eventNumeric(IrcConnection ircio, IrcReplies rply,
					Matcher m) {
				super.eventNumeric(ircio, rply, m);
				// Just display all responses:
				System.out.println(m.group("rawmessage"));
			}
			
		});
		irc.connect(server, nick, login, pass);
		irc.irc_cmd_join( channel );
		//irc.irc_cmd_join("#northernlion");
		// Send Irc v3 req to enable join/part notifies:
		irc.irc_send_raw("CAP REQ :twitch.tv/membership\r\n");
		irc.run( ); // BLOCKING !
        
	}

}
