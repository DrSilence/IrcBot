package de.drsilence.irclib;

public enum IrcMessages {
	
	IRCMSG_PASS ( "PASS" ),
	IRCMSG_NICK ( "NICK" ),
	IRCMSG_USER ( "USER" ),
	IRCMSG_OPER ( "OPER" ),
	IRCMSG_MODE ( "MODE" ),
	IRCMSG_SERVICE ( "SERVICE" ),
	IRCMSG_QUIT ( "QUIT" ),
	IRCMSG_SQUIT ( "SQUIT" ),
	IRCMSG_JOIN ( "JOIN" ),
	IRCMSG_PART ( "PART" ),
//	IRCMSG_MODE ( "MODE" ),
	IRCMSG_TOPIC ( "TOPIC" ),
	IRCMSG_NAMES ( "NAMES" ),
	IRCMSG_LIST ( "LIST" ),
	IRCMSG_INVITE ( "INVITE" ),
	IRCMSG_KICK ( "KICK" ),
	IRCMSG_PRIVMSG ( "PRIVMSG" ),
	IRCMSG_NOTICE ( "NOTICE" ),
	IRCMSG_MOTD ( "MOTD" ),
	IRCMSG_LUSERS ( "LUSERS" ),
	IRCMSG_STATS ( "STATS" ),
	IRCMSG_VERSION ( "VERSION" ),
	IRCMSG_LINKS ( "LINKS" ),
	IRCMSG_TIME ( "TIME" ),
	IRCMSG_CONNECT ( "CONNECT" ),
	IRCMSG_TRACE ( "TRACE" ),
	IRCMSG_ADMIN ( "ADMIN" ),
	IRCMSG_INFO ( "INFO" ),
	IRCMSG_SERVLIST ( "SERVLIST" ),
	IRCMSG_SQUERY ( "SQUERY" ),
	IRCMSG_WHO ( "WHO" ),
	IRCMSG_WHOIS ( "WHOIS" ),
	IRCMSG_WHOWAS ( "WHOWAS" ),
	IRCMSG_USERHOST ( "USERHOST" ),
	IRCMSG_KILL ( "KILL" ),
	IRCMSG_PING ( "PING" ),
	IRCMSG_PONG ( "PONG" ),
	IRCMSG_ERROR ( "ERROR" ),
	IRCMSG_AWAY ( "AWAY" ),
	IRCMSG_REHASH ( "REHASH" ),
	IRCMSG_DIE ( "DIE" ),
	IRCMSG_RESTART ( "RESTART" ),
	IRCMSG_SUMMON ( "SUMMON" ),
	IRCMSG_USERS ( "USERS" ),
	IRCMSG_WALLOPS ( "WALLOPS" ),
	IRCMSG_ISON ( "ISON" ),
	IRCMSG_UNKNOWN ( "UNKOWN" );

	private String _CMD;

	private IrcMessages( String cmd ) {
		this._CMD = cmd;
	}
	
	public static IrcMessages getEnumByValue( String s ) {
		IrcMessages t[] = IrcMessages.values();
		s = s.trim();
		for( IrcMessages m : t ) {
			if( s.equals(m._CMD) ) {
				return m;
			}
		}
		return IRCMSG_UNKNOWN;
	}

	public String getMsgString() {
		return this._CMD;
	}

}
