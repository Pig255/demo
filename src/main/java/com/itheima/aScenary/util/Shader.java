package com.itheima.aScenary.util;

import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL3;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.04.10
 */
public class Shader {
    private int id;

    public Shader(String vertCode, String fragCode, GL3 gl) {
        // 1. 编译着色器
        int vertShader, fragShader;
        vertShader = gl.glCreateShader(GL2ES2.GL_VERTEX_SHADER);
        gl.glShaderSource(vertShader, 1, new String[]{vertCode}, null);
        gl.glCompileShader(vertShader);
        checkCompileErrors(vertShader, ComPileType.SHADER, gl);
        fragShader = gl.glCreateShader(GL2ES2.GL_FRAGMENT_SHADER);
        gl.glShaderSource(fragShader, 1, new String[]{fragCode}, null);
        gl.glCompileShader(fragShader);
        checkCompileErrors(fragShader, ComPileType.SHADER, gl);
        // 2. 链接程序
        this.id = gl.glCreateProgram();
        gl.glAttachShader(id, vertShader);
        gl.glAttachShader(id, fragShader);
        gl.glLinkProgram(id);
        checkCompileErrors(id, ComPileType.PROGRAM, gl);
        // 3. 清理资源
        gl.glDeleteShader(vertShader);
        gl.glDeleteShader(fragShader);
    }

    public void use(GL3 gl) {
        gl.glUseProgram(id);
    }

    public void drop(GL3 gl) {
        gl.glUseProgram(0);
    }

    public void clear(GL3 gl) {
        gl.glDeleteProgram(id);
    }

    public void setBool(String name, boolean value, GL3 gl) {
        gl.glUniform1i(gl.glGetUniformLocation(id, name), value ? 1 : 0);
    }

    public void setFloat(String name, float value, GL3 gl) {
        gl.glUniform1f(gl.glGetUniformLocation(id, name), value);
    }

    public void setMat4(String name, FloatBuffer value, GL3 gl) {
        gl.glUniformMatrix4fv(gl.glGetUniformLocation(id, name), 1, false, value);
    }

    public void setVec3(String name, FloatBuffer value, GL3 gl) {
        gl.glUniform3fv(gl.glGetUniformLocation(id, name), 1, value);
    }

    public void setVec3(String name, float x, float y, float z, GL3 gl) {
        gl.glUniform3f(gl.glGetUniformLocation(id, name), x, y, z);
    }

    private void checkCompileErrors(int shader, ComPileType type, GL3 gl) {
        IntBuffer flag = IntBuffer.allocate(1);
        ByteBuffer infoLog = ByteBuffer.allocate(1024);
        switch (type) {
            case SHADER:
                gl.glGetShaderiv(shader, GL2ES2.GL_COMPILE_STATUS, flag);
                if (flag.get(0) == 0) {
                    gl.glGetShaderInfoLog(shader, 1024, null, infoLog);
                    System.out.println("ERROR::SHADER_COMPILATION_ERROR of type: ");
                    System.out.println(NormalUtil.byteArrToString(infoLog.array()));
                }
                break;
            case PROGRAM:
                gl.glGetProgramiv(shader, GL2ES2.GL_LINK_STATUS, flag);
                if (flag.get(0) == 0) {
                    gl.glGetProgramInfoLog(shader, 1024, null, infoLog);
                    System.out.println("ERROR::PROGRAM_LINKING_ERROR of type: ");
                    System.out.println(NormalUtil.byteArrToString(infoLog.array()));
                    gl.glDeleteProgram(id);
                }
                break;
        }
    }

    private enum ComPileType {
        SHADER,
        PROGRAM;
    }
}
