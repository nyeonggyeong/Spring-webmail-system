<%-- 
    Document   : address_popup
    Created on : 2026. 6. 7., 오전 3:51:40
    Author     : suk22
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>주소록에서 찾기</title>
        <style>
            body { font-family: 'Malgun Gothic', sans-serif; padding: 20px; background-color: #f9f9f9; }
            table { width: 100%; border-collapse: collapse; background-color: white; }
            th, td { border: 1px solid #ddd; padding: 10px; text-align: center; }
            th { background-color: #f2f2f2; }
            tr:hover { background-color: #f1f8ff; }
            .btn-select { background-color: #4CAF50; color: white; border: none; padding: 5px 10px; cursor: pointer; border-radius: 3px; }
        </style>
        <script>
            function setAddress(email) {
                window.opener.document.querySelector("input[name='to']").value = email;
                window.close(); 
            }
        </script>
    </head>
    <body>
        <h3 style="margin-top: 0;">내 주소록</h3>
        <table>
            <tr>
                <th>이름</th>
                <th>이메일</th>
                <th>선택</th>
            </tr>
            <c:forEach items="${addressList}" var="addr">
                <tr>
                    <td>${addr.name}</td>
                    <td>${addr.email}</td>
                    <td>
                        <button type="button" class="btn-select" onclick="setAddress('${addr.email}')">선택</button>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty addressList}">
                <tr><td colspan="3" style="padding: 20px; color: gray;">저장된 주소록이 없습니다.</td></tr>
            </c:if>
        </table>
    </body>
</html>
