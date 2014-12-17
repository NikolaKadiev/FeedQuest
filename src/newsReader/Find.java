package newsReader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;

import javax.servlet.http.*;

import newsReader.UrlInfo;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class FindFeeds extends HttpServlet
{
    private static final Logger loger = Logger.getLogger(FindFeeds.class
	    .getName());

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException
    {

	resp.setContentType("aplication/json");

	// validate form input entered by the user
	if (req.getParameter("searchText") != null
		|| !req.getParameter("searchText").equals(""))
	{
	    try
	    {
		ArrayList<String> links = new ArrayList<String>();
		String urlAddress = req.getParameter("searchText");

		// only used to throw MalformedURLException
		// if the input String urlAddress is not a valid url address
		@SuppressWarnings("unused")
		URL testUrlValidity = new URL(urlAddress);

		UrlInfo urlInfo = new UrlInfo(urlAddress);

		// access the Google Feeds API(using the getSimilarFeeds() method) and
		// get the response data
		JSONObject feedsApiResponse = urlInfo.getSimilarFeeds(req
			.getRemoteAddr());

		if (!feedsApiResponse.has("errorMessage"))
		{
		    JSONObject responseData = feedsApiResponse
			    .getJSONObject("responseData");
		    JSONArray entries = responseData.getJSONArray("entries");

		    // loop through the entries array and get the url from each
		    // JSON object and add it to the links list
		    for (int i = 0; i < entries.length(); i++)
		    {
			JSONObject feedEntry = entries.getJSONObject(i);
			links.add(feedEntry.getString("url"));

		    }
		    String json = new Gson().toJson(links);
		    resp.getWriter().write(json);
		} else
		{
		    JSONArray errorMessage = feedsApiResponse
			    .getJSONArray("errorMessage");
		    resp.getWriter().write(errorMessage.toString());
		}

		HttpSession session = req.getSession();
		session.setAttribute("UrlInfo", urlInfo);

	    }

	    catch (JSONException e)
	    {
		loger.log(Level.SEVERE, "JSONException", e);
		resp.sendRedirect("errorPage.html");
	    }

	    catch (MalformedURLException e)
	    {
		loger.log(Level.SEVERE, "MalformedUrl", e);
		resp.sendRedirect("errorPage.html");
	    }

	}

    }

}
