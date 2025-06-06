package org.example;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;

import javax.swing.JFrame;

public class Code extends JFrame implements GLEventListener {
    private GLCanvas myCanvas;
    private int renderingProgram;
    private int vao[] = new int[1];

    public Code()
    {
        setTitle("Chapter 2 - program 2");
        setSize(600, 400);
        GLProfile profile = GLProfile.get(GLProfile.GL4); // Request GL4 (Core)
        GLCapabilities caps = new GLCapabilities(profile);
        caps.setGLProfile(profile); // Explicit, but redundant here
        // doesn't seem like this function exists
        // caps.setForwardCompatible(true); // Required for core profile
        caps.setHardwareAccelerated(true);

        myCanvas = new GLCanvas(caps);
        myCanvas.addGLEventListener(this);
        this.add(myCanvas);
        this.setVisible(true);
    }

    public void display(GLAutoDrawable drawable)
    {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        // clear the screen before drawing
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        gl.glUseProgram(renderingProgram);
        gl.glPointSize(40.0f);
        gl.glDrawArrays(GL4.GL_POINTS,0,1);
    }

    public void init(GLAutoDrawable drawable)
    {

        GL4 gl = (GL4) GLContext.getCurrentGL();

        System.out.println("GL Version: " + gl.glGetString(GL.GL_VERSION));
        System.out.println("GLSL Version: " + gl.glGetString(GL4.GL_SHADING_LANGUAGE_VERSION));

        renderingProgram = createShaderProgram();
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
    }

    private int createShaderProgram()
    {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        int[] vertCompiled = new int[1];
        int[] fragCompiled = new int[1];
        int[] linked = new int[1];

        String vshaderSource[] =
                {	"#version 410 \n",
                        "void main(void) \n",
                        "{ gl_Position = vec4(0.0, 0.0, 0.5, 1.0); } \n",
                };
//        String fshaderSource[] =
//                {	"#version 410 \n",
//                        "out vec4 color; \n",
//                        "void main(void) \n",
//                        "{ color = vec4(0.0, 0.0, 1.0, 1.0); } \n"
//                };
        	String fshaderSource[] =
        	{	"#version 410    \n",
        		"out vec4 color; \n",
        		"void main(void) \n",
        		"{ if (gl_FragCoord.x < 295) color = vec4(1.0, 0.0, 0.0, 1.0); else color = vec4(0.0, 0.0, 1.0, 1.0); } \n"
        	};



        int vShader = gl.glCreateShader(GL4.GL_VERTEX_SHADER);
        gl.glShaderSource(vShader, 3, vshaderSource, null, 0);
        gl.glCompileShader(vShader);

        checkOpenGLError();  // can use returned boolean if desired
        gl.glGetShaderiv(vShader, GL4.GL_COMPILE_STATUS, vertCompiled, 0);
        if (vertCompiled[0] != 1)
        {	System.out.println("vertex shader compilation failed");
            printShaderLog(vShader);
        }

        int fShader = gl.glCreateShader(GL4.GL_FRAGMENT_SHADER);
        gl.glShaderSource(fShader, 4, fshaderSource, null, 0);
        gl.glCompileShader(fShader);

        checkOpenGLError();  // can use returned boolean if desired
        gl.glGetShaderiv(fShader, GL4.GL_COMPILE_STATUS, fragCompiled, 0);
        if (fragCompiled[0] != 1)
        {	System.out.println("fragment shader compilation failed");
            printShaderLog(fShader);
        }

        int vfprogram = gl.glCreateProgram();
        gl.glAttachShader(vfprogram, vShader);
        gl.glAttachShader(vfprogram, fShader);

        gl.glLinkProgram(vfprogram);
        checkOpenGLError();
        gl.glGetProgramiv(vfprogram, GL4.GL_LINK_STATUS, linked, 0);
        if (linked[0] != 1)
        {	System.out.println("linking failed");
            printProgramLog(vfprogram);
        }

        gl.glDeleteShader(vShader);
        gl.glDeleteShader(fShader);
        return vfprogram;
    }

    private void printShaderLog(int shader)
    {	GL4 gl = (GL4) GLContext.getCurrentGL();
        int[] len = new int[1];
        int[] chWrittn = new int[1];
        byte[] log = null;

        // determine the length of the shader compilation log
        gl.glGetShaderiv(shader, GL4.GL_INFO_LOG_LENGTH, len, 0);
        if (len[0] > 0)
        {	log = new byte[len[0]];
            gl.glGetShaderInfoLog(shader, len[0], chWrittn, 0, log, 0);
            System.out.println("Shader Info Log: ");
            for (int i = 0; i < log.length; i++)
            {	System.out.print((char) log[i]);
            }
        }
    }

    void printProgramLog(int prog)
    {	GL4 gl = (GL4) GLContext.getCurrentGL();
        int[] len = new int[1];
        int[] chWrittn = new int[1];
        byte[] log = null;

        // determine length of the program compilation log
        gl.glGetProgramiv(prog, GL4.GL_INFO_LOG_LENGTH, len, 0);
        if (len[0] > 0)
        {	log = new byte[len[0]];
            gl.glGetProgramInfoLog(prog, len[0], chWrittn, 0, log, 0);
            System.out.println("Program Info Log: ");
            for (int i = 0; i < log.length; i++)
            {	System.out.print((char) log[i]);
            }
        }
    }

    boolean checkOpenGLError()
    {	GL4 gl = (GL4) GLContext.getCurrentGL();
        boolean foundError = false;
        GLU glu = new GLU();
        int glErr = gl.glGetError();
        while (glErr != GL4.GL_NO_ERROR)
        {	System.err.println("glError: " + glu.gluErrorString(glErr));
            foundError = true;
            glErr = gl.glGetError();
        }
        return foundError;
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
    public void dispose(GLAutoDrawable drawable) {}

}