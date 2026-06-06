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
public class SentMailAgent {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SentMailAgent(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean insertSentMail(SentMailDto mail) {
        String sql = "INSERT INTO sent_mail (userid, receiver, subject, body) VALUES (?, ?, ?, ?)";
        try {
            return jdbcTemplate.update(sql, mail.getUserid(), mail.getReceiver(), 
                                       mail.getSubject(), mail.getBody()) > 0;
        } catch (Exception e) {
            log.error("보낸 편지함 저장 오류: {}", e.getMessage());
            return false;
        }
    }

    public List<SentMailDto> getSentMailList(String userid) {
        String sql = "SELECT * FROM sent_mail WHERE userid = ? ORDER BY sent_date DESC";
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                SentMailDto dto = new SentMailDto();
                dto.setId(rs.getInt("id"));
                dto.setUserid(rs.getString("userid"));
                dto.setReceiver(rs.getString("receiver"));
                dto.setSubject(rs.getString("subject"));
                dto.setBody(rs.getString("body"));
                dto.setSentDate(rs.getString("sent_date"));
                return dto;
            }, userid);
        } catch (Exception e) {
            return List.of();
        }
    }
}
