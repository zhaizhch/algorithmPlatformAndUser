package com.example.demo.common;

public enum DisplayErrorCode {
    nullTaskImage(206,"任务关联镜像已删除"),
    nullTask(205,"无此任务ID"),
    taskCheck(2, "该任务名称已被占用"),
    pvcSizeCheck(231, "pvc申请容量范围为[1,20]"),
    imageCheck(2, "该镜像未注册"),
    hostAliasesCheck(208, "域名格式错误，格式为:“ip  hostname，ip hostname”、IP格式错误、域名格式错误。首尾字符为数字或小写字母，中间字符为数字或小写字母或特殊字符/.-_"),
    getStartTypeCheck(202, "starttype:0或1   立即启动方式 0:是,1:否"),
    algoTypeCheck(202, "algotype:0或1   0视频分析、1图片分析"),
    taskTypeCheck(202, "tasktype:0或1   0：后台任务、1：在线服务"),
    taskNameCheck(213, "算子任务名称格式：首位字符为小写字母，中间字符为数字或小写字母或特殊字符- ，结尾为小写字母或数字"),
    imageTagCheck(212, "镜像格式错误"),
    imageNameCheck(221, "镜像名称格式：首尾字符为数字或小写字母，中间字符为数字或小写字母或特殊字符/.-_"),
    tagCheck(222, "tag格式：首尾字符为数字或小写字母，中间字符为数字或小写字母或特殊字符.-_"),
    formatCheck(202, "输入格式有误"),
    portsCheck(208, "端口格式：端口类型：端口号，端口类型：端口号,端口类型为a-z小写字母。例：ssh：22，jupyter：8888"),
    imageMountCheck(215, "请输入合法的路径名称。例/data-ym/vol_09"),
    configCheck(209, "请输入正确的配置文件信息，可包含：“A-Za-z0-9.-”例/data/gelin-k8s.config"),
    subPathCheck(210, "请输入合法的子目录映射。例/data-ym/vol.config:/data/vol.config"),
    hostPathCheck(223, "请输入合法的主机目录映射。例/data-ym/vol:/data/vol"),
    workingDirCheck(214, "请输入合法的路径名称。例/data-ym/vol_09"),
    envCheck(217, "请输入合法环境变量，例如 NAME:ZHANGSAN9527"),
    eventTypeCheck(211, "请输入合法的算法类型:0或1或2"),
    dependencyServiceIdCheck(216, "自研算法算法依赖不能为空"),
    uniqCheck(203, "已存在此镜像"),
    dependencyAlgoCheck(308, "请检查依赖算法是否正确：镜像名:标签名,镜像名:标签名"),
    imageQuerySuccess(0, "镜像查询成功"),
    updataImageNameTagCheck(219, "不支持算法名称和标签更改"),
    imageIdErrCheck(204, "镜像不存在"),
    uniqueCheck(203, "已存在此镜像"),
    imageDependencyErr(220, "无法删除被依赖镜像"),
    authorityErr(240, "权限异常"),
    USER_LOGOUT_FAILURE(234, "注销失败"),
    USER_STATUS_CHECK(230, "请登录"),
    USER_RESULTFAILURE(234, "用户名或密码错误"),
    LOGIN_MESSAGE_UPDATE_FAILURE(235, "登录信息更新失败"),
    USER_NAME_LENGTH_ERROR(1021, "用户名最长32位，已超过长度限制"),
    USER_NAME_EXIST_ERROR(1022, "用户名已存在"),
    USER_PWD_FORMAT_ERROR(1023, "密码不符合要求，长度大于等于8位数、包含大写字母、小写字母、数字和特殊字符"),
    USER_AUTHORITY_CHECK_ERROR(1025, "用户权限格式错误"),
    USER_NO_OPERATION_AUTHORITY(1027, "用户无操作权限"),
    USER_RESETPWD_ERROR(1029, "用户重置密码失败"),
    USER_RAWPWD_CHECK_ERROR(1030, "用户初始密码错误"),
    USER_RAWPWD_NULL_ERROR(1031, "用户初始密码不能为空"),
    DB_QUERY_ERROR(1005, "数据查询失败"),
    TOKEN_QUERY_ERROR(231, "认证失败"),
    DB_UPDATE_ERROR(1006, "数据修改失败"),
    DB_DELETE_ERROR(1007, "数据删除失败"),
    DB_INSERT_ERROR(1008, "数据增加失败"),
    PARAM_INVALID_ERROR(1002, "输入参数校验不通过");


    private final Integer val;
    private String message;

    DisplayErrorCode(Integer val) {
        this.val = val;
    }

    DisplayErrorCode(Integer val, String message) {
        this.val = val;
        this.message = message;
    }

    public Integer getVal() {
        return val;
    }

    public String getMessage() {
        return message;
    }

}
