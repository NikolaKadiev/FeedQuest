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
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js ">
	
</script>
<script>
	$(document).ready(function() {
		$("#urlInputFormSubmitButton").click(function() {
			var text = $("#inputUrl").val();
			$.ajax({
				url : "/find",
				type : "get",
				data : {
					searchText : text
				},
				success : function(jsonResponse) {
					$("#feedList").empty();
					var $ul = $('<ul>').appendTo($('#feedList'));
					$.each(jsonResponse, function(index, item) {
						$('<li>').text(item).appendTo($ul);
					});
				},
				error : function(xhr) {
					alert("Error");
				}

			});
		});

		$("#searchFeedsButton").click(function() {
			$("#searchResults").empty();

			$.ajax({
				url : "/processContent",
				type : "get",
				success : function(jsonResponse) {
					var $ul = $('<ul>').appendTo($('#searchResults'));
					$.each(jsonResponse, function(index, item) {
						var $li = $('<li>').text(item).appendTo($ul);
						var link = $("<a>").appendTo($li);
						link.text("Open link");
						link.attr("href", item);
					});

				},
				error : function() {
					alert("Error");
				}

			});

		});

	});
</script>

<title>NewsReader</title>
<style type="text/css">
#signInLink {
	font-size: 20px;
	font-style: italic;
}

#signInMessage {
	font-size: large;
	padding-top: 3em;
}

.signedIn {
	font-size: large;
	padding-top: 3em;
}

div.centre {
	width: 40%;
	height: 20%;
	min-width: 320px;
	min-height: 200px;
	margin: auto;
	background-color: #00ac77;
	text-align: center;
}

}
div.child {
	position: relative;
	margin: auto;
	min-width: 400px;
	min-height: 200px;
}

div.feedList {
	max-width: 800px;
}

div.searcResults {
	max-width: 800px;
}

ul {
	list-style-type: none;
	margin: 0;
	padding: 0;
}

li {
	font: 200 20px/1.5 Helvetica, Verdana, sans-serif;
	border-bottom: 1px solid #ccc;
}

li:last-child {
	border: none;
}

li a {
	text-decoration: none;
	color: #000;
	-webkit-transition: font-size 0.3s ease, background-color 0.3s ease;
	-moz-transition: font-size 0.3s ease, background-color 0.3s ease;
	-o-transition: font-size 0.3s ease, background-color 0.3s ease;
	-ms-transition: font-size 0.3s ease, background-color 0.3s ease;
	transition: font-size 0.3s ease, background-color 0.3s ease;
	display: block;
	width: 200px;
}

li a:hover {
	font-size: 30px;
	background: #f6f6f6;
}
</style>
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
					$("#urlInputForm").css("display", "block");
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

			<div id="urlInputForm" style="display: none;">
				<input id="inputUrl" type="url"
					placeholder="Enter or paste URL here" autofocus required> <input
					id="urlInputFormSubmitButton" type="submit" name="submit"
					value="Search">
			</div>


		</div>

	</div>
	<div>
		<div id="feedList">Default text</div>
		<input id="searchFeedsButton" type="submit" value="SearchFeeds">
	</div>
	<div id="searchResults"></div>
</body>
</html>
