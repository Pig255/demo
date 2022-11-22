package com.itheima.aScenary.listener;

import com.itheima.aScenary.container.CMDContainer;
import com.itheima.aScenary.entity.Face;
import com.itheima.aScenary.repository.FaceRepository;
import com.itheima.aScenary.repository.ShaderRepository;
import com.itheima.aScenary.util.Camera;
import com.itheima.aScenary.util.NormalUtil;
import com.itheima.aScenary.util.Shader;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.GLBuffers;
import glm.mat._4.Mat4;
import glm.vec._3.Vec3;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author XL Shi
 * @email xueli_shi@foxmail.com
 * @date 2022.04.13
 */
// TODO: 研究一下鼠标响应怎么设置
public class GLListener implements GLEventListener, KeyListener {

    // 常数
    private final int EBO_SIZE = 10000;
    private final int SCR_WIDTH = 800;
    private final int SCR_HIGHT = 600;

    // 从容器导入bean
    private FaceRepository faceRepository = CMDContainer.getListner(FaceRepository.class);
    private ShaderRepository shaderRepository = CMDContainer.getListner(ShaderRepository.class);
    private Camera camera = CMDContainer.getListner(Camera.class);

    // 时间记录，交互使用
    private long deltaTime = 0;
    private long lastTime = System.currentTimeMillis();

    // 域
    private Shader shader;
    private List<IntBuffer> vaoList;
    private List<IntBuffer> vboList;

    private GLWindow window;
    private Animator animator;

    // 总集方法
    public void setUp() {
        GLProfile glProfile = GLProfile.get(GLProfile.GL3);
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);

        camera.setPosition(new Vec3(-100.0f, 0.0f, -1000.0f));

        window = GLWindow.create(glCapabilities);
        window.setTitle("Compressor Mechanics Digitization");
        window.setSize(SCR_WIDTH, SCR_HIGHT);
        window.setVisible(true);
        window.addGLEventListener(this);
        window.addKeyListener(this);

        animator = new Animator(window);
        animator.start();

        window.addWindowListener(new WindowAdapter() {
            public void windowDestroyed(WindowEvent windowEvent) {
                animator.stop();
                System.exit(1);
            }
        });
    }

    // 初始化缓存
    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        vaoList = new ArrayList<>();
        vboList = new ArrayList<>();
        Collection<Face> faces = faceRepository.getDrawing();

        // 1. 由faces组装缓存
        IntBuffer vao = null,
                vbo = null;
        float[] vertices = null;
        int counter = EBO_SIZE; // 分组计数
        Iterator<Face> faceIterator = faces.iterator();

        while (faceIterator.hasNext()) {
            Face face = faceIterator.next();
            if (counter >= EBO_SIZE || !faceIterator.hasNext()) {
                // 绑定上一个vbo
                if (vbo != null) {
                    FloatBuffer verticesBuffer = GLBuffers.newDirectFloatBuffer(vertices);
                    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo.get(0));
                    gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.length * Float.BYTES,
                            verticesBuffer, GL.GL_STATIC_DRAW);
                    gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 6 * Float.BYTES, 0);
                    gl.glEnableVertexAttribArray(0);
                    gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
                    gl.glEnableVertexAttribArray(1);
                    vaoList.add(vao);
                    vboList.add(vbo);
                }
                // 生成下一个vbo
                vao = IntBuffer.allocate(1);
                vbo = IntBuffer.allocate(1);
                gl.glGenBuffers(1, vbo);
                gl.glGenVertexArrays(1, vao);
                gl.glBindVertexArray(vao.get(0));
                vertices = new float[EBO_SIZE * 18];
                counter = 0;
            }

            double[] faceColor = face.getColor().getColorCode();
            for (int i = 0; i < 3; i++) { // 一个面元的3个顶点
                for (int j = 0; j < 3; j++) { // 一个顶点的6个参数
                    vertices[18 * counter + 6 * i + j] = face.getNodes()[i].getCoord()[j];
                    vertices[18 * counter + 6 * i + 3 + j] = (float) faceColor[j];
                }
            }
            counter++;
        }

        // 2. 初始化着色器、摄像机，开启深度测试，开启面剔除
        shader = new Shader(shaderRepository.getVertexShaderCode(), shaderRepository.getFragmentShaderCode(), gl);
        gl.glEnable(GL.GL_DEPTH_TEST);
        // TODO: 进一步优化：面剔除，即剔除背面的面，要求生成面元时顶点环绕顺序一致
        // gl.glEnable(GL.GL_CULL_FACE);
    }

    // 资源清理
    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {
        GL3 gl = glAutoDrawable.getGL().getGL3();
        for (IntBuffer vao : vaoList) {
            gl.glDeleteVertexArrays(1, vao);
        }
        for (IntBuffer vbo : vboList) {
            gl.glDeleteBuffers(1, vbo);
        }
        shader.clear(gl);
    }

    // 循环绘制
    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        long curFram = System.currentTimeMillis();
        deltaTime = curFram - lastTime;
        lastTime = curFram;

        GL3 gl = glAutoDrawable.getGL().getGL3();
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        shader.use(gl);

        // 视角变换
        Mat4 model = new Mat4(1.0f);
        Mat4 view = camera.getViewMartrix();
        Mat4 projection = new Mat4();
        projection = projection.perspective((float) NormalUtil.angleToRadian(10.0),
                (float) SCR_WIDTH / (float) SCR_HIGHT, 0.1f, 50000.0f);
        shader.setMat4("model", model.toDfb_(), gl);
        shader.setMat4("view", view.toDfb_(), gl);
        shader.setMat4("projection", projection.toDfb_(), gl);

        for (IntBuffer vao : vaoList) {
            gl.glBindVertexArray(vao.get(0));
            // gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE); // 线元模式
            gl.glDrawArrays(GL.GL_TRIANGLES, 0, 3 * EBO_SIZE);
        }

        // shader.drop(gl);
        // gl.glBindVertexArray(0);
    }


    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {
        GL3 gl = glAutoDrawable.getGL().getGL3();
        gl.glViewport(i, i1, i2, i3);
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
            new Thread(() -> window.destroy()).start();
        }
        camera.processKeyboard(keyEvent, deltaTime);
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }


}
