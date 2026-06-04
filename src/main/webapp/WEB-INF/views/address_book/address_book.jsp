<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>주소록 관리</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
        <script>
            <c:if test="${!empty msg}">
                alert("${msg}");
            </c:if>
        </script>
    </head>
    <body>
        <%@include file="../header.jspf"%>

        <div id="sidebar">
            <jsp:include page="../sidebar_menu.jsp" />
        </div>

        <div id="main">
            <h2>주소록 등록</h2>
            <form method="POST" action="address_book.do">
                <table>
                    <tr>
                        <td>이름</td>
                        <td><input type="text" name="name" required></td>
                    </tr>
                    <tr>
                        <td>이메일 주소</td>
                        <td><input type="email" name="email" required></td>
                    </tr>
                    <tr>
                        <td>전화번호</td>
                        <td><input type="text" name="phone"></td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <input type="submit" value="등록">
                            <input type="reset" value="다시 입력">
                        </td>
                    </tr>
                </table>
            </form>

            <br><hr><br>

            <h2>현재 주소록 목록</h2>
            <table>
                <tr>
                    <th>이름</th>
                    <th>이메일 주소</th>
                    <th>전화번호</th>
                </tr>
                <c:forEach items="${addressList}" var="addr">
                    <tr>
                        <td>${addr.name}</td>
                        <td>${addr.email}</td>
                        <td>${addr.phone}</td>
                    </tr>
                </c:forEach>
            </table>
        </div>

        <%@include file="../footer.jspf"%>
    </body>
</html>