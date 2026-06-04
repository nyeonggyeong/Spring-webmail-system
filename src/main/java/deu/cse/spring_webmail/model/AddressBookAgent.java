package deu.cse.spring_webmail.model;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component // 스프링 빈 등록
@Slf4j
public class AddressBookAgent {

    private final JdbcTemplate jdbcTemplate;

    // 💡 1. DB 주소, 비밀번호를 직접 가져오지 않습니다! 
    // application.properties를 읽은 스프링 부트가 '알아서' 연결된 JdbcTemplate을 건네줍니다.
    @Autowired
    public AddressBookAgent(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        log.info("AddressBookAgent 생성 완료 (JdbcTemplate 주입됨)");
    }

    // 💡 2. 주소록 등록 기능 (코드가 획기적으로 짧아짐)
    public boolean addAddress(AddressBookDto address) {
        String sql = "INSERT INTO address_book (userid, name, email, phone) VALUES (?, ?, ?, ?)";
        
        try {
            // Connection 연결, PreparedStatement 세팅, 그리고 반납(close)까지 update() 한 줄이 다 해줍니다!
            int result = jdbcTemplate.update(sql, 
                    address.getUserid(), 
                    address.getName(), 
                    address.getEmail(), 
                    address.getPhone());
                    
            return result > 0;
        } catch (Exception e) {
            log.error("주소록 추가 중 데이터베이스 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }

    // 💡 3. 주소록 목록 가져오기 기능 (ResultSet 반복문 제거)
    public List<AddressBookDto> getAddressList(String userid) {
        String sql = "SELECT * FROM address_book WHERE userid = ? ORDER BY name ASC";
        
        try {
            // query() 메서드가 DB에서 데이터를 꺼내와서 람다식(RowMapper)을 통해 리스트로 쫙 만들어줍니다.
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                AddressBookDto dto = new AddressBookDto();
                dto.setId(rs.getInt("id"));
                dto.setUserid(rs.getString("userid"));
                dto.setName(rs.getString("name"));
                dto.setEmail(rs.getString("email"));
                dto.setPhone(rs.getString("phone"));
                return dto;
            }, userid);
            
        } catch (Exception e) {
            log.error("주소록 조회 중 데이터베이스 오류 발생: {}", e.getMessage(), e);
            return List.of(); // 오류 시 안전하게 빈 리스트 반환
        }
        
    }
    public boolean deleteAddress(int id, String userid) {
        String sql = "DELETE FROM address_book WHERE id = ? AND userid = ?";
        
        try {
            // id와 userid가 모두 일치하는 데이터만 안전하게 삭제
            int result = jdbcTemplate.update(sql, id, userid);
            return result > 0;
        } catch (Exception e) {
            log.error("주소록 삭제 중 데이터베이스 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }
}