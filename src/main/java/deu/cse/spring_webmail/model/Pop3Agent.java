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
 * [기능 추가]: In-Memory Global Sort (최신순 정렬) 및 메일 검색 필터링 로직 통합
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
        return status;
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
        return status; 
    }

    // 💡 [추가] 검색어 필터링 지원 헬퍼 메서드
    private boolean isMatch(Message m, String searchType, String keyword) {
        try {
            if ("sender".equals(searchType)) {
                jakarta.mail.Address[] froms = m.getFrom();
                if (froms != null) {
                    for (jakarta.mail.Address addr : froms) {
                        if (addr.toString().toLowerCase().contains(keyword.toLowerCase())) return true;
                    }
                }
            } else { // 기본값은 제목(subject) 검색
                String subj = m.getSubject();
                if (subj != null && subj.toLowerCase().contains(keyword.toLowerCase())) return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    // 💡 [수정] 검색어가 있을 경우 필터링된 메일의 총 개수를, 없으면 전체 개수를 반환
    public int getTotalMessageCount(String searchType, String keyword) {
        int count = 0;
        if (!connectToStore()) {
            return count;
        }
        try {
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            
            if (keyword == null || keyword.trim().isEmpty()) {
                count = folder.getMessageCount();
            } else {
                // 검색어가 있으면 일일이 확인하여 카운트 증가
                Message[] messages = folder.getMessages();
                FetchProfile fp = new FetchProfile();
                fp.add(FetchProfile.Item.ENVELOPE);
                folder.fetch(messages, fp);
                for (Message m : messages) {
                    if (isMatch(m, searchType, keyword)) count++;
                }
            }
            folder.close(false);
            store.close();
        } catch (Exception ex) {
            log.error("검색 메일 개수 조회 예외: {}", ex.getMessage());
        }
        return count; 
    }

    // 💡 [수정] 전체 메일 중 검색어에 맞는 것만 필터링 후 정렬 및 페이징 처리
    public String getMessageList(int page, int pageSize, String searchType, String keyword) {
        String result = "";
        
        if (!connectToStore()) {
            log.error("POP3 connection failed!");
            return "POP3 연결이 되지 않아 메일 목록을 볼 수 없습니다.";
        }

        try {
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            Message[] allMessages = folder.getMessages();
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            folder.fetch(allMessages, fp);

            // 1. 검색어 유무에 따른 필터링 (In-Memory Filter)
            java.util.List<Message> filteredList = new java.util.ArrayList<>();
            boolean isSearch = (keyword != null && !keyword.trim().isEmpty());

            for (Message m : allMessages) {
                if (!isSearch || isMatch(m, searchType, keyword)) {
                    filteredList.add(m);
                }
            }

            int totalMessages = filteredList.size();

            if (totalMessages > 0) {
                Message[] filteredArray = filteredList.toArray(new Message[0]);

                // 2. 필터링된 메일들을 최신 날짜순으로 정렬 (오름차순)
                java.util.Arrays.sort(filteredArray, new java.util.Comparator<Message>() {
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

                // 3. 현재 페이지 구간 자르기
                int endIndex = totalMessages - ((page - 1) * pageSize);
                int startIndex = Math.max(0, endIndex - pageSize);

                Message[] pagedMessages = java.util.Arrays.copyOfRange(filteredArray, startIndex, endIndex);

                // 4. HTML 표 생성
                MessageFormatter formatter = new MessageFormatter(userid);
                result = formatter.getMessageTable(pagedMessages);
            } else {
                result = "<div style='padding:40px; text-align:center; color:#666; font-size:1.1em;'>"
                       + "<strong>'" + (keyword != null ? keyword : "") + "'</strong>에 대한 검색 결과가 없습니다.</div>";
            }

            folder.close(true);
            store.close();
        } catch (Exception ex) {
            log.error("검색 및 페이징 조회 예외 = {}", ex.getMessage());
            result = "Pop3Agent.getMessageList 예외 = " + ex.getMessage();
        }
        return result; 
    }
    
    // 💡 파라미터가 비어있을 경우를 대비한 기본 오버로딩 메서드 (하위 호환성)
    public String getMessageList() {
        return getMessageList(1, 10, null, null);
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
        return result; 
    }

    private boolean connectToStore() {
        boolean status = false;
        Properties props = System.getProperties();
        props.setProperty("mail.pop3.host", host);
        props.setProperty("mail.pop3.user", userid);
        props.setProperty("mail.pop3.apop.enable", "false");
        props.setProperty("mail.pop3.disablecapa", "true");

        Session session = Session.getInstance(props);
        session.setDebug(false); 

        try {
            store = session.getStore("pop3");
            store.connect(host, userid, password);
            status = true;
        } catch (Exception ex) {
            log.error("connectToStore 예외: {}", ex.getMessage());
        }
        return status; 
    }
}