package de.drsilence.irclib;

public enum IrcMessages {
	
	MSG_PASS ( "PASS" ),
	MSG_NICK ( "NICK" ),
	MSG_USER ( "USER" ),
	MSG_OPER ( "OPER" ),
	MSG_MODE ( "MODE" ),
	MSG_SERVICE ( "SERVICE" ),
	MSG_QUIT ( "QUIT" ),
	MSG_SQUIT ( "SQUIT" ),
	MSG_JOIN ( "JOIN" ),
	MSG_PART ( "PART" ),
//	MSG_MODE ( "MODE" ),
	MSG_TOPIC ( "TOPIC" ),
	MSG_NAMES ( "NAMES" ),
	MSG_LIST ( "LIST" ),
	MSG_INVITE ( "INVITE" ),
	MSG_KICK ( "KICK" ),
	MSG_PRIVMSG ( "PRIVMSG" ),
	MSG_NOTICE ( "NOTICE" ),
	MSG_MOTD ( "MOTD" ),
	MSG_LUSERS ( "LUSERS" ),
	MSG_STATS ( "STATS" ),
	MSG_VERSION ( "VERSION" ),
	MSG_LINKS ( "LINKS" ),
	MSG_TIME ( "TIME" ),
	MSG_CONNECT ( "CONNECT" ),
	MSG_TRACE ( "TRACE" ),
	MSG_ADMIN ( "ADMIN" ),
	MSG_INFO ( "INFO" ),
	MSG_SERVLIST ( "SERVLIST" ),
	MSG_SQUERY ( "SQUERY" ),
	MSG_WHO ( "WHO" ),
	MSG_WHOIS ( "WHOIS" ),
	MSG_WHOWAS ( "WHOWAS" ),
	MSG_USERHOST ( "USERHOST" ),
	MSG_KILL ( "KILL" ),
	MSG_PING ( "PING" ),
	MSG_PONG ( "PONG" ),
	MSG_ERROR ( "ERROR" ),
	MSG_AWAY ( "AWAY" ),
	MSG_REHASH ( "REHASH" ),
	MSG_DIE ( "DIE" ),
	MSG_RESTART ( "RESTART" ),
	MSG_SUMMON ( "SUMMON" ),
	MSG_USERS ( "USERS" ),
	MSG_WALLOPS ( "WALLOPS" ),
	MSG_ISON ( "ISON" ),
	MSG_UNKNOWN ( "UNKOWN" );

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
		return MSG_UNKNOWN;
	}

	public String getMsgString() {
		return this._CMD;
	}

}
