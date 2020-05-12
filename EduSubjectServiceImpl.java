package com.online.edu.eduservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.online.edu.eduservice.entity.EduSubject;
import com.online.edu.eduservice.handler.EduException;
import com.online.edu.eduservice.mapper.EduSubjectMapper;
import com.online.edu.eduservice.service.EduSubjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2019-04-16
 */
@Service
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {

    //读取excel内容，添加分类表里面
    @Override
    public List<String> importSubject(MultipartFile file) {
        try {
            //1 获取文件输入流
            InputStream in = file.getInputStream();
            //2 创建workbook
            Workbook workbook = new HSSFWorkbook(in);
            //3 workbook获取sheet
            Sheet sheet = workbook.getSheetAt(0);

            //为了存储错误信息
            List<String> msg = new ArrayList<>();
            //4 sheet获取row
            //循环遍历获取行，从第二行开始获取数据
            int lastRowNum = sheet.getLastRowNum();
            // sheet.getRow(0)
            for (int i = 1; i <= lastRowNum; i++) {
                //得到excel每一行
                Row row = sheet.getRow(i);
                //如果行为空，提示错误信息
                if(row == null) {
                    String str = "表格数据为空，请输入数据";
                    msg.add(str);
                   continue;
                }
                //行数据不为空
                //5 row获取第一列
                Cell cellOne = row.getCell(0);
                //判断列是否为空
                if(cellOne == null) {
                    String str = "第"+i+"行数据为空";
                    // 跳出这一行，往下继续执行
                    msg.add(str);
                    continue;
                }
                //列不为空获取数据,第一列值
                //一级分类值
                String cellOneValue = cellOne.getStringCellValue();
                //添加一级分类
                //因为excel里面有很多重复的一级分类
                //在添加一级分类之前判断：判断要添加的一级分类在数据库表是否存在，如果不存在添加。
                EduSubject eduSubjectExist = this.existOneSubject(cellOneValue);
                //存储一级分类id
                String id_parent = null;
                if(eduSubjectExist == null) {//如果不存在添加
                    //添加
                    EduSubject eduSubject = new EduSubject();
                    eduSubject.setTitle(cellOneValue);
                    eduSubject.setParentId("0");
                    eduSubject.setSort(0);
                    baseMapper.insert(eduSubject);
                    //把新添加的一级分类id获取到进行赋值
                    id_parent = eduSubject.getId();
                } else {//存在，不添加
                    //把一级分类id赋值id_parent
                    id_parent = eduSubjectExist.getId();
                }

                //5 row获取第二列
                Cell cellTwo = row.getCell(1);
                if(cellTwo == null) {
                    String str = "第"+i+"行数据为空";
                    // 跳出这一行，往下继续执行
                    msg.add(str);
                    continue;
                }
                //不为空，获取第二列值
                String cellTwoValue = cellTwo.getStringCellValue();
                //添加二级分类
                //判断数据库表是否存储二级分类，如果不存在进行添加
                EduSubject twoSubjectExist = this.existTwoSubject(cellTwoValue, id_parent);
                if(twoSubjectExist == null) {
                    EduSubject twoSubject = new EduSubject();
                    twoSubject.setTitle(cellTwoValue);
                    twoSubject.setParentId(id_parent);
                    twoSubject.setSort(0);
                    baseMapper.insert(twoSubject);
                }
            }
            return msg;
        }catch(Exception e) {
            e.printStackTrace();
            throw new EduException(20001,"导入失败出现异常");
        }
    }

    //判断数据库表是否存在二级分类
    private EduSubject existTwoSubject(String name,String parentid) {
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        //拼接条件
        wrapper.eq("title",name);
        wrapper.eq("parent_id",parentid);
        EduSubject eduSubject = baseMapper.selectOne(wrapper);
        return eduSubject;
    }

    // 判断数据库表是否存在一级分类
    private EduSubject existOneSubject(String name) {
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        //拼接条件
        wrapper.eq("title",name);
        wrapper.eq("parent_id","0");
        //调用方法
        EduSubject eduSubject = baseMapper.selectOne(wrapper);
        return eduSubject;
    }
}
