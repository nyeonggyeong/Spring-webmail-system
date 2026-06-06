/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.control;

import deu.cse.spring_webmail.model.SmtpAgent;
import deu.cse.spring_webmail.model.SentMailAgent;
import deu.cse.spring_webmail.model.SentMailDto;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 메일 쓰기를 위한 제어기
 *
 * * @author Prof.Jong Min Lee
 */
@Controller
@PropertySource("classpath:/system.properties")
@Slf4j
public class WriteController {

    @Value("${file.upload_folder}")
    private String UPLOAD_FOLDER;
    @Value("${file.max_size}")
    private String MAX_SIZE;

    @Autowired
    private ServletContext ctx;
    @Autowired
    private HttpSession session;

    @Autowired
    private SentMailAgent sentMailAgent;

    @GetMapping("/write_mail")
    public String writeMail() {
        log.debug("write_mail called...");
        session.removeAttribute("sender");  // 220612 LJM - 메일 쓰기 시는 
        return "write_mail/write_mail";
    }

    @PostMapping("/write_mail.do")
    public String writeMailDo(@RequestParam String to, @RequestParam String cc,
            @RequestParam String subj, @RequestParam String body,
            @RequestParam(name = "file1") MultipartFile upFile,
            RedirectAttributes attrs) {
        log.debug("write_mail.do: to = {}, cc = {}, subj = {}, body = {}, file1 = {}",
                to, cc, subj, body, upFile.getOriginalFilename());
        if (!"".equals(upFile.getOriginalFilename())) {
            String basePath = ctx.getRealPath(UPLOAD_FOLDER);
            log.debug("{} 파일을 {} 폴더에 저장...", upFile.getOriginalFilename(), basePath);
            File f = new File(basePath + File.separator + upFile.getOriginalFilename());
            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f))) {
                bos.write(upFile.getBytes());
            } catch (IOException e) {
                log.error("upload.do: 오류 발생 - {}", e.getMessage());
            }
        }

        boolean sendSuccessful = sendMessage(to, cc, subj, body, upFile);

        if (sendSuccessful) {
            String userid = (String) session.getAttribute("userid");
            SentMailDto sentMail = new SentMailDto();
            sentMail.setUserid(userid);
            sentMail.setReceiver(to);     // 받는 사람
            sentMail.setSubject(subj);    // 제목
            sentMail.setBody(body);       // 내용

            sentMailAgent.insertSentMail(sentMail); // DB 저장 실행

            attrs.addFlashAttribute("msg", "메일 전송이 성공했습니다.");
        } else {
            attrs.addFlashAttribute("msg", "메일 전송이 실패했습니다.");
        }

        return "redirect:/main_menu";
    }

    /**
     * FormParser 클래스를 사용하지 않고 Spring Framework에서 이미 획득한 매개변수 정보를 사용하도록 기존
     * webmail 소스 코드를 수정함.
     *
     * * @param to
     * @param cc
     * @param sub
     * @param body
     * @param upFile
     * @return
     */
    private boolean sendMessage(String to, String cc, String subject, String body, MultipartFile upFile) {
        boolean status = false;

        String host = (String) session.getAttribute("host");
        String userid = (String) session.getAttribute("userid");

        SmtpAgent agent = new SmtpAgent(host, userid);
        agent.setTo(to);
        agent.setCc(cc);
        agent.setSubj(subject);
        agent.setBody(body);
        String fileName = upFile.getOriginalFilename();

        if (fileName != null && !"".equals(fileName)) {
            log.debug("sendMessage: 파일({}) 첨부 필요", fileName);
            File f = new File(ctx.getRealPath(UPLOAD_FOLDER) + File.separator + fileName);
            agent.setFile1(f.getAbsolutePath());
        }

        if (agent.sendMessage()) {
            status = true;
        }
        return status;
    }  // sendMessage()
}
