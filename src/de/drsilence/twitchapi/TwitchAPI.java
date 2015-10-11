package de.drsilence.twitchapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import de.drsilence.twitchapi.Json.JsonException;


public class TwitchAPI {
	
	public static enum TwitchRequests {
		REQUEST_BASIC		(false, "https://api.twitch.tv/kraken"),
		REQUEST_user		(false, "https://api.twitch.tv/kraken/user"),
		REQUEST_channel		(false, "https://api.twitch.tv/kraken/channel"),
		REQUEST_search		(false, "https://api.twitch.tv/kraken/search"),
		REQUEST_streams		(false, "https://api.twitch.tv/kraken/streams"),
		REQUEST_ingests		(false, "https://api.twitch.tv/kraken/ingests"),
		REQUEST_teams		(false, "https://api.twitch.tv/kraken/teams");
		
		private boolean needsAuth;
		private String url;
		
		private TwitchRequests(boolean needsAuth, String url) {
			this.needsAuth = needsAuth;
			this.url = url;
		}
		
		public boolean needsAuth() { return this.needsAuth; }
		public String getUrl() { return this.url; }
	}
	
	public static final String BASEURL = "https://api.twitch.tv/kraken";
	
	private boolean isAuthDone;
	
	public TwitchAPI() {
		super();
		this.isAuthDone = false;
	}
	
	private String doRequest(String method, String url) {
		StringBuilder sb = new StringBuilder();
		URL u;
		try {
			u = new URL(BASEURL + url);
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setDoInput(true);
			//give server some time to respond:
			String ret = conn.getResponseCode() + " : " + conn.getResponseMessage();
			//handle server output:
			InputStream r = conn.getInputStream();
			int c;
			while((c=r.read())!=-1) {
				sb.append((char)c);
//				System.err.print((char)c);
			}
			conn.disconnect();
//			System.err.println(ret+"\n"+sb.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	
	private String doRequest(String fmt) {
		StringBuilder jsonRaw = new StringBuilder();
		try ( 
				InputStream 		is 	= new URL(BASEURL+fmt).openStream();
				InputStreamReader 	isr = new InputStreamReader(is);
				BufferedReader 		rd 	= new BufferedReader(isr); 
		) {
			int cp;
			while((cp=rd.read())!=-1) {
				jsonRaw.append((char)cp);
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return jsonRaw.toString();
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		TwitchAPI api = new TwitchAPI();
		String jsonRaw;
		jsonRaw = api.doRequest("GET", "");
//		jsonRaw = api.doRequest("");
//		jsonRaw = api.doRequest("/channel");
//		jsonRaw = "{\"_links\":{\"user\":\"https://api.twitch.tv/kraken/user\",\"channel\":\"https://api.twitch.tv/kraken/channel\",\"search\":\"https://api.twitch.tv/kraken/search\",\"streams\":\"https://api.twitch.tv/kraken/streams\",\"ingests\":\"https://api.twitch.tv/kraken/ingests\",\"teams\":\"https://api.twitch.tv/kraken/teams\"},\"token\":{\"valid\":false,\"authorization\":null}}";
/*
 * 	{
 * 		"_links":{
 * 			"user"		: "https://api.twitch.tv/kraken/user",
 * 			"channel"	: "https://api.twitch.tv/kraken/channel",
 * 			"search"	: "https://api.twitch.tv/kraken/search",
 * 			"streams"	: "https://api.twitch.tv/kraken/streams",
 * 			"ingests"	: "https://api.twitch.tv/kraken/ingests",
 * 			"teams"		: "https://api.twitch.tv/kraken/teams"
 * 		},
 * 		"token":{
 * 			"valid":false,
 * 			"authorization":null
 * 		}
 * }
 */
//		System.out.println(jsonRaw);
		
		try {
			Object o = Json.parse(jsonRaw);
			System.out.println(o.toString());
		} catch (JsonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
