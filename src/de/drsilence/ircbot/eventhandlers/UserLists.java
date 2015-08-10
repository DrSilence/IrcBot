package de.drsilence.ircbot.eventhandlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import de.drsilence.irclib.IrcActionEvent;
import de.drsilence.irclib.IrcConnection;
import de.drsilence.irclib.IrcMessages;

public class UserLists extends IrcActionEvent {

	@SuppressWarnings("rawtypes")
	private class IrcUser implements Comparable {
		String nick;
		String modes;
		public IrcUser(String nick, String modes) { super(); this.nick = nick; this.modes = modes; }
		public void modifyModes(String modifiers) {
			if(modifiers.isEmpty()) return;
			char[] cs = modifiers.substring(1).toCharArray();
			char mode = modifiers.charAt(0);
			for(char c : cs) {
				int found = this.modes.indexOf(c);
				if(mode=='+' && found<0) {
					this.modes += c;
				} else if(mode=='-' && found>-1) {
					this.modes.replace(c, ' ');
				}
			}
			cs = this.modes.trim().toCharArray();
			Arrays.sort(cs);
			this.modes = new String(cs);
		}
		@Override
		public int compareTo(Object o) {
			if(o instanceof IrcUser) {
				IrcUser other=(IrcUser)o; 
				int modDif = this.modes.compareTo(other.modes);
				int nicDif = this.nick.compareTo(other.nick);
				if(modDif != 0 ) return modDif;
				return nicDif;
			}
			return 0;
		}
	}
	
	private class IrcChannel {
		String channel;
		List<IrcUser> users = new ArrayList<IrcUser>();
		public IrcChannel(String channel) { super(); this.channel = channel; }
		public void addUser(String nick,String modes) {	users.add(new IrcUser(nick,modes));	}
		public void removeUser(String nick) { for(IrcUser u : users) if(u.nick.equals(nick)) users.remove(u); }
		public IrcUser findUser(String nick) {
			for( IrcUser u : users ) if(u.nick.equalsIgnoreCase(nick)) return u;
			return null;
		}
		public void modifyUser(String nick, String modes) {
			IrcUser u=findUser(nick);
			if(u!=null) { u.modifyModes(modes); } else { addUser(nick, modes); }
		}
		public void sortUsers() { Collections.sort(users); }
	}
	
	private List<IrcChannel> _channels = new ArrayList<IrcChannel>();
	
	private IrcChannel findChannel(String channel) {
		for(IrcChannel c : _channels) if(c.channel.equals(channel)) return c;
		return null;
	}
	
	private void addChannel(String channel) {
		_channels.add(new IrcChannel(channel));
	}
	
	private void removeChannel(String channel) {
		for(IrcChannel c : _channels) if(c.channel.equals(channel)) _channels.remove(c);
	}
	
	private void addUserToChannel(String nick, String channel, String modes) {
		IrcChannel c = findChannel(channel);
		if(c==null) {
			c= new IrcChannel(channel);
			_channels.add(c);
		}
		c.modifyUser(nick, modes);
	}
	
	private void removeUserFromChannel(String nick, String channel) {
		IrcChannel c = findChannel(channel);
		if(c!=null) c.removeUser(nick);
	}

	public List<String> getUsers(String channel) {
		IrcChannel c = findChannel(channel);
		if(c==null) return null;
		List<String> l = new ArrayList();
		for(IrcUser u : c.users) l.add( u.modes + "|" + u.nick );
		return l;
	}



	@Override
	public void eventMsg(IrcConnection ircio, IrcMessages msg, Matcher m) {
		super.eventMsg(ircio, msg, m);
		String nick 	= "";
		String channel 	= "";
		String modes 	= "";
		switch( msg ) {
			case MSG_PRIVMSG:
				String txt = m.group("text").trim();
				if( txt.startsWith("!debug_userlist") ) {
					List<String> u = getUsers("#da_checker");
					for( String s : u )
						System.err.println(s);
				}
			case MSG_JOIN:
				nick 	= m.group("nick").trim();
				channel = m.group("parameters").trim();
				addUserToChannel(nick, channel, modes);
				break;
				
			case MSG_PART:
				///TODO: remove channel if bot leaves.
				nick 	= m.group("nick").trim();
				channel = m.group("parameters").trim();
				removeUserFromChannel(nick, channel);
				break;
				
			case MSG_MODE:
				String[] s = m.group("parameters").trim().split(" ");
				nick 	= s[2];
				channel = s[0];
				modes 	= s[1];
				addUserToChannel(nick, channel, modes);
				break;
				
			default:
				break;
					
		}
	}

	
}
