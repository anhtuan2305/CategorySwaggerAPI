package vn.tuan.categoryswaggerapi.controller.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AjaxPageController {

    @GetMapping("/ajax/categories")
    public String categoryAjaxPage() {
        return "ajax/category";
    }

    @GetMapping("/ajax/test")
    @ResponseBody
    public String test() {
        return "Ajax controller hoạt động";
    }
}