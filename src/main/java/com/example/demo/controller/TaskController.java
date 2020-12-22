package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.common.RestfulEntity;
import com.example.demo.entity.Task;
import com.example.demo.entity.TaskDto;
import com.example.demo.entity.TaskSaveDto;
import com.example.demo.service.TaskService;
import com.example.demo.service.UserService;
import io.kubernetes.client.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(value = "任务管理", tags = "任务管理接口")
@RestController
@RequestMapping("/algoPlatform/task")
public class TaskController {
    @Autowired
    TaskService taskService;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @ApiOperation(value = "添加任务")
    @PostMapping(value = "/_add")
    public RestfulEntity<JSONObject> taskAdd(@ApiParam(value = "用户信息", required = true)
                                                @RequestBody TaskSaveDto taskSaveDto) {
        /*
        * userToken校验
        *  logger.info("_add:finish user check");*/
        logger.info("任务添加，用户："+taskSaveDto.getUserResult().getUserName()+"," +
                "用户权限："+taskSaveDto.getUserResult().getAuthority()+",任务名称"+taskSaveDto.getTaskName());
        RestfulEntity<JSONObject>result=taskService.saveToTask(taskSaveDto);
        return result;
    }
    @ApiOperation(value = "开启任务")
    @PostMapping(value = "_run")
    public RestfulEntity<JSONObject> taskRun(
            @ApiParam(value = "用户信息", required = true)
            @RequestBody TaskDto taskDto) {
        /*userToken校验*/
        String taskId=taskDto.getTaskId();
        logger.info("任务启动，用户："+taskDto.getUserResult().getUserName()+
                "，用户权限："+taskDto.getUserResult().getAuthority()+",任务编号："+taskId);

        try{
            RestfulEntity<JSONObject>result=taskService.taskRun(taskDto);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject res=new JSONObject();
            res.put("taskId",taskId);
            return RestfulEntity.getFailure(res,e.toString());
        }
    }
    @ApiOperation(value = "停止任务")
    @PostMapping(value = "_stop")
    public RestfulEntity<JSONObject> taskStop(
            @ApiParam(value = "用户信息", required = true)
            @RequestBody TaskDto taskDto) {
        /*
        * UserToken校验*/
        logger.info("任务停止，用户："+taskDto.getUserResult().getUserName()+"，用户权限："+taskDto.getUserResult().getAuthority()+"，任务编号："+taskDto.getTaskId());
        try{
            RestfulEntity<JSONObject>result=taskService.taskStop(taskDto);
            return result;
        } catch (ApiException e) {
            e.printStackTrace();
            return RestfulEntity.getFailure(2,e.toString());
        }
    }
    @ApiOperation(value = "查找任务")
    @PostMapping(value = "_search")
    public RestfulEntity<JSONObject> taskSearch(
            @ApiParam(value = "用户信息", required = true)
            @RequestBody TaskDto taskDto) {
        /*
         * UserToken校验*/
        logger.info("任务查询，任务名称："+taskDto.getTaskName());
        RestfulEntity<JSONObject>result=taskService.taskSearch(taskDto);
        return result;
    }
/*    @ApiOperation(value = "插入测试")
    @PostMapping(value = "/insert")
    public RestfulEntity<JSONObject> taskInsert(HttpServletRequest request,
                                                @ApiParam(value = "用户信息", required = true)
                                                @RequestBody TaskSaveDto taskSaveDto) {
        RestfulEntity<JSONObject> ret = taskService.saveToTask(taskSaveDto);
        System.out.println(ret);
        return ret;
    }

    @ApiOperation(value = "查询测试")
    @PostMapping(value = "/query")
    public Task taskQuery(HttpServletRequest request,
                          @ApiParam(value = "用户信息", required = true)
                          @RequestBody TaskDto taskDto) {
        Task task = taskService.queryTask(taskDto);
        System.out.println(task);
        return task;
    }

    @ApiOperation(value = "更新测试")
    @PostMapping(value = "/update")
    public int taskUpdate(HttpServletRequest request,
                          @ApiParam(value = "用户信息", required = true)
                          @RequestBody TaskDto taskDto) {
        int ret = taskService.updateTask(taskDto);
        System.out.println(ret);
        return ret;
    }

    @ApiOperation(value = "删除测试")
    @PostMapping(value = "/delete")
    public int taskDelete(HttpServletRequest request,
                          @ApiParam(value = "用户信息", required = true)
                          @RequestBody TaskDto taskDto) {
        int ret = taskService.deleteTask(taskDto);
        System.out.println(ret);
        return ret;
    }

    @ApiOperation(value = "id查询测试")
    @PostMapping(value = "/queryTaskIds")
    public int taskIdsQuery(HttpServletRequest request,
                            @ApiParam(value = "用户信息", required = true)
                            @RequestBody TaskDto taskDto) {
        List<String> taskIdsList = taskService.queryTaskIds(taskDto);
        System.out.println(taskIdsList);
        return taskIdsList.size();
    }*/
}
