package com.azha.azha_take_out.controller;

import com.azha.azha_take_out.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class UploadController {
    @Value("${azha.path}")
    private String downloadPath;
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        log.info(file.toString());
        //使用UUID重新生成文件名，防止文件覆盖
        String originalFilename=file.getOriginalFilename();
        String suffix=originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName= UUID.randomUUID().toString()+suffix;
        log.info("生成的名字是{}",fileName);
        //创建一个目录对象
        File dir =new File(downloadPath);
        if (!dir.exists()){
            dir.mkdir();
        }
        try {
            //file只是个临时文件，一定要转存到指定位置，否则请求完成就删除了
            file.transferTo(new File(downloadPath+fileName));
        }catch (IOException e){
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /*
    文件下载
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
            //输入流，读取文件内容
            if(new File(downloadPath+name).exists()){
                FileInputStream inputStream = new FileInputStream(new File(downloadPath+name));
                //输出流，将文件写回浏览器，在浏览器展示图片
                ServletOutputStream outputStream = response.getOutputStream();

                response.setContentType("image/jpeg");
                int len = 0;
                byte[] bytes = new byte[1024];
                while ((len = inputStream.read(bytes))!= -1){
                    outputStream.write(bytes,0,len);
                    outputStream.flush();
                }
                //关闭资源
                inputStream.close();
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
