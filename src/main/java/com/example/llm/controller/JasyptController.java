package com.example.llm.controller;

import com.example.llm.util.JasyptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jasypt")
public class JasyptController {

    @Autowired
    private JasyptUtil jasyptUtil;

    @PostMapping
    public String encrypt(@RequestBody String val) {
        return jasyptUtil.encrypt(val);
    }

    @PutMapping
    public String decrypt(@RequestBody String val) {
        return jasyptUtil.decrypt(val);
    }
}
