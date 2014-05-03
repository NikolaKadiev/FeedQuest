package newsReader;

import java.io.IOException;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

/**
 * Simple utility class used for extracting data from web feeds.
 * @author Nikola Kadiev
 *
 */
public class FeedParser 
{
	private static final Logger loger = Logger.getLogger(Find.class.getName());
	
	/**
	 * Traverses the JSOUP XML Document(Rss or Atom feed) selects each individual feed entry element
	 * and fills a ArrayList with the text values of title,link and description elements.
	 * The ArrayList is filled using a simple FeedEntry object(plain container for the extracted String values)
	 * @param url  web Feed Url Address
	 * @return ArrayList(FeedEntry) - Each FeedEntry object has getters for title,link and description
	 * @throws IOException
	 */
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
			//get all item elements
			Elements rssItemElements  = xmlDoc.select("item");
				
			//fill FeedEntry objects
			for(Element e : rssItemElements)
			{
				//Element e = itemElements.get(i);
				    
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
		    //get all entry elements	
		    Elements atomEntryElements = xmlDoc.select("entry");
		    		
		    //fill FeedEntry objects
		    for(Element e : atomEntryElements)
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
	
	/**
	 * Gets the entire markup content of the provided url. 
	 * @param inputUrl provides the target address 
	 * @return String containing the whole page markup code 
	 * @throws IOException
	 */
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
			loger.log(Level.SEVERE, "MalformedUrl", e);	  
		}
	   	  
	   	 
	        
	 return builder.toString();
	}
	
	
}
