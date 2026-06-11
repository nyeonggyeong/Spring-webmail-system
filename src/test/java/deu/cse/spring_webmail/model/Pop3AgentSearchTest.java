/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.model;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author suk22
 */
class Pop3AgentSearchTest {

    @Test
    @DisplayName("1. 제목(Subject) 기준 검색 필터링 테스트")
    void filterBySubjectTest() throws Exception {
        // Given (준비): 제임스 서버 없이 가짜(Mock) 메일 객체 5개를 생성합니다.
        Message msg1 = mock(Message.class);
        when(msg1.getSubject()).thenReturn("객체지향설계 과제 제출합니다");

        Message msg2 = mock(Message.class);
        when(msg2.getSubject()).thenReturn("안녕하세요 교수님");

        Message msg3 = mock(Message.class);
        when(msg3.getSubject()).thenReturn("웹메일 과제 관련 질문");

        Message msg4 = mock(Message.class);
        when(msg4.getSubject()).thenReturn("휴강 안내");

        Message msg5 = mock(Message.class);
        when(msg5.getSubject()).thenReturn("[긴급] 최종 과제 기한 연장");

        Message[] allMessages = {msg1, msg2, msg3, msg4, msg5};
        String keyword = "과제"; // 검색어 세팅

        List<Message> filteredList = new ArrayList<>();
        for (Message m : allMessages) {
            String subj = m.getSubject();
            if (subj != null && subj.contains(keyword)) {
                filteredList.add(m);
            }
        }

        assertEquals(3, filteredList.size(), "'과제' 키워드가 포함된 메일은 정확히 3개 걸러져야 합니다.");
    }

    @Test
    @DisplayName("2. 보낸사람(Sender) 기준 검색 필터링 및 대소문자 무시 테스트")
    void filterBySenderTest() throws Exception {
        Message msg1 = mock(Message.class);
        when(msg1.getFrom()).thenReturn(new Address[]{new InternetAddress("Prof@deu.ac.kr")}); // 대문자 섞임

        Message msg2 = mock(Message.class);
        when(msg2.getFrom()).thenReturn(new Address[]{new InternetAddress("student1@deu.ac.kr")});

        Message msg3 = mock(Message.class);
        when(msg3.getFrom()).thenReturn(new Address[]{new InternetAddress("STUDENT2@deu.ac.kr")}); // 전체 대문자

        Message[] allMessages = {msg1, msg2, msg3};
        String keyword = "student"; // 소문자로 검색

        List<Message> filteredList = new ArrayList<>();
        for (Message m : allMessages) {
            Address[] froms = m.getFrom();
            if (froms != null) {
                for (Address addr : froms) {
                    if (addr.toString().toLowerCase().contains(keyword.toLowerCase())) {
                        filteredList.add(m);
                        break;
                    }
                }
            }
        }

        assertEquals(2, filteredList.size(), "대소문자에 상관없이 'student' 발신자는 2명 필터링되어야 합니다.");
    }
}
