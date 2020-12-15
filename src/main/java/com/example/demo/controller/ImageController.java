package com.example.demo.controller;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.common.RestfulEntity;
import com.example.demo.entity.ImageDto;
import com.example.demo.service.ImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Api(value = "镜像管理", tags = "镜像管理接口")
@RestController
@RequestMapping("/algoPlatform/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @ApiOperation(value = "镜像插入")
    @PostMapping(value = "/_register")
    public RestfulEntity<JSONObject> insertTest(@ApiParam(value = "用户信息", required = true)
                                                @RequestBody @Validated(ImageDto.addGroup.class) ImageDto imageDto) {
        RestfulEntity<JSONObject> ret = imageService.imageInsert(imageDto);
        return ret;
    }

    @ApiOperation(value = "条件查询")
    @PostMapping(value = "/_search")
    public RestfulEntity<JSONObject> queryImageByConditionTest(@ApiParam(value = "用户信息", required = true)
                                                               @RequestBody ImageDto imageDto) {
        RestfulEntity<JSONObject> result = imageService.queryImageInfoByCondition(imageDto);
        return result;
    }

    @ApiOperation(value = "镜像更新")
    @PostMapping(value = "/_update")
    public RestfulEntity<JSONObject> updateTest(@ApiParam(value = "用户信息", required = true)
                                                @RequestBody @Validated(ImageDto.updateGroup.class) ImageDto imageDto) {
        RestfulEntity<JSONObject> ret = imageService.updateImageInfoTest(imageDto);
        return ret;
    }

    @ApiOperation(value = "镜像删除")
    @PostMapping(value = "/_delete")
    public RestfulEntity<JSONObject> deleteTest(@ApiParam(value = "用户信息", required = true)
                                                @RequestBody @Validated(ImageDto.deleteImageGroup.class) ImageDto imageDto) {
        RestfulEntity<JSONObject> ret = imageService.deleteImageInfo(imageDto);
        return ret;
    }

    @ApiOperation(value = "镜像配置信息下载")
    @PostMapping(value = "/_load")
    public RestfulEntity<JSONObject> downloadImage(@ApiParam(value = "用户信息", required = true)
                                                   @RequestBody ImageDto imageDto) throws IOException {
        RestfulEntity<JSONObject> ret = imageService.downloadImage(imageDto.getSearchCondition());
        return ret;
    }

    @ApiOperation(value = "镜像配置信息上传")
    @PostMapping(value = "/_upload")
    public RestfulEntity<JSONObject> loadImage(@ApiParam(value = "用户信息", required = true)
                                               @RequestBody MultipartFile file) throws Exception {

        File newFile = new File(file.getOriginalFilename());
        File temp = FileUtil.writeFromStream(file.getInputStream(), newFile);
        //imageService.uploadImage(newFile);
        RestfulEntity<JSONObject> res = imageService.uploadImage(newFile);
        FileUtil.del(temp);
        return res;
    }

}
