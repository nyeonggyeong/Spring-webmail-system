/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.model;

/**
 *
 * @author suk22
 */
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailTrashAgent {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EmailTrashAgent(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. 삭제된 메일을 휴지통(DB)에 저장
    public boolean insertTrash(EmailTrashDto trash) {
        String sql = "INSERT INTO email_trash (userid, sender, subject, body) VALUES (?, ?, ?, ?)";
        try {
            return jdbcTemplate.update(sql, trash.getUserid(), trash.getSender(), 
                                       trash.getSubject(), trash.getBody()) > 0;
        } catch (Exception e) {
            log.error("휴지통 저장 중 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }

    // 2. 내 휴지통 목록 가져오기
    public List<EmailTrashDto> getTrashList(String userid) {
        String sql = "SELECT * FROM email_trash WHERE userid = ? ORDER BY deleted_date DESC";
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                EmailTrashDto dto = new EmailTrashDto();
                dto.setId(rs.getInt("id"));
                dto.setUserid(rs.getString("userid"));
                dto.setSender(rs.getString("sender"));
                dto.setSubject(rs.getString("subject"));
                dto.setBody(rs.getString("body"));
                dto.setDeletedDate(rs.getString("deleted_date"));
                return dto;
            }, userid);
        } catch (Exception e) {
            log.error("휴지통 조회 중 오류 발생: {}", e.getMessage(), e);
            return List.of();
        }
    }
}