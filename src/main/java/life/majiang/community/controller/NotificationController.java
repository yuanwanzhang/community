package life.majiang.community.controller;

import life.majiang.community.dto.NotificationDTO;
import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.enums.NotificationEnum;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.model.UserExample;
import life.majiang.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class NotificationController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification/{id}")
    public String profile(@PathVariable(name = "id") Long id,HttpServletRequest request,Model model) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length != 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    String token = cookie.getValue();
                    UserExample userExample = new UserExample();
                    userExample.createCriteria()
                            .andTokenEqualTo(token);
                    List<User> users = userMapper.selectByExample(userExample);
                    if (users.size() != 0) {
                        request.getSession().setAttribute("user", users.get(0));
                    }
                    if (users.get(0) == null) {
                        model.addAttribute("error", "用户未登录");
                        return "redirect:/";
                    }
                    NotificationDTO notificationDTO = notificationService.read(id, users.get(0));
                    if (NotificationEnum.RELY_COMMENT.getType() == notificationDTO.getType()
                            || NotificationEnum.RELY_QUESTION.getType() == notificationDTO.getType()) {
                        return "redirect:/question/" + notificationDTO.getOuterid();
                    } else {
                        return "redirect:/";
                    }
                }
            }
        }
        return "redirect:/";
    }
}
