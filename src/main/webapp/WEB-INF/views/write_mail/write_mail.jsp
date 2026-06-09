<%-- 
    Document   : write_mail.jsp
    Author     : jongmin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>메일 쓰기 화면</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
        
        <script src="https://code.jquery.com/jquery-3.4.1.slim.min.js"></script>
        <link href="https://cdn.jsdelivr.net/npm/summernote@0.8.18/dist/summernote-lite.min.css" rel="stylesheet">
        <script src="https://cdn.jsdelivr.net/npm/summernote@0.8.18/dist/summernote-lite.min.js"></script>
    </head>
    <body>
        <%@include file="../header.jspf"%>

        <div id="sidebar">
            <jsp:include page="../sidebar_previous_menu.jsp" />
        </div>

        <div id="main">
            <form enctype="multipart/form-data" method="POST" action="write_mail.do" >
                <table>
                    <tr>
                        <td> 수신 </td>
                        <td> 
                            <input type="text" name="to" size="63" id="toField" required value="${!empty param['sender'] ? param['sender'] : ''}">
                            <button type="button" onclick="openAddressBook()" style="padding: 3px 8px; margin-left: 5px; cursor: pointer; background-color: #6c757d; color: white; border: none; border-radius: 3px;">주소록에서 찾기</button>
                        </td>
                    </tr>
                    <tr>
                        <td>참조</td>
                        <td> <input type="text" name="cc" size="80">  </td>
                    </tr>
                    <tr>
                        <td> 메일 제목 </td>
                        <td> <input type="text" name="subj" size="80" 
                                    value="${!empty param['sender'] ? "RE: " += sessionScope['subject'] : ''}" >  </td>
                    </tr>
                    <tr>
                        <td colspan="2">본  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 문</td>
                    </tr>
                    <tr>  
                        <td colspan="2">
                            <textarea id="summernote" name="body">${!empty param['sender'] ? "



                                                        ----
                                                  " += sessionScope['body'] : ''}</textarea> 
                        </td>
                    </tr>
                    <tr>
                        <td>첨부 파일</td>
                        <td> <input type="file" name="file1"  size="80">  </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <input type="submit" value="메일 보내기">
                            <input type="reset" value="다시 입력">
                        </td>
                    </tr>
                </table>
            </form>
        </div>

        <script>
            // 주소록 팝업 스크립트
            function openAddressBook() {
                window.open('address_book_popup', '주소록', 'width=500, height=400, left=100, top=100');
            }

            // 💡 [추가] Summernote 에디터 초기화 및 UI 설정 스크립트
            $(document).ready(function() {
                $('#summernote').summernote({
                    placeholder: '메일 본문을 자유롭게 작성해주세요.',
                    tabsize: 2,
                    height: 350, // 에디터 높이
                    toolbar: [
                      // 원하는 도구만 화면에 노출되도록 설정
                      ['style', ['style']],
                      ['font', ['bold', 'underline', 'clear']],
                      ['color', ['color']],
                      ['para', ['ul', 'ol', 'paragraph']],
                      ['table', ['table']],
                      ['insert', ['link', 'picture']],
                      ['view', ['fullscreen', 'codeview', 'help']]
                    ]
                });
            });
        </script>

        <%@include file="../footer.jspf"%>
    </body>
</html>