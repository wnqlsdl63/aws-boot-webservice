package com.jubin.example.springboot.web;

import com.jubin.example.springboot.service.PostsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("postsList", postsService.findAllDesc());
        return "index";
    }

    @GetMapping("/posts/save")
    public String saveView() {
        return "posts-save";
    }

}
