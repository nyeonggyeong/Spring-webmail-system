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
 * 휴지통 기능 자동화 테스트
 */
@SpringBootTest
@Transactional 
class EmailTrashAgentTest {

    @Autowired
    private EmailTrashAgent emailTrashAgent;

    @Test
    @DisplayName("휴지통에 삭제된 메일이 정상적으로 백업(저장)되어야 한다")
    void insertTrashTest() {
        // Given (준비: 가상의 삭제된 메일 데이터 생성)
        EmailTrashDto trash = new EmailTrashDto();
        trash.setUserid("testUser");
        trash.setSender("hacker@test.com");
        trash.setSubject("테스트 메일 제목입니다");
        trash.setBody("테스트 메일 본문입니다");

        boolean result = emailTrashAgent.insertTrash(trash);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("휴지통 목록을 정상적으로 불러와야 한다")
    void getTrashListTest() {
        // Given (준비: 먼저 메일을 하나 휴지통에 넣음)
        EmailTrashDto trash = new EmailTrashDto();
        trash.setUserid("testUser");
        trash.setSender("tester@test.com");
        trash.setSubject("목록 조회 테스트");
        trash.setBody("내용");
        emailTrashAgent.insertTrash(trash);

        List<EmailTrashDto> list = emailTrashAgent.getTrashList("testUser");

        assertThat(list).isNotEmpty();
        assertThat(list.get(0).getSubject()).isEqualTo("목록 조회 테스트");
    }

    @Test
    @DisplayName("휴지통에서 메일이 영구 삭제되어야 한다")
    void deleteTrashTest() {
        // Given (준비: 메일을 넣고 해당 메일의 목록을 가져옴)
        EmailTrashDto trash = new EmailTrashDto();
        trash.setUserid("testUser");
        trash.setSender("delete@test.com");
        trash.setSubject("삭제될 메일");
        emailTrashAgent.insertTrash(trash);

        List<EmailTrashDto> list = emailTrashAgent.getTrashList("testUser");
        int targetId = list.get(0).getId(); 

        boolean deleteResult = emailTrashAgent.deleteTrash(targetId, "testUser");

        assertThat(deleteResult).isTrue();
    }
}
