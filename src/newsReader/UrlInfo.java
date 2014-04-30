package newsReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.String;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import newsReader.WordOccurence;


public class UrlInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title = "";
	private String description = "";
	private String keywords[];
	private List<WordOccurence> sortedWords;
	
	
	public UrlInfo(String url)
	{
		Document doc;
		
		try 
		{
			doc = Jsoup.connect(url).get();
			this.title = doc.title();
			
			 for (Element node : doc.getElementsByTag("meta"))
			 {
				
				if(node.attr("name").contains("description"))
				 {
				  this.description = node.attr("content");
				 }
				
				if(node.attr("name").contains("keywords"))
				 {
				  String keywords[] = node.attr("content").split(",");
				  this.keywords =  keywords;
				 } 
			 }
			 
	       
	       sortedWords =  this.keywordsOccurence(doc);
	     
	       
	       
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
        
	}
	
	/**
	 * Parses the JSOUP (HTML) Document, splits the paragraphs into words and returns the words occurence.
	 * @param doc JSOUP Document to analyze
	 * @return List of WordOccurence objects
	 */
	private  List<WordOccurence> keywordsOccurence(Document doc)
	{
		
     HashMap<String, Integer> map = new HashMap<String, Integer>();
		
	 for(Element paragraph : doc.getElementsByTag("p"))
	  {
		String[] words = paragraph.text().split("[^a-zA-Z]+");
		
		for(String Word : words )
		 {
	        String word = Word.toLowerCase();
	        
	        if(!UrlInfo.isInStopList(word))
	      {
			if(map.containsKey(word))
			{
			 map.put(word, map.get(word) + 1);	
			}
			else
			{ 
		     map.put(word, new Integer(1));
			}
	      }
		 }
		
	   }
	 
	    List<WordOccurence> wordsCount = new ArrayList<WordOccurence>();
	 
	    for(Map.Entry<String,Integer> entry : map.entrySet())
	    {
	     wordsCount.add(new WordOccurence(entry.getKey(),entry.getValue())); 
	    }
	 
	    Collections.sort(wordsCount,Collections.reverseOrder());
	    
	 
	    return wordsCount;
	}
		
	public static boolean isInStopList(String word)
	
  {
	  Set<String> stopWords = new LinkedHashSet<String>();
	  BufferedReader reader = null ;
	  String line= "";
      try
      {
	   reader = new BufferedReader(new FileReader("stopWords.txt"));
	  }
	 
	  catch(FileNotFoundException e)
	  {
	   e.getMessage();
	  }
     
      if(reader != null)
      {
	   try
	    {
	     while((line = reader.readLine()) != null )
	     {
	 	  stopWords.add(line.trim());
	     }
	     
	     reader.close();
        }
	   
	    catch(IOException e)
	    {
         e.getMessage();
	    }
     }
	 
	 return stopWords.contains(word) ?  true :  false;
	  
  }
	
    public JSONObject searchFeeds(String ipAddress) throws IOException, JSONException
    {
    	StringBuilder searchQuery = new StringBuilder();
    	int i = 0;
    	
    	//only add the 3 first words of the sortedWords list to the searchQuery
    	 for(WordOccurence word : sortedWords)
    	 {
    		 if(i == 3)
    			 break;
    		
    		 if(i == 0)
    		 {
    		  searchQuery.append(word.key);
    		 }
    		 else
    		 {
    		  searchQuery.append("%20" + word.key);
    		 }
    		  i++;
    		
    	 }
    	
    	
    	URL url = new URL("https://ajax.googleapis.com/ajax/services/feed/find?v=1.0&q=" +
    			          searchQuery.toString() + "&userip=" + ipAddress );
    	
    	URLConnection connection = url.openConnection();
    	connection.setReadTimeout(10000);
    	
    	String line;
    	StringBuilder builder = new StringBuilder();
    	BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    	
    	while((line = reader.readLine()) != null)
    	{
    		builder.append(line);
    	}
    	
    	JSONObject json = new JSONObject(builder.toString());
    	
    	return json;

    	
    	
    }
	public String getDescription() {
		return description;
	}


	public String getTitle() {
		return title;
	}
	
	public String[] getKeywords() {
		return keywords;
	}
	

	

}
