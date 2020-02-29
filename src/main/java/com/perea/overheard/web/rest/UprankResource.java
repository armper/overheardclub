package com.perea.overheard.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * UprankResource controller
 */
@RestController
@RequestMapping("/api/uprank")
public class UprankResource {

    private final Logger log = LoggerFactory.getLogger(UprankResource.class);

    /**
    * POST uprank
    */
    @PostMapping("/uprank")
    public String uprank() {
        return "uprank";
    }

}
