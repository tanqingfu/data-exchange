package com.sdy.dataexchange.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 * jump class
 *页面跳转接口
 * @author 高连明
 * @date 2019/08/12
 */
@Slf4j
@Controller
@RequestMapping(value = "/jump")
public class JumpController {
    @GetMapping("/gatherTest")
    public String gatherTest() {
        return "/page/gatherTest";
    }
    @GetMapping("/exDbDictPage")
    public String exDbDictPage() {
        return "/page/exDbDictPage";
    }

    @GetMapping("/addDbMapping")
    public String addDbMapping() {
        return "/page/addDbMappingPage";
    }

    @GetMapping("/gatherDict")
    public String gatherDict() {
        return "/page/gatherDict";
    }

    @GetMapping("/exDbDictPage/addDbDict")
    public String addDbDict() {
        return "/page/addDbPage";
    }

    @GetMapping("/addGatherDict")
    public String addGatherDict() {
        return "/page/addGatherDict";
    }

    @GetMapping("/taskPage/additionalTasks")
    public String additionalTasks() {
        return "/page/additionalTasks";
    }

    @GetMapping("/taskPage/editTask")
    public String editTask(Integer id, String gatherDesc, String taskName, String sourceDb, String sourceTable
            , String destDb, String destTable, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("taskName", taskName);
        model.addAttribute("sourceDb", sourceDb);
        model.addAttribute("gatherDesc", gatherDesc);
        model.addAttribute("sourceTable", sourceTable);
        model.addAttribute("destDb", destDb);
        model.addAttribute("destTable", destTable);
        return "/page/editTask";
    }

    @GetMapping("/addFields")
    public String addFields() {
        return "/page/addFields";
    }

    @GetMapping("/taskPage")
    public String taskPage() {
        return "/page/taskPage";
    }

    @GetMapping("/fieldMapping")
    public String fieldMapping() {
        return "/page/fieldMapping";
    }

    @GetMapping("/addUserInfo")
    public String addUserInfo() {
        return "/page/addUserInfo";
    }

    @GetMapping("/sourceDb")
    public String sourceDb() {
        return "/page/sourceDb";
    }

    @GetMapping("/jobPage")
    public String jobPage() {
        return "/page/ex_job_info/page";
    }

    @GetMapping("/exchangeInfo")
    public String exchangeInfo() {
        return "/page/exchangeInfo";
    }

    @GetMapping("/jobList")
    public String jobList() {
        return "/page/jobList";
    }
}
