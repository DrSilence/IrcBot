package de.drsilence.twitchapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import de.drsilence.twitchapi.Json.JsonException;


public class TwitchAPI {
	
	public static final String BASEURL = "https://api.twitch.tv/kraken";
	
	private static String doRequest(String fmt) {
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
		
		
		String jsonRaw;
//		jsonRaw = doRequest("");
		jsonRaw = "{\"_links\":{\"user\":\"https://api.twitch.tv/kraken/user\",\"channel\":\"https://api.twitch.tv/kraken/channel\",\"search\":\"https://api.twitch.tv/kraken/search\",\"streams\":\"https://api.twitch.tv/kraken/streams\",\"ingests\":\"https://api.twitch.tv/kraken/ingests\",\"teams\":\"https://api.twitch.tv/kraken/teams\"},\"token\":{\"valid\":false,\"authorization\":null}}";
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
		System.out.println(jsonRaw);
		
		try {
			Object o = Json.parse(jsonRaw);
			System.out.println(o.toString());
		} catch (JsonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
