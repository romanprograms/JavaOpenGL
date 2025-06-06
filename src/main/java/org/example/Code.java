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
    private int vao[] = new int[1];

    public Code()
    {	setTitle("Chapter 2 - program 2");
        setSize(600, 400);
        myCanvas = new GLCanvas();
        myCanvas.addGLEventListener(this);
        this.add(myCanvas);
        this.setVisible(true);
    }

    public void display(GLAutoDrawable drawable)
    {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glUseProgram(renderingProgram);
//        gl.glPointSize(1.0f);
//        gl.glPointSize(30.0f);
        gl.glDrawArrays(GL4.GL_POINTS,0,1);
    }

    public void init(GLAutoDrawable drawable)
    {	GL4 gl = (GL4) GLContext.getCurrentGL();
        renderingProgram = createShaderProgram();
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
    }

    private int createShaderProgram() {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        String vshaderSource [] =
                {
                        "#version 430    \n",
                        "void main(void) \n",
                        "{ gl_Position = vec4(0.0, 0.0, 0.0, 1.0); gl_PointSize = 30.0;} \n"
                };

        String fshaderSource [] =
                {
                        "#version 430    \n",
                        "out vec4 color; \n",
                        "void main(void) \n",
                        "{ color = vec4(0.0, 0.0, 1.0, 1.0); } \n"
                };

//        	String fshaderSource[] =
//        	{	"#version 430    \n",
//        		"out vec4 color; \n",
//        		"void main(void) \n",
//        		"{ if (gl_FragCoord.x < 295) color = vec4(1.0, 0.0, 0.0, 1.0); else color = vec4(0.0, 0.0, 1.0, 1.0); } \n"
//        	};

        int vShader = gl.glCreateShader(GL4.GL_VERTEX_SHADER);
        gl.glShaderSource(vShader, 3, vshaderSource, null, 0);
        gl.glCompileShader(vShader);

        int fShader = gl.glCreateShader(GL4.GL_FRAGMENT_SHADER);
        gl.glShaderSource(fShader, 4, fshaderSource, null, 0);
        gl.glCompileShader(fShader);

        int vfprogram = gl.glCreateProgram();
        gl.glAttachShader(vfprogram, vShader);
        gl.glAttachShader(vfprogram, fShader);
        gl.glLinkProgram(vfprogram);

        gl.glDeleteShader(vShader);
        gl.glDeleteShader(fShader);
        return vfprogram;
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
    public void dispose(GLAutoDrawable drawable) {}

}