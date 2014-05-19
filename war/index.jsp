<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="java.util.List"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<script type="text/javascript"
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js "></script>
	
<script src="main.js"></script>

<link rel="stylesheet" type="text/css" href="style.css">
<title>NewsReader</title>

</head>

<body>
	<div class="centre">
		<div class="child">
			<%
			    UserService userService = UserServiceFactory.getUserService();
						User user = userService.getCurrentUser();
						if (user != null) {
							pageContext.setAttribute("user", user);
			%>
			<p class="signedIn">
				Hello, ${fn:escapeXml(user.nickname)}! (You can <a
					href="<%=userService.createLogoutURL(request.getRequestURI())%>">sign
					out</a>.)
			</p>
			<script>
				$(document).ready(function() {
					$("#urlInputArea").css("display", "block");
				});
			</script>

			<%
			    } else
			    {
			%>
			<p id="signInMessage">
				Hello! <a
					href="<%=userService.createLoginURL(request.getRequestURI())%>"><span
					id="signInLink">Sign in</span></a> with your Google account to use the
				application.
			</p>
			<%
			    }
			%>

			<div id="urlInputArea" style="display: none;">
				<input id="inputUrl" type="url"
					placeholder="Enter or paste URL here..." autofocus required> <button
					id="urlInputAreaSubmitButton" type="submit" name="submit" >SEARCH</button>
					
			</div>


		</div>

	</div>
	<div>
		<div id="feedList"></div>
		<button id="searchFeedsButton" type="submit" style="display: none;">search</button>
	</div>
	<div id="searchResults"></div>
</body>
</html>
