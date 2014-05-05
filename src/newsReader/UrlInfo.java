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
import java.util.logging.Level;
import java.util.logging.Logger;

import newsReader.WordOccurence;

public class UrlInfo implements Serializable
{

    private static final long serialVersionUID = 1L;
    private String title = "";
    private String description = "";
    private String keywords[];
    private List<WordOccurence> sortedWords;
    private static final Logger loger = Logger.getLogger(Find.class.getName());

    public UrlInfo(String url)
    {
	Document doc;

	try
	{
        //get JSOUP HTML document
	    doc = Jsoup.connect(url).get();
	    this.title = doc.title();
	    // get all meta elements and loop through them
	    for (Element node : doc.getElementsByTag("meta"))
	    {
		// get description from description meta tag
		if (node.attr("name").contains("description"))
		{
		    this.description = node.attr("content");
		}

		// get keywords from keywords meta tag
		// and fill the array using the split() function
		if (node.attr("name").contains("keywords"))
		{
		    this.keywords = node.attr("content").split(",");
		}
	    }
        //populate the list with the words and their count in the opened
		//jsoup document using WordOccurence objects
	    sortedWords = this.keywordsOccurence(doc);

	}

	catch (IOException e)
	{
	    loger.log(Level.SEVERE, "IOException", e);
	}

    }

    /**
     * Parses the JSOUP (HTML) Document, splits the paragraphs into words and
     * returns the words occurence.
     * 
     * @param doc
     *            JSOUP Document to analyze
     * @return List of WordOccurence objects
     */
    private List<WordOccurence> keywordsOccurence(Document doc)
    {

	HashMap<String, Integer> wordsMap = new HashMap<String, Integer>();

	// select all paragraph elements from the document
	for (Element paragraph : doc.getElementsByTag("p"))
	{
	    // split the paragraph into the containing words
	    String[] words = paragraph.text().split("[^a-zA-Z]+");

	    for (String Word : words)
	    {
		//convert word to lowerCase 
		//to avoid adding the same words with different case 
		//more then once
		String word = Word.toLowerCase();

		// ignore the words that match the ones found in the StopList
		// file
		// this is done to eliminate too broad search results
		// and get a precise searchQuery
		if (!UrlInfo.isInStopList(word))
		{
		    if (wordsMap.containsKey(word))
		    {
			// increase word count by 1
			wordsMap.put(word, wordsMap.get(word) + 1);
		    } else
		    {
			// enter the integer value (1) for the word count
			// only executed the first time
			// some word is entered in the map
			wordsMap.put(word, new Integer(1));
		    }
		}
	    }

	}

	List<WordOccurence> wordsCount = new ArrayList<WordOccurence>();

	// enter each map entry(key,value) into its own WordOccurence object
	// and insert the object into a list
	//this is done so we can sort the list
	for (Map.Entry<String, Integer> entry : wordsMap.entrySet())
	{
	    wordsCount.add(new WordOccurence(entry.getKey(), entry.getValue()));
	}
	// Sort the list in descending order
	// Sorting is done by the integer value wordCount
	// of the WordOccurence object
	Collections.sort(wordsCount, Collections.reverseOrder());

	return wordsCount;
    }

    /**
     * Checks if a given word is in the text file containing the words to be
     * ignored.
     * 
     * @param word
     *            String to be checked
     * @return true if String is contained in the text file.
     */
    public static boolean isInStopList(String word)

    {
	Set<String> stopWords = new LinkedHashSet<String>();
	BufferedReader reader = null;
	String line = "";
	try
	{
	    reader = new BufferedReader(new FileReader("stopWords.txt"));

	    if (reader != null)
	    {
		// add each word to the Set
		// the file contains only one word per line
		// so we add the whole line
		while ((line = reader.readLine()) != null)
		{
		    stopWords.add(line.trim());
		}

		reader.close();
	    }
	}

	catch (FileNotFoundException e)
	{
	    loger.log(Level.SEVERE, "FileNotFound", e);
	} catch (IOException e)
	{
	    loger.log(Level.SEVERE, "IOException", e);
	}

	return stopWords.contains(word) ? true : false;

    }

    /**
     * Connects to the Google Feeds API with a custom searchQuery containing the
     * 3 most used word in the paragraphs of this HTML document.
     * 
     * @param ipAddress
     *            User's IP address - required by the Google Feeds API
     * @return JSON object with the information about the returned similar feeds
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject getSimilarFeeds(String ipAddress) throws IOException,
	    JSONException
    {
	StringBuilder searchQuery = new StringBuilder();
	int i = 0;

	// constructing the String with the keywords
	// to be added to the searchQuery
	for (WordOccurence word : sortedWords)
	{
	    // only add the 3 first words of the sortedWords list to the
	    // searchQuery
	    if (i == 3)
		break;

	    if (i == 0)
	    {
		searchQuery.append(word.key);
	    } else
	    {
		searchQuery.append("%20" + word.key);
	    }
	    i++;

	}

	// constructing the searchQuery
	// with the static part of the url
	// and the keywords String we generated
	URL googleFeedsUrl = new URL(
		"https://ajax.googleapis.com/ajax/services/feed/find?v=1.0&q="
			+ searchQuery.toString() + "&userip=" + ipAddress);

	URLConnection connection = googleFeedsUrl.openConnection();
	connection.setReadTimeout(10000);

	String line;
	StringBuilder builder = new StringBuilder();
	BufferedReader reader = new BufferedReader(new InputStreamReader(
		connection.getInputStream()));

	while ((line = reader.readLine()) != null)
	{
	    builder.append(line);
	}

	JSONObject json = new JSONObject(builder.toString());

	return json;

    }

    public String getDescription()
    {
	return description;
    }

    public String getTitle()
    {
	return title;
    }

    public String[] getKeywords()
    {
	return keywords;
    }

}
