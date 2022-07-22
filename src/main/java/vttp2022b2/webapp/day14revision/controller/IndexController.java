package vttp2022b2.webapp.day14revision.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import vttp2022b2.webapp.day14revision.model.Cart;

@Controller
@RequestMapping(path = "/")
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @GetMapping
    public String showShoppingCartForm(Model model) {
        Cart c = new Cart("");
        model.addAttribute("cart", c);
        return "shoppingcart";
    }
}
