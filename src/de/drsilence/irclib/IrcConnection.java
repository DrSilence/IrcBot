package de.drsilence.irclib;

import java.lang.Runnable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class IrcConnection implements Runnable {
	
	private static final String NEWLINE = "\r\n";
	
	private BufferedWriter _writer = null;
	private BufferedReader _reader = null;
	
	private ReentrantLock _lock_writer = new ReentrantLock();
	private ReentrantLock _lock_reader = new ReentrantLock();
	
	private Pattern pattern = Pattern.compile(
			"(?<rawmessage>\\:(?<source>((?<nick>[^!]+)![~]{0,1}(?<user>[^@]+)@)?(?<host>[^\\s]+)) (?<command>[^\\s]+)( )?(?<parameters>[^:]+){0,1}(:)?(?<text>[^\\r^\\n]+)?)"
			);
	
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
			// Handle raw message:
			_handle_raw_message( line );
			// Print received line raw to console.
			//System.out.println( (char)27 + "[31m" + line + (char)27 + "[0m");
			//System.out.println( line );
		}
	}
	
	private Matcher _parse_raw_message(String msg_raw) {
		Matcher m = pattern.matcher(msg_raw);
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
			_parse_raw_message( line );
		} else {
			// PING or Client->Client.
			if( line.toUpperCase().startsWith("PING ") ) {
				// We must respond to PINGs to avoid being disconnected.
                irc_send_raw( "PONG " + line.substring(5) + NEWLINE );
			}
		};
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
