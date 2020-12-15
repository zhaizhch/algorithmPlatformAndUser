package com.example.demo.mapper;

import com.example.demo.entity.Task;
import com.example.demo.entity.TaskDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author User
 */
@Mapper
@Component
public interface TaskMapper {
    List<Task> queryTask(TaskDto taskDto);

    int insertTaskInfo(TaskDto taskDto);

    int updateTask(TaskDto taskDto);

    int deleteTask(TaskDto taskDto);

    List<String> queryTaskIds(TaskDto taskDto);
}
