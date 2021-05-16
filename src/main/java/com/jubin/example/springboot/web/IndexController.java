package com.jubin.example.springboot.web;

import com.jubin.example.springboot.config.auth.dto.SessionUser;
import com.jubin.example.springboot.service.PostsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;
    private final HttpSession httpSession;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("postsList", postsService.findAllDesc());

        SessionUser user = (SessionUser) httpSession.getAttribute("user");

        if (user != null) {
            model.addAttribute("userName", user.getName());
        }

        return "index";
    }

    @GetMapping("/posts/save")
    public String saveView() {
        return "posts-save";
    }

    @GetMapping("/posts/update/{id}")
    public String updateView(@PathVariable Long id, Model model) {

        model.addAttribute("post", postsService.findById(id));
        return "posts-update";
    }

}
