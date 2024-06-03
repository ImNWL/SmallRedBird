package com.sky.constant;

/**
 * 信息提示常量类
 */
public class MessageConstant {

    public static final String ACCOUNT_LOCKED = "账号被锁定";
    public static final String ALREADY_EXIST = "账号已存在";
    public static final String UNKNOWN_ERROR = "未知错误";
    public static final String USER_NOT_LOGIN = "用户未登录";
    public static final String CATEGORY_BE_RELATED_BY_SETMEAL = "当前分类关联了套餐,不能删除";
    public static final String CATEGORY_BE_RELATED_BY_DISH = "当前分类关联了菜品,不能删除";
    public static final String SHOPPING_CART_IS_NULL = "购物车数据为空，不能下单";
    public static final String ADDRESS_BOOK_IS_NULL = "用户地址为空，不能下单";
    public static final String LOGIN_FAILED = "登录失败";
    public static final String UPLOAD_FAILED = "文件上传失败";
    public static final String SETMEAL_ENABLE_FAILED = "套餐内包含未启售菜品，无法启售";
    public static final String PASSWORD_EDIT_FAILED = "密码修改失败";
    public static final String DISH_ON_SALE = "起售中的菜品不能删除";
    public static final String SETMEAL_ON_SALE = "起售中的套餐不能删除";
    public static final String DISH_BE_RELATED_BY_SETMEAL = "当前菜品关联了套餐,不能删除";
    public static final String ORDER_STATUS_ERROR = "订单状态错误";
    public static final String ORDER_NOT_FOUND = "订单不存在";

    public static final String PHONE_EXISTS = "手机号已经被注册";
    public static final String PHONE_NOT_EXISTS = "手机号未注册";
    public static final String TRY_LATER = "操作太频繁，请稍后再试";
    public static final String KAFKA_PROBLEM = "Kafka服务失败";
    public static final String ACCOUNT_NOT_FOUND = "账号不存在";
    public static final String PASSWORD_ERROR = "密码错误";
    public static final String ERROR = "错误";

    public static final String KAFKA_TOPIC_USER_REGISTER = "user-register";
    public static final String KAFKA_TOPIC_USER_DELETE = "user-delete";

}
