package com.lad.controller;

import com.lad.service.IPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/7
 */
@Controller
@RequestMapping("/party")
public class PartyController extends BaseContorller {

    @Autowired
    private IPartyService partyService;

    @RequestMapping("/create")
    @ResponseBody
    public String create(@RequestParam String party,
                          @RequestParam("backPic") MultipartFile backPic,
                          @RequestParam("images") MultipartFile images,
                          @RequestParam("video") MultipartFile video,
                          HttpServletRequest request, HttpServletResponse response){


        return "";
    }



}
