package newsReader;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ProcessContent extends HttpServlet
{

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException
    {
	PrintWriter writer = resp.getWriter();

	FeedParser parser = new FeedParser();

	HttpSession session = req.getSession();
	ArrayList<String> feedLinks = (ArrayList<String>) session
		.getAttribute("FeedLinks");
	UrlInfo info = (UrlInfo) session.getAttribute("UrlInfo");

	ArrayList<WordOccurence> links = new ArrayList<>();
	ArrayList<FeedEntry> results;

	for (String link : feedLinks)
	{

	    results = parser.getFeedEntries(link);

	    for (WordOccurence wo : sortByContent(results, info))
	    {
		links.add(wo);
	    }

	}

	Collections.sort(links, Collections.reverseOrder());

	for (WordOccurence wo : links)
	{
	    if (wo.count > 0)
	    {
		writer.println(wo.key);
		writer.println("<br/>");
		writer.println(wo.count);
	    }
	}

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
    private ArrayList<WordOccurence> sortByContent(
	    ArrayList<FeedEntry> entries, UrlInfo info)
    {
	HashMap<String, Integer> contentMap = new HashMap<>();
	ArrayList<WordOccurence> linkList = new ArrayList<>();

	// add the feed entry link and the keywords that match the description
	// of the entered url ,and enter them in a map
	for (FeedEntry e : entries)

	{
	    int matches = matchingKeywors(info.getDescription(),
		    e.getDescription());

	    String link = e.getLink();

	    // avoid entering same keys and overwriting the value
	    if (!contentMap.containsKey(link))
	    {
		contentMap.put(link, matches);
	    }

	}

	// get the key/value pairs from the map and create plain objects
	// that can be ordered in a collection with Collections.Sort
	for (Map.Entry<String, Integer> entry : contentMap.entrySet())
	{
	    linkList.add(new WordOccurence(entry.getKey(), entry.getValue()));
	}

	Collections.sort(linkList, Collections.reverseOrder());

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
    private int matchingKeywors(String urlData, String feedData)
    {
	String[] urlWords = urlData.split("[^a-zA-Z]+");
	String[] feedWords = feedData.split("[^a-zA-Z]+");
	int MatchingWordsCount = 0;

	for (int i = 0; i < urlWords.length; i++)
	{
	    if (UrlInfo.isInStopList(urlWords[i]))
	    {
		continue;
	    }

	    for (int j = 0; j < feedWords.length; j++)
	    {
		if (urlWords[i].equalsIgnoreCase(feedWords[j]))
		{
		    MatchingWordsCount++;
		}
	    }
	}

	return MatchingWordsCount;
    }
}
