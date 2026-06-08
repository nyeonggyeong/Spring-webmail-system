/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.model;

/**
 *
 * @author suk22
 */
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 보낸 편지함 기능 자동화 테스트
 */
@SpringBootTest
@Transactional
class SentMailAgentTest {

    @Autowired
    private SentMailAgent sentMailAgent;

    @Test
    @DisplayName("1. 메일 발송 성공 시 보낸 편지함에 정상적으로 백업(저장)되어야 한다")
    void insertSentMailTest() {
        SentMailDto mail = new SentMailDto();
        mail.setUserid("testUser");
        mail.setReceiver("professor@univ.ac.kr");
        mail.setSubject("과제 제출합니다");
        mail.setBody("보낸 편지함 테스트 본문입니다.");

        boolean result = sentMailAgent.insertSentMail(mail);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("2. 보낸 편지함 목록과 상세 조회 기능이 정상 작동해야 한다")
    void getSentMailTest() {
        SentMailDto mail = new SentMailDto();
        mail.setUserid("testUser");
        mail.setReceiver("friend@test.com");
        mail.setSubject("상세조회 테스트 메일");
        mail.setBody("잘 지내지?");
        sentMailAgent.insertSentMail(mail);

        List<SentMailDto> list = sentMailAgent.getSentMailList("testUser");
        int targetId = list.get(0).getId();
        SentMailDto detail = sentMailAgent.getSentMail(targetId, "testUser");

        assertThat(list).isNotEmpty();
        assertThat(detail).isNotNull();
        assertThat(detail.getSubject()).isEqualTo("상세조회 테스트 메일");
    }

    @Test
    @DisplayName("3. 보낸 편지함의 특정 내역을 정상적으로 삭제해야 한다")
    void deleteSentMailTest() {
        SentMailDto mail = new SentMailDto();
        mail.setUserid("testUser");
        mail.setReceiver("spammer@test.com");
        mail.setSubject("지워질 메일입니다");
        mail.setBody("내용없음");
        sentMailAgent.insertSentMail(mail);

        List<SentMailDto> list = sentMailAgent.getSentMailList("testUser");
        int targetId = list.get(0).getId();

        boolean deleteResult = sentMailAgent.deleteSentMail(targetId, "testUser");

        assertThat(deleteResult).isTrue();
    }
}
