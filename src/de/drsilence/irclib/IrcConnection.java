package de.drsilence.irclib;

import java.io.*;
import java.net.*;

public class IrcConnection {

	public IrcConnection() {
		super();
	}
	
	public static void init() throws UnknownHostException, IOException {

        // The server to connect to and our details.
//        String server = "irc.freenode.net";
        String server = "irc.twitch.tv";
        String pass   = "oauth:7appgukzxulffvv59u93vbawcuncm5";
        String nick = "da_checker";
        String login = "da_checker";

        // The channel which the bot will join.
//        String channel = "#nope_smokemideveryday";
        String channel = "#da_checker";
        
        // Connect directly to the IRC server.
        Socket socket = new Socket(server, 6667);
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream( )));
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream( )));
        
        // Log on to the server.
        
        writer.write("USER " + login + " 8 * : \r\n");
        writer.write("PASS " + pass + "\r\n");
        writer.write("NICK " + nick + "\r\n");
        writer.flush( );
        
        // Read lines from the server until it tells us we have connected.
        String line = null;
//        while ((line = reader.readLine( )) != null) {
//            if (line.indexOf("004") >= 0) {
//                // We are now logged in.
//                break;
//            }
//            else if (line.indexOf("433") >= 0) {
//                System.out.println("Nickname is already in use.");
//                return;
//            }
//        }
        
        // Join the channel.
        writer.write("JOIN " + channel + "\r\n");
        writer.flush( );
        
        // Keep reading lines from the server.
        while ((line = reader.readLine( )) != null) {
            if (line.toUpperCase( ).startsWith("PING ")) {
                // We must respond to PINGs to avoid being disconnected.
                writer.write("PONG " + line.substring(5) + "\r\n");
                //writer.write("PRIVMSG " + channel + " :I got pinged!\r\n");
                writer.flush( );
                System.out.println(line);
            }
            else {
                // Print the raw line received by the bot.
                System.out.println(line);
                if( line.indexOf("@!quit") > 0 ) {
                	writer.write("PRIVMSG " + channel + " :Yes, of course master!\r\n");
                	writer.write("QUIT :" + "\r\n");
                	writer.flush();
                }
            }
        }
	}

}
