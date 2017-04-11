<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<h1>Missions</h1>
<body>
<table border="1">
    <thead>
    <tr>
        <th>Name</th>
        <th>Status</th>
        <th>Required rank</th>
    </tr>
    </thead>
    <c:forEach items="${missions}" var="mission">
        <tr>
            <td><c:out value="${mission.name}"/></td>
            <td><c:out value="${mission.status}"/></td>
            <td><c:out value="${mission.requiredRank}"/></td>
            <td><form method="post" action="${pageContext.request.contextPath}/missions/delete?id=${mission.id}"
                      style="margin-bottom: 0;"><input type="submit" value="Delete"></form></td>
        </tr>
    </c:forEach>
</table>

<h2>Insert mission</h2>
<c:if test="${not empty chyba}">
    <div style="border: solid 1px red; background-color: yellow; padding: 10px">
        <c:out value="${chyba}"/>
    </div>
</c:if>
<form action="${pageContext.request.contextPath}/missions/add" method="post">
    <table>
        <tr>
            <th>Name:</th>
            <td><input type="text" name="name" value="<c:out value='${param.name}'/>"/></td>
        </tr>
        <tr>
            <th>Required rank:</th>
            <td><input type="number" name="rank" value="<c:out value='${param.rank}'/>"/></td>
        </tr>
    </table>
    <input type="Submit" value="Insert" />
</form>

</body>
</html>
