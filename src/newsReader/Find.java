package newsReader;

import java.io.IOException;

import newsReader.UrlInfo;

//import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class Find extends HttpServlet {
	ArrayList<String> links = new ArrayList<String>();
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException 
  {
		String url;
		resp.setContentType("text/plain");
		url = req.getParameter("searchText");
		
		UrlInfo info = new UrlInfo(url);
       //PrintWriter out = resp.getWriter();
        
       // out.println(info.getTitle());
       // out.println(info.getDescription());
        
       // for(String word : info.getKeywords())
      //  {
      //  out.println(word);
      //  }
        
        //for(WordOccurence word : info.orderedValues )
       // {
      //  out.println(word.key + ":" + word.count);	
      //  }
        
          
       try 
       {
    	    JSONObject  jsonObject = info.searchFeeds(req.getRemoteAddr());
		    JSONObject  responseData = jsonObject.getJSONObject("responseData");
		    JSONArray  entries = responseData.getJSONArray("entries");
		    
		    for(int i=0;i<entries.length();i++)
		    {
		    	JSONObject obj = entries.getJSONObject(i);
		    	links.add( obj.getString("url"));
		    }
		    
       }
	   catch (JSONException e) 
		    {
             e.printStackTrace();
	        }
 
    	req.setAttribute("FeedLinks", links);
    	
    	try {
    		HttpSession session = req.getSession();
    		session.setAttribute("FeedLinks", links);
    		session.setAttribute("UrlInfo", info);
			req.getRequestDispatcher("viewLinks.jsp").forward(req, resp);
			
			url = null;
		} catch (ServletException e) {
			
			e.printStackTrace();
		}
    	
       
        
 }
	
	
}
