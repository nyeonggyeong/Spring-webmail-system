<%-- 
    Document   : main_menu
    Created on : 2022. 6. 10.
    Author     : skylo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>주메뉴 화면</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
        <script>
            <c:if test="${!empty msg}">
            alert("${msg}");
            </c:if>
        </script>
    </head>
    <body>
        <%@include file="header.jspf"%>

        <div id="sidebar">
            <jsp:include page="sidebar_menu.jsp" />
        </div>

        <div id="main">
            <div style="margin-bottom: 15px; text-align: right;">
                <form action="main_menu" method="GET" style="display: inline-block;">
                    <select name="searchType" style="padding: 5px; vertical-align: middle;">
                        <option value="subject" ${searchType == 'subject' ? 'selected' : ''}>제목</option>
                        <option value="sender" ${searchType == 'sender' ? 'selected' : ''}>보낸사람</option>
                    </select>
                    <input type="text" name="keyword" value="${keyword}" placeholder="검색어 입력" style="padding: 5px; width: 200px; vertical-align: middle;">
                    <button type="submit" style="padding: 5px 15px; background-color: #333; color: white; border: none; cursor: pointer; vertical-align: middle;">검색</button>
                    <c:if test="${!empty keyword}">
                        <button type="button" onclick="location.href='main_menu'" style="padding: 5px 10px; border: 1px solid #ccc; background: #fff; cursor: pointer; vertical-align: middle;">초기화</button>
                    </c:if>
                </form>
            </div>

            ${messageList}
            
            <c:set var="searchQuery" value="" />
            <c:if test="${!empty keyword}">
                <c:set var="searchQuery" value="&searchType=${searchType}&keyword=${keyword}" />
            </c:if>

            <div style="text-align: center; margin-top: 25px; font-family: 'Malgun Gothic', sans-serif;">
                <c:if test="${currentPage > 1}">
                    <a href="main_menu?page=${currentPage - 1}${searchQuery}" style="text-decoration: none; color: #0066cc; margin-right: 12px; font-weight: bold;">[이전]</a>
                </c:if>
                
                <c:forEach var="i" begin="1" end="${totalPages}">
                    <c:choose>
                        <c:when test="${i == currentPage}">
                            <strong style="color: #ef4444; font-size: 1.2em; padding: 0 8px; border-bottom: 2px solid #ef4444;">${i}</strong>
                        </c:when>
                        <c:otherwise>
                            <a href="main_menu?page=${i}${searchQuery}" style="text-decoration: none; color: #333; padding: 0 8px;">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                
                <c:if test="${currentPage < totalPages}">
                    <a href="main_menu?page=${currentPage + 1}${searchQuery}" style="text-decoration: none; color: #0066cc; margin-left: 12px; font-weight: bold;">[다음]</a>
                </c:if>
            </div>
        </div>

        <%@include file="footer.jspf"%>
    </body>
</html>