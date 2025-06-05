package org.example;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;

import javax.swing.JFrame;

public class Code extends JFrame implements GLEventListener {
    private GLCanvas myCanvas;
    private int renderingProgram;

    public Code() {
       setTitle("Chapter 2 program 1");
       setSize(600, 400);
       setLocation(200,200);
       myCanvas = new GLCanvas();
       myCanvas.addGLEventListener(this);
       this.add(myCanvas);
       this.setVisible(true);
    }
    public void display(GLAutoDrawable drawable){
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glClearColor(1.0f, 0.0f, 0.0f , 1.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
    }
    public void init(GLAutoDrawable drawable) {
        renderingProgram = createShaderProgram();
    }
    public void dispose(GLAutoDrawable drawable) {}
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}

    private int createShaderProgram() {
        // TODO:
        return 0;
    }
}