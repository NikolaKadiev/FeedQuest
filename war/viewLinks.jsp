<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.ArrayList"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title></head>
<body>

<div id="result">
<c:forEach  var="feed" items="${FeedLinks}">
 <p> <c:out value="${feed}"/> </p>
</c:forEach>
</div>

 <form action="/processContent" method="post">
	 <input type="submit" name="submit">
 </form>


</body>
</html>