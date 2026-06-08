/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail;

/**
 *
 * @author suk22
 */
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 데이터베이스(MariaDB) 연결 상태 검증 테스트
 */
@SpringBootTest
class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    @DisplayName("DB(MariaDB) 커넥션 풀이 정상적으로 연결되어야 한다")
    void testConnection() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            
            assertThat(connection).isNotNull();
            
            System.out.println("======================================");
            System.out.println("연결된 DB 종류: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("연결된 DB URL: " + connection.getMetaData().getURL());
            System.out.println("DB 사용자 이름: " + connection.getMetaData().getUserName());
            System.out.println("======================================");
        }
    }
}
