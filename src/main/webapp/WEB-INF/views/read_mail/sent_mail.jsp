<%-- 
    Document   : sent_mail
    Created on : 2026. 6. 6., 오전 1:54:40
    Author     : suk22
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>보낸 메일함</title>
        <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/main_style.css" />
    </head>
    <body>
        <%@include file="../header.jspf"%>

        <div id="sidebar">
            <jsp:include page="../sidebar_menu.jsp" />
        </div>

        <div id="main">
            <h2>보낸 메일함</h2>
            <p style="color: gray; margin-bottom: 20px;">내가 발송한 메일 기록입니다.</p>

            <table>
                <tr>
                    <th>No.</th>
                    <th>받는 사람</th> <th>제목</th>
                    <th>보낸 날짜</th>
                </tr>
                <c:forEach items="${sentList}" var="mail" varStatus="status">
                    <tr style="background-color: #f0f8ff;">
                        <td style="text-align: center;">${status.count}</td>
                        <td style="text-align: center;">${mail.receiver}</td>
                        <td>
                            <a href="show_sent_message?id=${mail.id}" style="text-decoration: none; color: #0066cc; font-weight: bold;">
                                ${mail.subject}
                            </a>
                        </td>
                        <td style="text-align: center;">${mail.sentDate}</td>
                        <td style="text-align: center;">
                            <a href="delete_sent_mail.do?id=${mail.id}" onclick="return confirm('이 발송 내역을 삭제하시겠습니까?');">
                                <button type="button" style="background-color: #f44336; color: white; border: none; padding: 3px 8px; cursor: pointer;">삭제</button>
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty sentList}">
                    <tr>
                        <td colspan="4" style="text-align: center; padding: 20px;">보낸 메일이 없습니다.</td>
                    </tr>
                </c:if>
            </table>
        </div>

        <%@include file="../footer.jspf"%>
    </body>
</html>
