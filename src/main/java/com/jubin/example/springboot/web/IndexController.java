package com.jubin.example.springboot.web;

import com.jubin.example.springboot.config.auth.LoginUser;
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
    public String index(Model model, @LoginUser SessionUser user) {
        model.addAttribute("postsList", postsService.findAllDesc());

        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        System.out.println("test");
        System.out.println("test3");

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
