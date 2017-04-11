<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@taglib prefix="a" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<h1>Agents</h1>
<body>
<table border="1">
    <thead>
    <tr>
        <th>Name</th>
        <th>Rank</th>
        <th>Alive</th>
    </tr>
    </thead>
    <a:forEach items="${agents}" var="agent">
        <tr>
            <td><a:out value="${agent.name}"/></td>
            <td><a:out value="${agent.rank}"/></td>
            <td><a:out value="${agent.alive}"/></td>
            <td><form method="post" action="${pageContext.request.contextPath}/agents/delete?id=${agent.id}"
                      style="margin-bottom: 0;"><input type="submit" value="Delete"></form></td>
        </tr>
    </a:forEach>
</table>

<h2>Insert agent</h2>
<a:if test="${not empty chyba}">
    <div style="border: solid 1px red; background-color: yellow; padding: 10px">
        <a:out value="${chyba}"/>
    </div>
</a:if>
<form action="${pageContext.request.contextPath}/agents/add" method="post">
    <table>
        <tr>
            <th>Name:</th>
            <td><input type="text" name="name" value="<a:out value='${param.name}'/>"/></td>
        </tr>
        <tr>
            <th>Rank:</th>
            <td><input type="number" name="rank" value="<a:out value='${param.rank}'/>"/></td>
        </tr>
    </table>
    <input type="Submit" value="Insert" />
</form>
</body>
</html>