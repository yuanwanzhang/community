package life.majiang.community.controller;

import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.model.UserExample;
import life.majiang.community.service.NotificationService;
import life.majiang.community.service.QuestionService;
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
public class ProfileController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/profile/{action}")
    public String profile(HttpServletRequest request,
                          @PathVariable(name = "action") String action,
                          Model model,
                          @RequestParam(name="page",defaultValue = "1") Integer page,
                          @RequestParam(name="size",defaultValue = "5") Integer size) {
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
                        User user = (User) request.getSession().getAttribute("user");
                        Long unreadCount = notificationService.unreadCount(user.getId());
                        request.getSession().setAttribute("unreadCount",unreadCount);
                    }
                    if(users.get(0) == null)
                    {
                        model.addAttribute("error","用户未登录");
                        return "publish";
                    }
                    if ("questions".equals(action)) {
                        model.addAttribute("section", "questions");
                        model.addAttribute("sectionName", "我的提问");
                        PaginationDTO paginationDto = questionService.list(users.get(0).getId(), page, size);
                        model.addAttribute("pagination",paginationDto);
                    }else if("replies".equals(action)){
                        PaginationDTO paginationDTO = notificationService.list(users.get(0).getId(), page, size);
                        model.addAttribute("section", "replies");
                        model.addAttribute("pagination", paginationDTO);
                        model.addAttribute("sectionName", "最新回复");
                    }
                    break;
                }
            }
        }
        return "profile";
    }
}
