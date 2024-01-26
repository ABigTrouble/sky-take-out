package com.sky.common;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    @ApiOperation("上传图片")
    // 参数名一定要为null，和html中的name一样！！！
    public Result<String> upload(MultipartFile file) {
        log.info(file.getName());

        // 获取文件名，得到后缀，拼接新的名称
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        String name = UUID.randomUUID().toString() + extension;

        // 上传到alioss
        try {
            String fileName = aliOssUtil.upload(file.getBytes(), name);
            // 返回文件地址给客户端回显

            return Result.success(fileName);
        } catch (IOException e) {
            log.info("文件上传失败:{}", e.getMessage());
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);

    }
}
