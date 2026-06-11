/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.model;

import jakarta.mail.Message;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author suk22
 */
class Pop3AgentSortTest {

    @Test
    @DisplayName("메일 날짜(SentDate) 기준 오름차순 정렬 테스트")
    void sortBySentDateTest() throws Exception {
        long now = System.currentTimeMillis();
        long oneDay = 1000 * 60 * 60 * 24L; // 하루(1일)를 밀리초로 계산

        Message msg1 = mock(Message.class); 
        when(msg1.getSentDate()).thenReturn(new Date(now - (oneDay * 5)));

        Message msg2 = mock(Message.class); 
        when(msg2.getSentDate()).thenReturn(new Date(now - oneDay));

        Message msg3 = mock(Message.class); 
        when(msg3.getSentDate()).thenReturn(new Date(now - (oneDay * 3)));

        Message msg4 = mock(Message.class); 
        when(msg4.getSentDate()).thenReturn(new Date(now));

        Message msg5 = mock(Message.class); 
        when(msg5.getSentDate()).thenReturn(new Date(now - (oneDay * 2)));

        Message[] messages = {msg3, msg1, msg4, msg2, msg5};

        Arrays.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                try {
                    Date d1 = m1.getSentDate();
                    Date d2 = m2.getSentDate();
                    if (d1 == null && d2 == null) return 0;
                    if (d1 == null) return -1;
                    if (d2 == null) return 1;
                    return d1.compareTo(d2); // 오름차순 정렬 (과거 -> 최신)
                } catch (Exception e) {
                    return 0;
                }
            }
        });
        
        assertEquals(msg1, messages[0], "배열의 0번째 인덱스는 가장 오래된 메일(5일 전)이어야 합니다.");
        assertEquals(msg3, messages[1], "배열의 1번째 인덱스는 3일 전 메일이어야 합니다.");
        assertEquals(msg5, messages[2], "배열의 2번째 인덱스는 2일 전 메일이어야 합니다.");
        assertEquals(msg2, messages[3], "배열의 3번째 인덱스는 1일 전 메일이어야 합니다.");
        assertEquals(msg4, messages[4], "배열의 마지막(4번째) 인덱스는 가장 최신 메일(오늘)이어야 합니다.");
    }
}
