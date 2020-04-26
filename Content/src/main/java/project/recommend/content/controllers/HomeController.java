package project.recommend.content.controllers;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import project.recommend.content.helper.RegexHelper;
import project.recommend.content.helper.RetrofitHelper;
import project.recommend.content.helper.WebHelper;

@Controller
public class HomeController {

    @Autowired
    WebHelper webHelper;
    
    @Autowired
    RegexHelper regexHelper;
    
    @Autowired
    RetrofitHelper retrofitHelper;
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Locale locale, Model model) {
        return "home";
    }

}
