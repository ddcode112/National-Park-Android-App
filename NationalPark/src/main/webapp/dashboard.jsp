<%--
  Created by IntelliJ IDEA.
  User: wantienchiang
  Date: 4/1/23
  Time: 10:31 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="ds.nationalpark.Record" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%= request.getAttribute("doctype") %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>National Park</title>
</head>
<body>
    <h1>DashBoard</h1>
    <% ArrayList<String> top3Topics = (ArrayList<String>) request.getAttribute("top3topics"); %>
    <% ArrayList<String> top3States = (ArrayList<String>) request.getAttribute("top3states"); %>
    <h2>Top 3 Frequently Searched Topics</h2>
    <ol type="1">
        <% for (int i = 0; i < top3Topics.size(); i++) { %>
        <li><%= top3Topics.get(i) %></li>
        <% } %>
    </ol>
    <h2>Top 3 Frequently Searched States</h2>
    <ol type="1">
        <% for (int i = 0; i < top3States.size(); i++) { %>
        <li><%= top3States.get(i) %></li>
        <% } %>
    </ol>
    <br><br>
    <h2>Entrance Fees Range</h2>
    <p><%=request.getAttribute("minFee")%> - <%=request.getAttribute("maxFee")%></p>
    <h2>Search Records</h2>
    <table>
        <tr>
            <th>Topic</th>
            <th>State</th>
            <th>Query</th>
            <th>National Park</th>
            <th>Minimum Entrance Fee</th>
            <th>Maximum Entrance Fee</th>
            <th>Url</th>
            <th>Search Time</th>
        </tr>
        <% ArrayList<Record> recordList = (ArrayList<Record>) request.getAttribute("records"); %>
        <% for (int i = 0; i < recordList.size(); i++) { %>
            <tr>
                <td><%= recordList.get(i).getTopic() %></td>
                <td><%= recordList.get(i).getState() %></td>
                <td><%= recordList.get(i).getQuery() %></td>
                <td><%= recordList.get(i).getPark() %></td>
                <td><%= recordList.get(i).getMinFee() %></td>
                <td><%= recordList.get(i).getMaxFee() %></td>
                <td><%= recordList.get(i).getUrl() %></td>
                <td><%= recordList.get(i).getTime() %></td>
            </tr>
        <% } %>
    </table>
</body>
</html>
