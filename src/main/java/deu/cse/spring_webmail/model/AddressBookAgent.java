package deu.cse.spring_webmail.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AddressBookAgent {

    private final String dbUrl;
    private final String dbUser;
    private final String dbPass;

    // 💡 불필요한 드라이버 강제 로딩 코드를 제거하고 깔끔하게 생성자 주입만 남김
    public AddressBookAgent(
            @Value("${spring.datasource.url}") String dbUrl,
            @Value("${spring.datasource.username}") String dbUser,
            @Value("${spring.datasource.password}") String dbPass) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
        log.info("AddressBookAgent 스프링 빈 생성 완료. URL: {}", dbUrl);
    }

    // 주소록 등록 기능
    public boolean addAddress(AddressBookDto address) {
        String sql = "INSERT INTO address_book (userid, name, email, phone) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, address.getUserid());
            pstmt.setString(2, address.getName());
            pstmt.setString(3, address.getEmail());
            pstmt.setString(4, address.getPhone());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // 💡 네트워크 단절, 문법 오류 등 진짜 DB 에러를 잡기 위해 SQLException 처리는 유지
            log.error("주소록 추가 중 데이터베이스 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }

    // 주소록 목록 가져오기 기능
    public List<AddressBookDto> getAddressList(String userid) {
        List<AddressBookDto> list = new ArrayList<>();
        String sql = "SELECT * FROM address_book WHERE userid = ? ORDER BY name ASC";
        
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userid);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AddressBookDto dto = new AddressBookDto();
                    dto.setId(rs.getInt("id"));
                    dto.setUserid(rs.getString("userid"));
                    dto.setName(rs.getString("name"));
                    dto.setEmail(rs.getString("email"));
                    dto.setPhone(rs.getString("phone"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            log.error("주소록 조회 중 데이터베이스 오류 발생: {}", e.getMessage(), e);
        }
        return list;
    }
}