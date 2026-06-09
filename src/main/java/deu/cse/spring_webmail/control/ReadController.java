/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.control;

import deu.cse.spring_webmail.model.EmailTrashAgent;
import deu.cse.spring_webmail.model.EmailTrashDto;
import deu.cse.spring_webmail.model.Pop3Agent;
import deu.cse.spring_webmail.model.SentMailDto;
import deu.cse.spring_webmail.model.SmtpAgent;

import jakarta.mail.internet.MimeUtility;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ReadController - 휴지통 및 보낸 편지함 관리 기능 통합본
 * [수정 내용]: 기본 생성자가 제거된 Pop3Agent를 매개변수 생성자 구조에 맞게 리팩토링 완료
 * @author Prof.Jong Min Lee
 */
@Controller
@PropertySource("classpath:/system.properties")
@Slf4j
public class ReadController {

    @Autowired
    private ServletContext ctx;
    @Autowired
    private HttpSession session;
    @Autowired
    private HttpServletRequest request;
    @Value("${file.download_folder}")
    private String DOWNLOAD_FOLDER;

    @Autowired
    private EmailTrashAgent emailTrashAgent;

    @Autowired
    private deu.cse.spring_webmail.model.SentMailAgent sentMailAgent;

    @GetMapping("/show_message")
    public String showMessage(@RequestParam Integer msgid, Model model) {
        log.debug("download_folder = {}", DOWNLOAD_FOLDER);

        String host = (String) session.getAttribute("host");
        String userid = (String) session.getAttribute("userid");
        String password = (String) session.getAttribute("password");

        // ✨ [수정 완료] 파라미터가 있는 생성자로 안전하게 변경
        Pop3Agent pop3 = new Pop3Agent(host, userid, password);
        pop3.setRequest(request);

        String msg = pop3.getMessage(msgid);
        session.setAttribute("sender", pop3.getSender());  
        session.setAttribute("subject", pop3.getSubject());
        session.setAttribute("body", pop3.getBody());
        model.addAttribute("msg", msg);
        return "/read_mail/show_message";
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam("userid") String userId,
            @RequestParam("filename") String fileName) {
        log.debug("userid = {}, filename = {}", userId, fileName);
        try {
            log.debug("userid = {}, filename = {}", userId, MimeUtility.decodeText(fileName));
        } catch (UnsupportedEncodingException ex) {
            log.error("error");
        }

        String basePath = ctx.getRealPath(DOWNLOAD_FOLDER) + File.separator + userId;

        Path path = Paths.get(basePath + File.separator + fileName);
        String contentType = null;
        try {
            contentType = Files.probeContentType(path);
            log.debug("File: {}, Content-Type: {}", path.toString(), contentType);
        } catch (IOException e) {
            log.error("downloadDo: 오류 발생 - {}", e.getMessage());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.builder("attachment").filename(fileName, StandardCharsets.UTF_8).build());
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);

        Resource resource = null;
        try {
            resource = new InputStreamResource(Files.newInputStream(path));
        } catch (IOException e) {
            log.error("downloadDo: 오류 발생 - {}", e.getMessage());
        }
        if (resource == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @GetMapping("/delete_mail.do")
    public String deleteMailDo(@RequestParam("msgid") Integer msgId, RedirectAttributes attrs) {
        log.debug("delete_mail.do: msgid = {}", msgId);

        String host = (String) session.getAttribute("host");
        String userid = (String) session.getAttribute("userid");
        String password = (String) session.getAttribute("password");

        Pop3Agent pop3 = new Pop3Agent(host, userid, password);
        pop3.setRequest(request);

        // 메일 본문과 정보를 먼저 필드에 채워넣음
        pop3.getMessage(msgId);

        EmailTrashDto trashInfo = new EmailTrashDto();
        trashInfo.setUserid(userid);
        trashInfo.setSender(pop3.getSender());
        trashInfo.setSubject(pop3.getSubject());
        trashInfo.setBody(pop3.getBody());

        // 휴지통 DB 테이블에 인서트
        emailTrashAgent.insertTrash(trashInfo);

        // 실제 메일 서버(Inbox)에서 영구 삭제 처리
        boolean deleteSuccessful = pop3.deleteMessage(msgId, true);
        if (deleteSuccessful) {
            attrs.addFlashAttribute("msg", "메시지가 휴지통으로 이동되었습니다.");
        } else {
            attrs.addFlashAttribute("msg", "메시지 삭제를 실패하였습니다.");
        }

        return "redirect:/main_menu";
    }

    @GetMapping("/email_trash")
    public String emailTrash(Model model) {
        String userid = (String) session.getAttribute("userid");
        if (userid == null) {
            return "redirect:/";
        }

        model.addAttribute("trashList", emailTrashAgent.getTrashList(userid));
        return "read_mail/email_trash";
    }

    @GetMapping("/restore_trash.do")
    public String restoreTrashDo(@RequestParam("id") Integer id, RedirectAttributes attrs) {
        String host = (String) session.getAttribute("host");
        String userid = (String) session.getAttribute("userid");

        EmailTrashDto trash = emailTrashAgent.getTrash(id, userid);
        if (trash != null) {
            SmtpAgent agent = new SmtpAgent(host, userid);
            agent.setTo(userid + "@" + host);
            agent.setCc("");
            agent.setSubj("[휴지통 복구] " + trash.getSubject());

            String restoreBody = String.format("원래 보낸 사람: %s\n\n[원래 본문]\n%s",
                    trash.getSender(), trash.getBody());
            agent.setBody(restoreBody);

            emailTrashAgent.deleteTrash(id, userid);

            if (agent.sendMessage()) {
                attrs.addFlashAttribute("msg", "메일이 성공적으로 복구(재수신) 되었습니다.");
            } else {
                attrs.addFlashAttribute("msg", "복구 메일 발송에 실패했습니다. (DB에서는 삭제됨)");
            }
        }
        return "redirect:/email_trash";
    }

    @GetMapping("/empty_trash.do")
    public String emptyTrashDo(RedirectAttributes attrs) {
        String userid = (String) session.getAttribute("userid");
        if (userid != null) {
            emailTrashAgent.emptyTrash(userid);
            attrs.addFlashAttribute("msg", "휴지통이 모두 비워졌습니다.");
        }
        return "redirect:/email_trash";
    }

    @GetMapping("/delete_trash.do")
    public String deleteTrashDo(@RequestParam("id") Integer id, RedirectAttributes attrs) {
        String userid = (String) session.getAttribute("userid");
        if (emailTrashAgent.deleteTrash(id, userid)) {
            attrs.addFlashAttribute("msg", "메일이 영구 삭제되었습니다.");
        } else {
            attrs.addFlashAttribute("msg", "영구 삭제에 실패했습니다.");
        }
        return "redirect:/email_trash";
    }

    @GetMapping("/sent_mail")
    public String sentMail(Model model) {
        String userid = (String) session.getAttribute("userid");
        if (userid == null) {
            return "redirect:/";
        }

        model.addAttribute("sentList", sentMailAgent.getSentMailList(userid));
        return "read_mail/sent_mail";
    }

    @GetMapping("/show_sent_message")
    public String showSentMessage(@RequestParam("id") Integer id, Model model) {
        String userid = (String) session.getAttribute("userid");
        if (userid == null) {
            return "redirect:/";
        }

        SentMailDto sentMail = sentMailAgent.getSentMail(id, userid);
        model.addAttribute("mail", sentMail);

        return "read_mail/show_sent_message";
    }

    @GetMapping("/delete_sent_mail.do")
    public String deleteSentMailDo(@RequestParam("id") Integer id, RedirectAttributes attrs) {
        String userid = (String) session.getAttribute("userid");
        if (userid != null && sentMailAgent.deleteSentMail(id, userid)) {
            attrs.addFlashAttribute("msg", "보낸 편지함에서 내역이 삭제되었습니다.");
        }
        return "redirect:/sent_mail";
    }
}