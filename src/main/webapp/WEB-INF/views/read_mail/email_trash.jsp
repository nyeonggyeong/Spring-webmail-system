<%-- 
    Document   : email_trash
    Created on : 2026. 6. 6., 오전 1:28:00
    Author     : suk22
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>휴지통</title>
        <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/main_style.css" />
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
            <h2>🗑️ 메일 휴지통</h2>
            <p style="color: gray; margin-bottom: 20px;">삭제된 메일들이 보관되는 곳입니다.</p>

            <table>
                <tr>
                    <th>No.</th>
                    <th>보낸 사람</th>
                    <th>제목</th>
                    <th>삭제된 날짜</th>
                    <th>관리</th> </tr>
                        <c:forEach items="${trashList}" var="mail" varStatus="status">
                    <tr style="background-color: #f9f9f9;">
                        <td style="text-align: center;">${status.count}</td>
                        <td style="text-align: center;">${mail.sender}</td>
                        <td>${mail.subject}</td>
                        <td style="text-align: center;">${mail.deletedDate}</td>

                        <td style="text-align: center;">
                            <a href="restore_trash.do?id=${mail.id}">
                                <button type="button" style="background-color: #4CAF50; color: white; border: none; padding: 5px 10px; cursor: pointer;">복구</button>
                            </a>
                            <a href="delete_trash.do?id=${mail.id}" onclick="return confirm('정말 영구 삭제하시겠습니까? 복구할 수 없습니다.');">
                                <button type="button" style="background-color: #f44336; color: white; border: none; padding: 5px 10px; cursor: pointer;">영구 삭제</button>
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty trashList}">
                    <tr>
                        <td colspan="5" style="text-align: center; padding: 20px;">휴지통이 비어있습니다.</td>
                    </tr>
                </c:if>
            </table>
        </div>

        <%@include file="../footer.jspf"%>
    </body>
</html>