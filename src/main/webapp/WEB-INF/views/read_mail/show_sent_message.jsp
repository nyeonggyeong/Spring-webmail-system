<%-- 
    Document   : show_sent_message
    Created on : 2026. 6. 7., 오전 3:35:55
    Author     : suk22
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>보낸 메일 읽기</title>
        <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/main_style.css" />
    </head>
    <body>
        <%@include file="../header.jspf"%>
        <div id="sidebar">
            <jsp:include page="../sidebar_menu.jsp" />
        </div>
        <div id="main">
            <h2>보낸 메일 상세 보기</h2>
            
            <div style="border: 1px solid #ccc; padding: 20px; background-color: #ffffff; color: #333333; margin-top: 15px;">
                <p style="margin-bottom: 8px;"><strong>받는 사람:</strong> ${mail.receiver}</p>
                <p style="margin-bottom: 8px;"><strong>제목:</strong> ${mail.subject}</p>
                <p style="margin-bottom: 8px;"><strong>보낸 날짜:</strong> ${mail.sentDate}</p>
                
                <hr style="border: 0; border-top: 1px solid #ccc; margin: 15px 0;">
                
                <div style="min-height: 200px; white-space: pre-wrap; color: #000000; line-height: 1.5;">${mail.body}</div>
            </div>
            
            <div style="margin-top: 20px; text-align: right;">
                <a href="sent_mail"><button type="button" style="padding: 5px 15px;">목록으로 돌아가기</button></a>
            </div>
        </div>
        <%@include file="../footer.jspf"%>
    </body>
</html>