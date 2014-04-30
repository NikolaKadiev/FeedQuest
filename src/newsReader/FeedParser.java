package newsReader;

import java.io.IOException;
import java.net.*;
import java.io.*;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class FeedParser 
{
	
	
	public  ArrayList<FeedEntry> getFeedEntries(String url) throws IOException
	{
		ArrayList<FeedEntry> webSyndicationItems = new ArrayList<FeedEntry>();
		
		String html = this.getHTML(url);
		Document xmlDoc = Jsoup.parse(html,"", Parser.xmlParser());
		Elements root;
		FeedEntry rssEntry, atomEntry;
		
		
			//process RSS feeds
			if(xmlDoc.select("rss") != null)
			{
			   
			   Elements itemElements  = xmlDoc.select("item");
			   
			   for(int i = 0; i < itemElements.size();i++)
			   {
				   Element e = itemElements.get(i);
				   
				   
				   rssEntry = new FeedEntry();
				   
				   rssEntry.setTitle(e.getElementsByTag("title").text());
				   rssEntry.setLink(e.getElementsByTag("link").text());
				   rssEntry.setDescription(e.getElementsByTag("description").text());
				   
				   webSyndicationItems.add(rssEntry);
			   }
			   
			}
			
			//process ATOM feeds
		    if((root = xmlDoc.select("feed")) != null && root.attr("xmlns").contains("http://www.w3.org/2005/Atom"))
			{
			  Elements atomEntries = xmlDoc.select("entry");
			   
			    for(Element e : atomEntries)
			   {
			      atomEntry = new FeedEntry();
			      
			      atomEntry.setTitle(e.getElementsByTag("title").text());
			      atomEntry.setDescription(e.getElementsByTag("summary").text());
			      atomEntry.setLink(e.getElementsByTag("link").attr("href"));
				 
				  webSyndicationItems.add(atomEntry); 
			   }	
			}
			
			
	 return webSyndicationItems;
	}
	
	
	private String getHTML(String inputUrl) throws IOException
	{
		StringBuilder builder = new StringBuilder();
		URL url;
		
		try
		{
	   	  url = new URL(inputUrl);
	   	  URLConnection conn = url.openConnection();
	   	  conn.setReadTimeout(10000);
	      BufferedReader in = new BufferedReader(
	      new InputStreamReader(conn.getInputStream()));
	      
	      String line;
	      
	        while ((line = in.readLine()) != null)
	         {
	          builder.append(line);
	         }
	        
	        in.close();
	   	}
		
		catch(MalformedURLException e)
		{
		 e.printStackTrace();
		}
	   	  
	   	 
	        
	        return builder.toString();
	}
	
	
}
