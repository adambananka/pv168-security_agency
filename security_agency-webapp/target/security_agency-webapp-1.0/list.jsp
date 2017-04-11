<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@taglib prefix="m" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <m:forEach items="${missions}" var="mission">
        <tr>
            <td><m:out value="${mission.name}"/></td>
            <td><m:out value="${mission.status}"/></td>
            <td><m:out value="${mission.requiredRank}"/></td>
            <td><form method="post" action="${pageContext.request.contextPath}/missions/initUpdate?id=${mission.id}"
                      style="margin-bottom: 0;"><input type="submit" value="Update"></form> </td>
            <td><form method="post" action="${pageContext.request.contextPath}/missions/delete?id=${mission.id}"
                      style="margin-bottom: 0;"><input type="submit" value="Delete"></form></td>
        </tr>
    </m:forEach>
</table>

<m:if test="${empty update}">
    <h2>Insert mission</h2>
    <m:if test="${not empty error}">
        <div style="border: solid 1px red; background-color: yellow; padding: 10px">
            <m:out value="${error}"/>
        </div>
    </m:if>
    <form action="${pageContext.request.contextPath}/missions/add" method="post">
        <table>
            <tr>
                <th>Name:</th>
                <td><input type="text" name="name" value="<m:out value='${param.name}'/>"/></td>
            </tr>
            <tr>
                <th>Required rank:</th>
                <td><input type="number" name="rank" value="<m:out value='${param.rank}'/>"/></td>
            </tr>
        </table>
        <input type="Submit" value="Insert" />
    </form>
</m:if>

<m:if test="${not empty update}">
    <h2>Update mission</h2>
    <m:if test="${not empty error}">
        <div style="border: solid 1px red; background-color: yellow; padding: 10px">
            <m:out value="${error}"/>
        </div>
    </m:if>
    <form action="${pageContext.request.contextPath}/missions/update" method="post">
        <table>
            <tr>
                <th>Name:</th>
                <td><input type="text" name="name" value="<m:out value='${param.name}'/>"/></td>
            </tr>
            <tr>
                <th>Status: (0=NOT_ASSIGNED, 1=IN_PROGRESS, 2=ACCOMPLISHED, 3=FAILED)</th>
                <td><input type="number" name="status" value="<m:out value='${param.status}'/>"/></td>
            </tr>
            <tr>
                <th>Required rank:</th>
                <td><input type="number" name="rank" value="<m:out value='${param.rank}'/>"/></td>
            </tr>
        </table>
        <input type="Submit" value="Update" />
    </form>
</m:if>
</body>
</html>
