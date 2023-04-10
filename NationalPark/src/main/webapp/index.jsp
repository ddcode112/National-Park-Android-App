<%--
@author Candice Chiang
Andrew id: wantienc
Last Modified: Apr 5, 2023
Initial View
--%>
<%@ page import="org.json.JSONArray" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%= request.getAttribute("doctype") %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>National Park</title>
</head>
<body>
<h1><%= "Search National Parks" %></h1>

<% JSONArray topics = new JSONArray(request.getAttribute("topicList").toString()); %>
<% JSONArray states = new JSONArray(request.getAttribute("stateList").toString()); %>
<form action="NationalPark" method="GET">
    <label for="topic">Select a topic:</label>
    <select name="topic" id="topic">
        <% for (int i = 0; i < topics.length(); i++) { %>
        <option value="<%= topics.getString(i) %>"><%= topics.getString(i) %></option>
        <% } %>
    </select>
    <br><br>
    <label for="stateCode">Choose a state:</label>
    <select name="stateCode" id="stateCode">
        <% for (int i = 0; i < states.length(); i++) { %>
        <option value="<%= states.getString(i) %>"><%= states.getString(i) %></option>
        <% } %>
    </select>
    <br><br>
    <label>Type a keyword.</label>
    <input type="text" name="q" value="" /><br><br>
    <input type="submit" value="Submit">
</form>
</body>
</html>