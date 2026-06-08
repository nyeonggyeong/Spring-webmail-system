/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.model;

import jakarta.mail.FetchProfile;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;
import java.util.Properties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author skylo
 */
@Slf4j
@NoArgsConstructor        // 기본 생성자 생성
public class Pop3Agent {
    @Getter @Setter private String host;
    @Getter @Setter private String userid;
    @Getter @Setter private String password;
    @Getter @Setter private Store store;
    @Getter @Setter private String excveptionType;
    @Getter @Setter private HttpServletRequest request;
    
    // 220612 LJM - added to implement REPLY
    @Getter private String sender;
    @Getter private String subject;
    @Getter private String body;
    
    public Pop3Agent(String host, String userid, String password) {
        this.host = host;
        this.userid = userid;
        this.password = password;
    }
    
    public boolean validate() {
        boolean status = false;

        try {
            status = connectToStore();
            store.close();
        } catch (Exception ex) {
            log.error("Pop3Agent.validate() error : " + ex);
            status = false;  // for clarity
        } finally {
            return status;
        }
    }

    public boolean deleteMessage(int msgid, boolean really_delete) {
        boolean status = false;

        if (!connectToStore()) {
            return status;
        }

        try {
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);

            Message msg = folder.getMessage(msgid);
            msg.setFlag(Flags.Flag.DELETED, really_delete);

            folder.close(true);  // expunge == true
            store.close();
            status = true;
        } catch (Exception ex) {
            log.error("deleteMessage() error: {}", ex.getMessage());
        } finally {
            return status;
        }
    }

    public int getTotalMessageCount() {
        int count = 0;
        if (!connectToStore()) {
            return count;
        }
        try {
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            count = folder.getMessageCount();
            folder.close(false);
            store.close();
        } catch (Exception ex) {
            log.error("Pop3Agent.getTotalMessageCount() 예외: {}", ex.getMessage());
        }
        return count;
    }

    public String getMessageList(int page, int pageSize) {
        String result = "";
        Message[] messages = null;

        if (!connectToStore()) {
            log.error("POP3 connection failed!");
            return "POP3 연결이 되지 않아 메일 목록을 볼 수 없습니다.";
        }

        try {
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            int totalMessages = folder.getMessageCount();

            if (totalMessages > 0) {
                int endIndex = totalMessages - ((page - 1) * pageSize);
                int startIndex = Math.max(1, endIndex - pageSize + 1);

                // 서버에서 startIndex ~ endIndex 구간의 메일만 정확히 뽑아옵니다.
                messages = folder.getMessages(startIndex, endIndex);
                
                FetchProfile fp = new FetchProfile();
                fp.add(FetchProfile.Item.ENVELOPE);
                folder.fetch(messages, fp);

                MessageFormatter formatter = new MessageFormatter(userid);
                result = formatter.getMessageTable(messages);
            } else {
                result = "<div style='padding:20px; text-align:center;'>수신된 메시지가 없습니다.</div>";
            }

            folder.close(true);
            store.close();
        } catch (Exception ex) {
            log.error("Pop3Agent.getMessageList(page) 예외 = {}", ex.getMessage());
            result = "Pop3Agent.getMessageList(page) 예외 = " + ex.getMessage();
        } finally {
            return result;
        }
    }

    public String getMessageList() {
        return getMessageList(1, 1000); 
    }

    public String getMessage(int n) {
        String result = "POP3  서버 연결이 되지 않아 메시지를 볼 수 없습니다.";

        if (!connectToStore()) {
            log.error("POP3 connection failed!");
            return result;
        }

        try {
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            Message message = folder.getMessage(n);

            MessageFormatter formatter = new MessageFormatter(userid);
            formatter.setRequest(request);  
            result = formatter.getMessage(message);
            sender = formatter.getSender();  
            subject = formatter.getSubject();
            body = formatter.getBody();

            folder.close(true);
            store.close();
        } catch (Exception ex) {
            log.error("Pop3Agent.getMessageList() : exception = {}", ex);
            result = "Pop3Agent.getMessage() : exception = " + ex;
        } finally {
            return result;
        }
    }

    private boolean connectToStore() {
        boolean status = false;
        Properties props = System.getProperties();
        props.setProperty("mail.pop3.host", host);
        props.setProperty("mail.pop3.user", userid);
        props.setProperty("mail.pop3.apop.enable", "false");
        props.setProperty("mail.pop3.disablecapa", "true");  
        props.setProperty("mail.debug", "false");
        props.setProperty("mail.pop3.debug", "false");

        Session session = Session.getInstance(props);
        session.setDebug(false);

        try {
            store = session.getStore("pop3");
            store.connect(host, userid, password);
            status = true;
        } catch (Exception ex) {
            log.error("connectToStore 예외: {}", ex.getMessage());
        } finally {
            return status;
        }
    }
}