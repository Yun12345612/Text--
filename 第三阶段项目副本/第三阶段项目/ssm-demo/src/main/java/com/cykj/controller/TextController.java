//package com.cykj.controller;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//@Controller
//@RequestMapping("/test")
//public class TextController {
//
//    @RequestMapping("/a")
//    @ResponseBody//动态资源
//    public user test(String acc, String pwd) {
//        user user = new user(1, "zhangsan");
//        return user;
//
//    }
//
//
//    //方法一:a.jsp直接放到与web-inf同级文件下
////    @RequestMapping("/b")
////    public String test1(){
////        return "/a.jsp";
////    }
//    //方法二:把a.jsp放到web-inf文件底下,springmvc中添加识别jsp的配置,返回一个"a"即可
//    @RequestMapping("/b")
//    public String test1() {
//        return "a";
//    }
//
//
//    @RequestMapping("/c")
//    public String test2() {
//        return "login";
//    }
//
//    //方法一:通过static/images直接访问静态资源
////    @RequestMapping("/d")
////    public String test3(){
////        return "三体1.png";
////    }
//    //方法二:通过外部盘符访问
//    @RequestMapping("/d")
//    public String test3() {
//        return "redirect:/external-images/三体1.png";
//    }
//
//}
