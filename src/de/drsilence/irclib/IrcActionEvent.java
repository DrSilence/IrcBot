package de.drsilence.irclib;

import java.util.regex.Matcher;

public abstract class IrcActionEvent {

	public void eventMsg(IrcConnection ircio, IrcMessages msg, Matcher m) {}
	public void eventNumeric(IrcConnection ircio, IrcReplies rply, Matcher m) {}
	
	public void eventRaw(IrcConnection ircio, Matcher m) { }

}

