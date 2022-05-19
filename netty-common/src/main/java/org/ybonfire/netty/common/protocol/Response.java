package org.ybonfire.netty.common.protocol;

import java.io.Serializable;

/**
 * 远程调用响应结构
 *
 * @author Bo.Yuan5
 * @date 2022-05-19 15:52
 */
public class Response<T> implements Serializable {

    private Integer code;
    private String message;
    private T data;

    private Response(final Integer code, final String message, final T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
