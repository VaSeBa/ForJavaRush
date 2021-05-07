package com.game.controller;

import com.game.entity.Player;
import com.game.service.PlayerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class PlayerController {
    private PlayerService playerService;

    @GetMapping("/rest/players")
    public String findAll(Model model) {
        List<Player> players = playerService.allPlayer();
        model.addAttribute("player", players);

        return "index";
    }
}
