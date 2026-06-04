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

    // 💡 생성자 주입 단계를 보완하여 드라이버 누락 에러를 완벽하게 방지합니다.
    public AddressBookAgent(
            @Value("${spring.datasource.url}") String dbUrl,
            @Value("${spring.datasource.username}") String dbUser,
            @Value("${spring.datasource.password}") String dbPass) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
        
        // 💡 [핵심 추가] DriverManager가 MariaDB 드라이버를 찾을 수 있도록 명시적으로 로딩을 수행합니다!
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            log.info("MariaDB JDBC 드라이버 클래스 로딩 성공!");
        } catch (ClassNotFoundException e) {
            log.error("MariaDB 드라이버를 클래스 패스에서 찾을 수 없습니다: {}", e.getMessage());
        }
        
        log.info("AddressBookAgent 스프링 빈 생성 완료. URL: {}", dbUrl);
    }

    // 주소록 등록 기능 (기존과 동일)
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
            log.error("주소록 추가 중 데이터베이스 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }

    // 주소록 목록 가져오기 기능 (기존과 동일)
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