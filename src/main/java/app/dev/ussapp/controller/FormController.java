package app.dev.ussapp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FormController {

    @PostMapping("/join-class")
    @ResponseBody
    public Map<String, Object> joinClass(@RequestBody Map<String, Object> data) {
        HashMap<String, Object> map = new HashMap<>();

        map.put("status", "VALID");

        return map;
    }
}
