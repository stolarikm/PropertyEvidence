<%--
  Created by IntelliJ IDEA.
  User: mato
  Date: 7.4.18
  Time: 16:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Property Evidence - Property Database</title>
</head>
<body>
    <h2>Property Database</h2>
    <table border="1">
        <tr>
            <th>Address</th>>
            <th>Area</th>>
            <th>Price</th>>
            <th>Type</th>>
            <th>Action</th>>
        </tr>
        <c:forEach items="${properties}" var="property">
            <tr>
                <td><c:out value="${property.address}"/></td>
                <td><c:out value="${property.area}"/></td>
                <td><c:out value="${property.price}"/></td>
                <td><c:out value="${property.type}"/></td>
                <td>
                    <form method="post" action="${pageContext.request.contextPath}/property/update?id=${property.id}"
                        style="margin-bottom: 0;"><input type="submit" value="Update"></form>
                    <form method="post" action="${pageContext.request.contextPath}/property/delete?id=${property.id}"
                        style="margin-bottom: 0;"><input type="submit" value="Delete"></form>


                </td>
            </tr>>
        </c:forEach>
    </table>
    <br/>
    <hr/>

    <c:if test="${not empty error}">
        <div style="border: solid 1px red; background-color: yellow; padding: 10px">
            <c:out value="${error}"/>
        </div>
    </c:if>

    <c:if test="${empty propertyToBeUpdated}">
        <h2>Create new property</h2>
        <form action="${pageContext.request.contextPath}/property/create" method="post">
            <table>
                <tr>
                    <th align="right">Address:</th>
                    <td><input type="text" name="address" value="<c:out value='${param.address}'/>"/></td>
                </tr>
                <tr>
                    <th align="right">Area:</th>
                    <td><input type="number" step="0.01" name="area" value="<c:out value='${param.area}'/>"/></td>
                </tr>
                <tr>
                    <th align="right">Price:</th>
                    <td><input type="number" step="0.01" name="price" value="<c:out value='${param.price}'/>"/></td>
                </tr>
                <tr>
                    <th align="right">Type:</th>
                    <td><input type="text" name="type" value="<c:out value='${param.type}'/>"/></td>
                </tr>
            </table>
            <input type="Submit" value="Create"/>
        </form>
    </c:if>


    <c:if test="${not empty propertyToBeUpdated}">
        <h2>Update property</h2>
        <form action="${pageContext.request.contextPath}/property/updateConfirm?id=${propertyToBeUpdated.id}" method="post">
            <table>
                <tr>
                    <th align="right">Address:</th>
                    <td><input type="text" name="address" value="<c:out value='${propertyToBeUpdated.address}'/>"/></td>
                </tr>
                <tr>
                    <th align="right">Area:</th>
                    <td><input type="number" name="area" step="0.01" value="<c:out value='${propertyToBeUpdated.area}'/>"/></td>
                </tr>
                <tr>
                    <th align="right">Price:</th>
                    <td><input type="number" name="price" step="0.01" value="<c:out value='${propertyToBeUpdated.price}'/>"/></td>
                </tr>
                <tr>
                    <th align="right">Type:</th>
                    <td><input type="text" name="type" value="<c:out value='${propertyToBeUpdated.type}'/>"/></td>
            </table>
            <input type="Submit" value="Update"/>
        </form>
    </c:if>
</body>
</html>
