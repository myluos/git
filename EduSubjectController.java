package com.online.edu.eduservice.controller;


import com.online.edu.common.R;
import com.online.edu.eduservice.service.EduSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 课程科目 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2019-04-16
 */
@RestController
@RequestMapping("/eduservice/subject")
@CrossOrigin
public class EduSubjectController {

    @Autowired
    private EduSubjectService eduSubjectService;

    //通过上传excel文件获取文件内容
    @PostMapping("import")
    public R importExcelSubject(@RequestParam("file") MultipartFile file) {
        //1 获取上传excel文件
        List<String> msg = eduSubjectService.importSubject(file);
        if(msg.size()==0) {
            return R.ok();
        } else {
            return R.error().message("部分数据导入失败").data("msgList",msg);
        }
    }

}

