package de.drsilence.irclib;

public abstract class IrcActionEvent {

	public void event_connect(IrcConnection ircio, String event, String origin, String[] params) { }
	public void event_nick(IrcConnection ircio, String event, String origin, String[] params) { }
	public void event_quit(IrcConnection ircio, String event, String origin, String[] params) { }
	public void event_join(IrcConnection ircio, String event, String origin, String[] params) { }
	public void event_part(IrcConnection ircio, String event, String origin, String[] params) { }
	public void event_mode(IrcConnection ircio, String event, String origin, String[] params) { }
	public void event_umode(IrcConnection ircio, String event, String origin, String[] params) { }
	public void event_topic(IrcConnection ircio, String event, String origin, String[] params) { }
	public void event_kick(IrcConnection ircio, String event, String origin, String[] params) { }
	public void event_channel(IrcConnection ircio, String event, String origin, String[] params) { }
	public void event_privmsg(IrcConnection ircio, String event, String origin, String[] params) { }
	public void event_notice(IrcConnection ircio, String event, String origin, String[] params) { }
	public void event_channel_notice(IrcConnection ircio, String event, String origin, String[] params) { }
	public void event_invite(IrcConnection ircio, String event, String origin, String[] params) { }
	public void event_ctcp_req(IrcConnection ircio, String event, String origin, String[] params) { }
	public void event_ctcp_rep(IrcConnection ircio, String event, String origin, String[] params) { }
	public void event_ctcp_action(IrcConnection ircio, String event, String origin, String[] params) { }
	public void event_unknown(IrcConnection ircio, String event, String origin, String[] params) { }
	
	public void event_numeric(IrcConnection ircio, String event, String origin, String[] params) { }
	
	public void event_dcc_chat_req(IrcConnection ircio, String nick, String addr, long ddcid) { }
	public void event_dcc_send_req(IrcConnection ircio, String nick, String addr, String filename, long size, long ddcid) { }
	
	public void event_raw(IrcConnection ircio, String event, String[] params) { }

}

