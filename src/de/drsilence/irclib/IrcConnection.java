package de.drsilence.irclib;

import java.lang.Runnable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class IrcConnection implements Runnable {
	
	private static final String NEWLINE = "\r\n";
	
	private BufferedWriter _writer = null;
	private BufferedReader _reader = null;
	
	private ReentrantLock _lock_writer = new ReentrantLock();
	private ReentrantLock _lock_reader = new ReentrantLock();
	
	private ArrayList<IrcActionEvent> _actionHandlers = new ArrayList<IrcActionEvent>();

	private Boolean _printDebug = false;
	
	private Pattern pattern = Pattern.compile(
			"(?<rawmessage>\\:(?<source>((?<nick>[^!]+)![~]{0,1}(?<user>[^@]+)@)?(?<host>[^\\s]+)) " +
			"(?<command>[^\\s]+)( )?(?<parameters>[^:]+){0,1}(:)?(?<text>[^\\r^\\n]+)?)"
			);
	
	public void addIrcActionEvent(IrcActionEvent e) {
		_actionHandlers.add(e);
	}
	
	public void removeIrcActionEvent(IrcActionEvent e) {
		_actionHandlers.remove(e);
	}
	
	public void connect(String server, String nick, String login, String pass) {
		Socket socket;
		try {
			// Connect directly to the IRC server.
			socket = new Socket(server, 6667);
			_writer = new BufferedWriter( new OutputStreamWriter(socket.getOutputStream( ) ) );
			_reader = new BufferedReader( new InputStreamReader(socket.getInputStream( ) ) );
		} catch ( IOException e ) {
			e.printStackTrace();
		}	
	
		// Log on to the server.
		irc_send_raw( "USER " + login + " 8 * : " + NEWLINE );
		irc_send_raw( "PASS " + pass + NEWLINE );
		irc_send_raw( "NICK " + nick + NEWLINE );
	}
	
	public void run() {
		String line = null;
		// Keep reading lines from the server.
		while( (line = irc_read_raw( )) != null ) {
			// Emergency exit.
			if ( (line.indexOf("@!quit") > 0) && (line.indexOf("dr_silence") > 0) ) {
				irc_send_raw( "QUIT :" + NEWLINE );
			}
			// Print received line raw to console.
			//System.out.println( (char)27 + "[31m" + line + (char)27 + "[0m");
			System.out.println( line );
			
			// Handle raw message:
			_handle_raw_message( line );
		}
	}
	
	private Matcher _parse_raw_message(String msg_raw) {
		Matcher m = pattern.matcher(msg_raw);
		if( m.matches( ) && _printDebug ) {
			_print_raw_matcher(m);
		}
		return m;
	}
	
	private Matcher _print_raw_matcher(Matcher m) {
		if( m.matches( ) ) {
			System.out.println( "<rawmessage> "	+ m.group("rawmessage") );
			System.out.println( "<source> "		+ m.group("source") );
			System.out.println( "<nick> "		+ m.group("nick") );
			System.out.println( "<user> "		+ m.group("user") );
			System.out.println( "<host> "		+ m.group("host") );
			System.out.println( "<command> "	+ m.group("command") );
			System.out.println( "<parameters> "	+ m.group("parameters") );
			System.out.println( "<text> "		+ m.group("text") );
			System.out.println( "=========================================" );
		}
		return m;
	}
	
	private void _handle_raw_message(String line) {
		if(line.startsWith(":")) {
			// Server -> Client msg.
			Matcher matcher = _parse_raw_message( line.replaceAll("[\\^]", "X") );
			String command = matcher.group("command").trim();
			if( command.matches("\\d+") ) {
				// Reply.
				IrcReplies reply = IrcReplies.getEnumByValue( Integer.parseInt( command ) );
				_handle_reply( reply, matcher );
			} else {
				IrcMessages message = IrcMessages.getEnumByValue( command );
				_handle_messages( message, matcher );
			}
		} else {
			// PING or Client->Client.
			if( line.toUpperCase().startsWith("PING ") ) {
				// We must respond to PINGs to avoid being disconnected.
				irc_send_raw( "PONG " + line.substring(5) + NEWLINE );
			}
		};
	}
	
	private void _handle_reply(IrcReplies r, Matcher m) {
		switch(r) {
		case ERR_ALREADYREGISTRED:
		case ERR_BADCHANMASK:
		case ERR_BADCHANNELKEY:
		case ERR_BANNEDFROMCHAN:
		case ERR_CANNOTSENDTOCHAN:
		case ERR_CANTKILLSERVER:
		case ERR_CHANNELISFULL:
		case ERR_CHANOPRIVSNEEDED:
		case ERR_ERRONEUSNICKNAME:
		case ERR_FILEERROR:
		case ERR_INVITEONLYCHAN:
		case ERR_KEYSET:
		case ERR_NEEDMOREPARAMS:
		case ERR_NICKCOLLISION:
		case ERR_NICKNAMEINUSE:
		case ERR_NOADMININFO:
		case ERR_NOLOGIN:
		case ERR_NOMOTD:
		case ERR_NONICKNAMEGIVEN:
		case ERR_NOOPERHOST:
		case ERR_NOORIGIN:
		case ERR_NOPERMFORHOST:
		case ERR_NOPRIVILEGES:
		case ERR_NORECIPIENT:
		case ERR_NOSERVICEHOST:
		case ERR_NOSUCHCHANNEL:
		case ERR_NOSUCHNICK:
		case ERR_NOSUCHSERVER:
		case ERR_NOTDEFINED:
		case ERR_NOTEXTTOSEND:
		case ERR_NOTONCHANNEL:
		case ERR_NOTOPLEVEL:
		case ERR_NOTREGISTERED:
		case ERR_PASSWDMISMATCH:
		case ERR_SUMMONDISABLED:
		case ERR_TOOMANYCHANNELS:
		case ERR_TOOMANYTARGETS:
		case ERR_UMODEUNKNOWNFLAG:
		case ERR_UNKNOWNCOMMAND:
		case ERR_UNKNOWNMODE:
		case ERR_USERNOTINCHANNEL:
		case ERR_USERONCHANNEL:
		case ERR_USERSDISABLED:
		case ERR_USERSDONTMATCH:
		case ERR_WASNOSUCHNICK:
		case ERR_WILDTOPLEVEL:
		case ERR_YOUREBANNEDCREEP:
		case ERR_YOUWILLBEBANNED:
		case RPL_ADMINEMAIL:
		case RPL_ADMINLOC1:
		case RPL_ADMINLOC2:
		case RPL_ADMINME:
		case RPL_AWAY:
		case RPL_BANLIST:
		case RPL_CHANNELMODEIS:
		case RPL_CLOSEEND:
		case RPL_CLOSING:
		case RPL_ENDOFBANLIST:
		case RPL_ENDOFINFO:
		case RPL_ENDOFLINKS:
		case RPL_ENDOFMOTD:
		case RPL_ENDOFNAMES:
		case RPL_ENDOFSERVICES:
		case RPL_ENDOFSTATS:
		case RPL_ENDOFUSERS:
		case RPL_ENDOFWHO:
		case RPL_ENDOFWHOIS:
		case RPL_ENDOFWHOWAS:
		case RPL_INFO:
		case RPL_INFOSTART:
		case RPL_INVITING:
		case RPL_ISON:
		case RPL_KILLDONE:
		case RPL_LINKS:
		case RPL_LIST:
		case RPL_LISTEND:
		case RPL_LISTSTART:
		case RPL_LUSERCHANNELS:
		case RPL_LUSERCLIENT:
		case RPL_LUSERME:
		case RPL_LUSEROP:
		case RPL_LUSERUNKNOWN:
		case RPL_MOTD:
		case RPL_MOTDSTART:
		case RPL_MYPORTIS:
		case RPL_NAMREPLY:
		case RPL_NONE:
		case RPL_NOTOPIC:
		case RPL_NOUSERS:
		case RPL_NOWAWAY:
		case RPL_REHASHING:
		case RPL_SERVICE:
		case RPL_SERVICEINFO:
		case RPL_SERVLIST:
		case RPL_SERVLISTEND:
		case RPL_STATSCLINE:
		case RPL_STATSCOMMANDS:
		case RPL_STATSHLINE:
		case RPL_STATSILINE:
		case RPL_STATSKLINE:
		case RPL_STATSLINKINFO:
		case RPL_STATSLLINE:
		case RPL_STATSNLINE:
		case RPL_STATSOLINE:
		case RPL_STATSQLINE:
		case RPL_STATSUPTIME:
		case RPL_STATSYLINE:
		case RPL_SUMMONING:
		case RPL_TIME:
		case RPL_TOPIC:
		case RPL_TRACECLASS:
		case RPL_TRACECONNECTING:
		case RPL_TRACEHANDSHAKE:
		case RPL_TRACELINK:
		case RPL_TRACELOG:
		case RPL_TRACENEWTYPE:
		case RPL_TRACEOPERATOR:
		case RPL_TRACESERVER:
		case RPL_TRACEUNKNOWN:
		case RPL_TRACEUSER:
		case RPL_UMODEIS:
		case RPL_UNAWAY:
		case RPL_USERHOST:
		case RPL_USERS:
		case RPL_USERSSTART:
		case RPL_VERSION:
		case RPL_WHOISCHANNELS:
		case RPL_WHOISCHANOP:
		case RPL_WHOISIDLE:
		case RPL_WHOISOPERATOR:
		case RPL_WHOISSERVER:
		case RPL_WHOISUSER:
		case RPL_WHOREPLY:
		case RPL_WHOWASUSER:
		case RPL_YOUREOPER:
		default:
			for( IrcActionEvent e : _actionHandlers ) {
				e.eventNumeric(this, r, m);
				e.eventRaw(this, m );
			}
			break;
		
		}
	}
	
	private void _handle_messages(IrcMessages m, Matcher mm) {
		switch(m) {
		case MSG_UNKNOWN: 
		case MSG_ADMIN:
		case MSG_AWAY:
		case MSG_CONNECT:
		case MSG_DIE:
		case MSG_ERROR:
		case MSG_INFO:
		case MSG_INVITE:
		case MSG_ISON:
		case MSG_JOIN:
		case MSG_KICK:
		case MSG_KILL:
		case MSG_LINKS:
		case MSG_LIST:
		case MSG_LUSERS:
		case MSG_MODE:
		case MSG_MOTD:
		case MSG_NAMES:
		case MSG_NICK:
		case MSG_NOTICE:
		case MSG_OPER:
		case MSG_PART:
		case MSG_PASS:
		case MSG_PING:
		case MSG_PONG:
		case MSG_PRIVMSG:
		case MSG_QUIT:
		case MSG_REHASH:
		case MSG_RESTART:
		case MSG_SERVICE:
		case MSG_SERVLIST:
		case MSG_SQUERY:
		case MSG_SQUIT:
		case MSG_STATS:
		case MSG_SUMMON:
		case MSG_TIME:
		case MSG_TOPIC:
		case MSG_TRACE:
		case MSG_USER:
		case MSG_USERHOST:
		case MSG_USERS:
		case MSG_VERSION:
		case MSG_WALLOPS:
		case MSG_WHO:
		case MSG_WHOIS:
		case MSG_WHOWAS:
		default:
			for( IrcActionEvent e : _actionHandlers ) {
				e.eventMsg(this, m, mm);
				e.eventRaw(this, mm );
			}
			break;
		
		}
	}
	
	
	
	public void irc_send_raw(String msg) {
		if( _writer == null) {
			return ;
		}
		_lock_writer.lock();
		try {
			_writer.write(msg);
			_writer.flush( );
		} catch (IOException e) {
			e.printStackTrace();
		}
		_lock_writer.unlock();
	}
	
	public String irc_read_raw() {
		String ret = null;
		_lock_reader.lock();
		try {
			ret = _reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		_lock_reader.unlock();
		return ret;
	}

	
// Managing the IRC channels: joining, leaving, inviting
	
	public void irc_cmd_join(String channel) {
		// Join the channel.
		irc_send_raw( "JOIN " + channel + NEWLINE );
	}
	
	public void irc_cmd_part(String channel) {
		irc_send_raw( "PART " + channel + NEWLINE );
	}
//	irc_cmd_invite
//	irc_cmd_names
//	irc_cmd_list
//	irc_cmd_topic
//	irc_cmd_channel_mode
//	irc_cmd_user_mode
//	irc_cmd_kick
	
// Sending the messages, notices, /me messages and working with CTCP
	
	public void irc_cmd_msg(String target, String text) {
		irc_send_raw( "PRIVMSG " + target + " " + text + NEWLINE );
	}
//	irc_cmd_me
//	irc_cmd_notice
//	irc_cmd_ctcp_request
//	irc_cmd_ctcp_reply
	
// Miscellaneous: library version, changing nick, quitting
//	irc_cmd_nick
//	irc_cmd_whois
//	irc_cmd_quit
//	irc_target_get_nick
//	irc_target_get_host
	
// DCC initiating and accepting chat sessions, sending and receiving files
//	irc_dcc_chat
//	irc_dcc_msg
//	irc_dcc_accept
//	irc_dcc_decline
//	irc_dcc_sendfile
//	irc_dcc_destroy
	
	
	
	
	
	
	
//	private static void test() {
//
//        // Read lines from the server until it tells us we have connected.
////        String line = null;
////        while ((line = reader.readLine( )) != null) {
////            if (line.indexOf("004") >= 0) {
////                // We are now logged in.
////                break;
////            }
////            else if (line.indexOf("433") >= 0) {
////                System.out.println("Nickname is already in use.");
////                return;
////            }
////        }
//       
//	}

}
