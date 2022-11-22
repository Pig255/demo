package com.itheima.aScenary.exception;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.04.11
 */
public class CMDException extends RuntimeException {
    public CMDException(String description) {
        super(description);
    }
}
