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
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Pop3Agent - POP3 메일 서버 연동 및 리팩토링 버전
 * [예방 유지보수]: 데드코드 제거, 디버그 설정 중복 제거, finally 내 return 안티패턴 수정
 */
@Slf4j
public class Pop3Agent {

    @Getter @Setter private String host;
    @Getter @Setter private String userid;
    @Getter @Setter private String password;
    @Getter @Setter private Store store;
    @Getter @Setter private HttpServletRequest request;

    // 220612 LJM - added to implement REPLY
    @Getter private String sender;
    @Getter private String subject;
    @Getter private String body;

    // 객체 생성 시 필수 정보를 강제하기 위해 기본 생성자는 배제하고 커스텀 생성자만 유지
    public Pop3Agent(String host, String userid, String password) {
        this.host = host;
        this.userid = userid;
        this.password = password;
    }

    public boolean validate() {
        boolean status = false;
        try {
            status = connectToStore();
            if (store != null) {
                store.close();
            }
        } catch (Exception ex) {
            log.error("Pop3Agent.validate() error : ", ex);
            status = false;
        }
        return status; // finally 내부 return 제거
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
        }
        return status; // finally 내부 return 제거
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
        Message[] allMessages = null;

        if (!connectToStore()) {
            log.error("POP3 connection failed!");
            return "POP3 연결이 되지 않아 메일 목록을 볼 수 없습니다.";
        }

        try {
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            int totalMessages = folder.getMessageCount();

            if (totalMessages > 0) {
                allMessages = folder.getMessages();

                FetchProfile fp = new FetchProfile();
                fp.add(FetchProfile.Item.ENVELOPE);
                folder.fetch(allMessages, fp);

                // 날짜 기준 최신순 정렬 (오름차순)
                java.util.Arrays.sort(allMessages, new java.util.Comparator<Message>() {
                    @Override
                    public int compare(Message m1, Message m2) {
                        try {
                            java.util.Date d1 = m1.getSentDate();
                            java.util.Date d2 = m2.getSentDate();
                            if (d1 == null && d2 == null) return 0;
                            if (d1 == null) return -1; 
                            if (d2 == null) return 1;
                            return d1.compareTo(d2);
                        } catch (Exception e) {
                            return 0;
                        }
                    }
                });

                int endIndex = totalMessages - ((page - 1) * pageSize);
                int startIndex = Math.max(0, endIndex - pageSize);

                Message[] pagedMessages = java.util.Arrays.copyOfRange(allMessages, startIndex, endIndex);

                MessageFormatter formatter = new MessageFormatter(userid);
                result = formatter.getMessageTable(pagedMessages);
            } else {
                result = "<div style='padding:20px; text-align:center;'>수신된 메시지가 없습니다.</div>";
            }

            folder.close(true);
            store.close();
        } catch (Exception ex) {
            log.error("Pop3Agent.getMessageList(page) 예외 = {}", ex.getMessage());
            result = "Pop3Agent.getMessageList(page) 예외 = " + ex.getMessage();
        }
        return result; // finally 내부 return 제거
    }
    
    public String getMessageList() {
        return getMessageList(1, 10);
    }

    public String getMessage(int n) {
        String result = "POP3 서버 연결이 되지 않아 메시지를 볼 수 없습니다.";

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
        }
        return result; // finally 내부 return 제거
    }

    private boolean connectToStore() {
        boolean status = false;
        Properties props = System.getProperties();
        props.setProperty("mail.pop3.host", host);
        props.setProperty("mail.pop3.user", userid);
        props.setProperty("mail.pop3.apop.enable", "false");
        props.setProperty("mail.pop3.disablecapa", "true");

        Session session = Session.getInstance(props);
        session.setDebug(false); // 불필요한 설정 축소 및 중복 제거

        try {
            store = session.getStore("pop3");
            store.connect(host, userid, password);
            status = true;
        } catch (Exception ex) {
            log.error("connectToStore 예외: {}", ex.getMessage());
        }
        return status; // finally 내부 return 제거
    }
}