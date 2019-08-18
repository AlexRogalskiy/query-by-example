<%@page contentType="text/html;charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Search Persons</title>
</head>
<script>
    function submit(action) {
        var form = document.forms[0];
        form.action='${pageContext.request.contextPath}/' + action;
        form.submit();
    }
    function submitJpql() {
        submit('jpql');
    }
    function submitCriteria() {
        submit('criteria');
    }
    function submitQbe() {
        submit('qbe');
    }
</script>
<body>
<h1>Persons</h1>
<h2>Search</h2>
<form method="post">
    <label>First name:
        <input type="text" name="firstName" value="${requestScope.firstName}">
    </label><br>
    <label>Last name:
        <input type="text" name="lastName" value="${requestScope.lastName}">
    </label><br>
    <label>Birthdate:
        <input type="date" name="birthdate" value="${requestScope.birthdate}">
    </label><br>
    <input type="button" value="JPQL Search" onclick="submitJpql()" />
    <input type="button" value="Criteria Search" onclick="submitCriteria()" />
    <input type="button" value="QBE Search" onclick="submitQbe()" />
</form>
<c:if test="${not empty requestScope.persons}">
<h2>Result</h2>
    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>First name</th>
            <th>Last name</th>
            <th>Birthdate</th>
        </tr>
        </thead>
        <tbody>
<c:forEach items="${requestScope.persons}" var="person">
    <tr>
        <td>${person.id}</td>
        <td>${person.firstName}</td>
        <td>${person.lastName}</td>
        <td>${person.birthdate}</td>
    </tr>
</c:forEach>
        </tbody>
    </table>
</c:if>
</body>
</html>
