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

    // 💡 new 키워드 대신 스프링에게 주소록 에이전트 관리를 위임함 (예방 유지보수 적용)
    @Autowired
    private AddressBookAgent addressBookAgent;

    // 1. 주소록 메인 화면 띄우기 (목록 조회)
    @GetMapping("/address_book")
    public String addressBook(Model model) {
        String userid = (String) session.getAttribute("userid");
        if (userid == null) return "redirect:/"; 

        // 인스턴스를 직접 생성하지 않고 주입받은 빈(Bean) 사용
        model.addAttribute("addressList", addressBookAgent.getAddressList(userid));
        return "address_book/address_book";
    }

    // 2. 주소록 신규 등록 요청 처리
    @PostMapping("/address_book.do")
    public String addAddressDo(@RequestParam String name, @RequestParam String email, 
                               @RequestParam String phone, RedirectAttributes attrs) {
        String userid = (String) session.getAttribute("userid");
        
        AddressBookDto dto = new AddressBookDto();
        dto.setUserid(userid);
        dto.setName(name);
        dto.setEmail(email);
        dto.setPhone(phone);

        // 주입받은 빈(Bean) 사용
        if (addressBookAgent.addAddress(dto)) {
            attrs.addFlashAttribute("msg", "주소록 등록에 성공했습니다.");
        } else {
            attrs.addFlashAttribute("msg", "주소록 등록에 실패했습니다.");
        }
        return "redirect:/address_book";
    }
}