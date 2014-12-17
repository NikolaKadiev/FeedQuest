package newsReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class ProcessContent extends HttpServlet
{

    private static final long serialVersionUID = 1L;
    private static final int MIN_MATCHES_COUNT = 3;

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException
    {
	resp.setContentType("aplication/json");
	FeedParser parser = new FeedParser();

	HttpSession session = req.getSession();
	UrlInfo info = (UrlInfo) session.getAttribute("UrlInfo");
	String feedUrlAddress = req.getParameter("url");

	ArrayList<FeedEntry> feedEntries = parser
		.getFeedEntries(feedUrlAddress);
	ArrayList<WordOccurence> links = sortLinksByDescription(feedEntries,
		info);

	JSONArray jsonResponse = new JSONArray();
	for (WordOccurence wo : links)
	{

	    if (wo.count > MIN_MATCHES_COUNT)
	    {
		JSONObject obj = new JSONObject();
		try
		{
		    obj.put("url", wo.key);
		    obj.put("count", wo.count);
		    jsonResponse.put(obj);
		} catch (JSONException e)
		{
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

	    }

	}

	resp.getWriter().write(jsonResponse.toString());

    }

    /**
     * Sorts the returned WordOccurence objects by the matching words
     * count(Descending) from the Description meta tags
     * <p>
     * Compares the search url Description meta tag with the corresponding
     * Description tag from each individual Feed item.
     * 
     * @param entries
     *            ArrayList of all the feed entries from one Feed
     * @param info
     *            UrlInfo object used to get the description and title from the
     *            entered search url
     * @return List of WordOccurence objects containing the matching words and
     *         the corresponding words count
     */
    private ArrayList<WordOccurence> sortLinksByDescription(
	    ArrayList<FeedEntry> FeedEntries, UrlInfo searchUrl)
    {

	ArrayList<WordOccurence> linkList = new ArrayList<>();

	// add the feed entry link and the keywords that match the description
	// of the entered url ,and enter them in a lit
	for (FeedEntry feedEntry : FeedEntries)
	{
	    String link = feedEntry.getLink();
	    int matches = getMatchingWordsCount(searchUrl.getDescription(),
		    feedEntry.getDescription());
	    linkList.add(new WordOccurence(link, matches));
	}

	Collections.sort(linkList);
	Collections.reverse(linkList);
	return linkList;
    }

    /**
     * Compares two stings and returns the number of matching words
     * 
     * @param urlData
     *            String from the search url (Title or Description)
     * @param feedData
     *            String from the Feed entry(Title or Description)
     * @return Integer - matching words count
     */
    private int getMatchingWordsCount(String urlDescription,
	    String feedEntryDescription)
    {
	String[] urlDescriptionWords = urlDescription.split("[^a-zA-Z]+");
	String[] feedDescriptionWords = feedEntryDescription
		.split("[^a-zA-Z]+");
	int matchingWordsCount = 0;

	for (int i = 0; i < urlDescriptionWords.length; i++)
	{
	    if (UrlInfo.isInStopList(urlDescriptionWords[i]))
	    {
		continue;
	    }

	    for (int j = 0; j < feedDescriptionWords.length; j++)
	    {
		if (urlDescriptionWords[i]
			.equalsIgnoreCase(feedDescriptionWords[j]))
		{
		    matchingWordsCount++;
		}
	    }
	}

	return matchingWordsCount;
    }
}
