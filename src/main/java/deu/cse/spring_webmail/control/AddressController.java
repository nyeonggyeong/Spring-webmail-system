package deu.cse.spring_webmail.control;

import deu.cse.spring_webmail.model.AddressBookAgent;
import deu.cse.spring_webmail.model.AddressBookDto;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
public class AddressController {

    @Autowired
    private HttpSession session;

    @Autowired
    private AddressBookAgent addressBookAgent;

    // 1. 주소록 메인 화면 띄우기 (목록 조회)
    @GetMapping("/address_book")
    public String addressBook(Model model) {
        String userid = (String) session.getAttribute("userid");
        if (userid == null) return "redirect:/"; 

        model.addAttribute("addressList", addressBookAgent.getAddressList(userid));
        return "address_book/address_book";
    }

    // 2. 주소록 신규 등록 요청 처리
    @PostMapping("/address_book.do")
    public String addAddressDo(@RequestParam String name, @RequestParam String email, 
                               @RequestParam String phone, RedirectAttributes attrs) {
        String userid = (String) session.getAttribute("userid");
        if (userid == null) return "redirect:/";

        if (name == null || name.trim().isEmpty()) {
            attrs.addFlashAttribute("msg", "이름을 반드시 입력해야 합니다.");
            return "redirect:/address_book";
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (email == null || !email.matches(emailRegex)) {
            attrs.addFlashAttribute("msg", "올바른 이메일 형식이 아닙니다. (예: test@gmail.com)");
            return "redirect:/address_book";
        }

        AddressBookDto dto = new AddressBookDto();
        dto.setUserid(userid);
        dto.setName(name.trim()); // 양끝 공백 제거 후 저장
        dto.setEmail(email.trim());
        dto.setPhone(phone.trim());

        if (addressBookAgent.addAddress(dto)) {
            attrs.addFlashAttribute("msg", "주소록 등록에 성공했습니다.");
        } else {
            attrs.addFlashAttribute("msg", "주소록 등록에 실패했습니다.");
        }
        return "redirect:/address_book";
    }
    @GetMapping("/address_delete.do")
    public String deleteAddress(@RequestParam int id, RedirectAttributes attrs) {
        String userid = (String) session.getAttribute("userid");
        if (userid == null) return "redirect:/"; // 로그인 안 되어있으면 튕겨냄

        // DB 삭제 에이전트 호출
        if (addressBookAgent.deleteAddress(id, userid)) {
            attrs.addFlashAttribute("msg", "주소록이 안전하게 삭제되었습니다.");
        } else {
            attrs.addFlashAttribute("msg", "주소록 삭제에 실패했습니다.");
        }
        
        // 삭제 후 다시 주소록 목록 화면으로 새로고침
        return "redirect:/address_book";
    }
}