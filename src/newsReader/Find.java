package newsReader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import newsReader.UrlInfo;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class Find extends HttpServlet
{
    private static final Logger loger = Logger.getLogger(Find.class.getName());

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

		UrlInfo info = new UrlInfo(urlAddress);

		// access the Google Feeds API(using the searchFeeds method) and
		// get the response data
		JSONObject jsonObject = info.getSimilarFeeds(req
			.getRemoteAddr());

		if (!jsonObject.has("errorMessage"))
		{
		    JSONObject responseData = jsonObject
			    .getJSONObject("responseData");
		    JSONArray entries = responseData.getJSONArray("entries");

		    // loop through the entries array and get the url from each
		    // JSON
		    // object
		    // and add it to the links list
		    for (int i = 0; i < entries.length(); i++)
		    {
			JSONObject obj = entries.getJSONObject(i);
			links.add(obj.getString("url"));

		    }
		    String json = new Gson().toJson(links);
		    resp.getWriter().write(json);
		} else
		{
		    JSONArray array = jsonObject.getJSONArray("errorMessage");
		    resp.getWriter().write(array.toString());
		}

		req.setAttribute("FeedLinks", links);

		HttpSession session = req.getSession();
		session.setAttribute("FeedLinks", links);
		session.setAttribute("UrlInfo", info);


	    }

	    catch (JSONException e)
	    {
		loger.log(Level.SEVERE, "JSONException", e);
		resp.sendRedirect("errorPage.html");
	    }

	    /**
	     * catch (ServletException e) { loger.log(Level.SEVERE,
	     * "ServletException", e); resp.sendRedirect("errorPage.html"); }
	     **/
	    catch (MalformedURLException e)
	    {
		loger.log(Level.SEVERE, "MalformedUrl", e);
		resp.sendRedirect("errorPage.html");
	    }

	}

    }

}
