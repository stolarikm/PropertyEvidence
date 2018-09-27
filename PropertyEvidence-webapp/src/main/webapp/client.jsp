<%--
  Created by IntelliJ IDEA.
  User: stolarikm
  Date: 6.4.2018
  Time: 11:49
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Property Evidence - Client database</title>
</head>
<body>
    <h2>Client database</h2>

    <table border="1">
        <tr>
            <th>Full name</th>
            <th>Phone number</th>
            <th>Action</th>
        </tr>
        <c:forEach items="${clients}" var="client">
            <tr>
                <td><c:out value="${client.fullName}"/></td>
                <td><c:out value="${client.phoneNumber}"/></td>
                <td>
                    <form method="post" action="${pageContext.request.contextPath}/client/delete?id=${client.id}"
                          style="margin-bottom: 0;"><input type="submit" value="Delete"></form>
                    <form method="post" action="${pageContext.request.contextPath}/client/update?id=${client.id}"
                          style="margin-bottom: 0;"><input type="submit" value="Update"></form>
                </td>
            </tr>
        </c:forEach>
    </table>
    <br/>
    <hr/>

    <c:if test="${not empty error}">
        <div style="border: solid 1px red; background-color: yellow; padding: 10px">
            <c:out value="${error}"/>
        </div>
    </c:if>

    <c:if test="${empty clientToUpdate}">
        <h2>Create new client</h2>
        <form action="${pageContext.request.contextPath}/client/create" method="post">
            <table>
                <tr>
                    <th align="right">Name:</th>
                    <td><input type="text" name="name" value="<c:out value='${param.fullName}'/>"/></td>
                </tr>
                <tr>
                    <th align="right">Phone number:</th>
                    <td><input type="text" name="phone" value="<c:out value='${param.phoneNumber}'/>"/></td>
                </tr>
            </table>
            <input type="Submit" value="Create"/>
        </form>
    </c:if>

    <c:if test="${not empty clientToUpdate}">
        <h2>Update client</h2>
        <form action="${pageContext.request.contextPath}/client/updateConfirm?id=${clientToUpdate.id}" method="post">
            <table>
                <tr>
                    <th align="right">Name:</th>
                    <td><input type="text" name="name" value="<c:out value='${clientToUpdate.fullName}'/>"/></td>
                </tr>
                <tr>
                    <th align="right">Phone number:</th>
                    <td><input type="text" name="phone" value="<c:out value='${clientToUpdate.phoneNumber}'/>"/></td>
                </tr>
            </table>
            <input type="Submit" value="Update"/>
        </form>
    </c:if>



</body>
</html>
