/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.control;

/**
 *
 * @author suk22
 */
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import jakarta.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WriteControllerTest {

    @Mock
    private HttpSession session;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private WriteController writeController;

    @Test
    @DisplayName("임시저장 성공: 세션이 존재하고 DB 저장이 정상적으로 수행될 때 'success' 반환")
    void autoSave_Success() {
        // given (준비)
        String userid = "testuser";
        String to = "receiver@test.com";
        String cc = "cc@test.com";
        String subj = "테스트 메일 제목";
        String body = "테스트 메일 본문입니다.";

        when(session.getAttribute("userid")).thenReturn(userid);
        
        when(jdbcTemplate.update(anyString(), 
                eq(userid), eq(to), eq(cc), eq(subj), eq(body), 
                eq(to), eq(cc), eq(subj), eq(body)))
                .thenReturn(1);

        // when (실행)
        String result = writeController.autoSave(to, cc, subj, body);

        // then (검증)
        assertEquals("success", result, "임시저장이 정상적으로 완료되면 'success'를 반환해야 합니다.");
        
        // 실제로 JdbcTemplate.update가 ON DUPLICATE KEY 파라미터 개수(9개)에 맞춰 정확히 1번 호출되었는지 검증
        verify(jdbcTemplate, times(1)).update(anyString(), 
                eq(userid), eq(to), eq(cc), eq(subj), eq(body), 
                eq(to), eq(cc), eq(subj), eq(body));
    }

    @Test
    @DisplayName("🚨 임시저장 실패: 세션에 로그인 정보(userid)가 없을 때 조기 종료 후 'fail' 반환")
    void autoSave_Fail_NoSession() {
        when(session.getAttribute("userid")).thenReturn(null);

        String result = writeController.autoSave("receiver", "cc", "subj", "body");

        assertEquals("fail", result, "로그인되지 않은 상태에서는 'fail'을 반환해야 합니다.");
        
       verifyNoInteractions(jdbcTemplate);
    }

    @Test
    @DisplayName("임시저장 실패: DB 쿼리 실행 중 예외(SQL Error 등) 발생 시 우아하게 'fail' 반환")
    void autoSave_Fail_DbException() {
        // given (준비)
        String userid = "testuser";
        when(session.getAttribute("userid")).thenReturn(userid);
        
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("강제 DB 연결 오류 테스트"));

        String result = writeController.autoSave("receiver", "cc", "subj", "body");

        assertEquals("fail", result, "DB 오류 발생 시 서버가 죽지 않고 'fail'을 반환해야 합니다.");
    }
}
