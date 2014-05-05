<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
  <head>
   <meta charset="utf-8" />
   <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js "></script>
   <title>NewsReader</title>
  </head>

  <body>
  
  <%
     UserService userService = UserServiceFactory.getUserService();
     User user = userService.getCurrentUser();
     if(user !=null)
     {
	 pageContext.setAttribute("user", user);
     
   %>
     <p>Hello, ${fn:escapeXml(user.nickname)}! (You can
	 <a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>.)</p>
	 
	 <%
     } else {	 
	 %>
	 <p>Hello!
     <a href="<%= userService.createLoginURL(request.getRequestURI()) %>">Sign in</a>
     with your Google account to use the application.</p>
     <%
	      }
     %>
  
	 <form action="/find" method="post">
	  <input type="url" id="urlInput"name="searchText" placeholder="Enter or paste URL here" autofocus required>
	  <input id="submit" type="submit" name="submit" value="Search">
	 </form>
  </body>
</html>
