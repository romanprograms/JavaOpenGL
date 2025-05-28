package org.example;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ShaderCube implements GLEventListener {

    private int shaderProgram;
    private int vaoId;
    private float angle = 0.0f;

    private static final String VERTEX_SHADER = """
    #version 330 core
    layout(location = 0) in vec3 position;
    layout(location = 1) in vec3 color;

    uniform float angle;
    uniform mat4 projection;
    uniform mat4 view;

    out vec3 vColor;

    void main() {
        float s = sin(angle);
        float c = cos(angle);
        mat4 rotY = mat4(
            c, 0, -s, 0,
            0, 1,  0, 0,
            s, 0,  c, 0,
            0, 0,  0, 1
        );
        gl_Position = projection * view * rotY * vec4(position, 1.0);
        vColor = color;
    }
""";

    private static final String FRAGMENT_SHADER = """
        #version 330 core
        in vec3 vColor;
        out vec4 fragColor;
        void main() {
            fragColor = vec4(vColor, 1.0);
        }
    """;

    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        shaderProgram = compileShaders(gl);

        float[] vertices = {
                // Positions        // Colors
                -1, -1, -1, 1, 0, 0,
                1, -1, -1, 0, 1, 0,
                1, 1, -1, 0, 0, 1,
                -1, 1, -1, 1, 1, 0,
                -1, -1, 1, 0, 1, 1,
                1, -1, 1, 1, 0, 1,
                1, 1, 1, 1, 1, 1,
                -1, 1, 1, 0, 0, 0,};

        int[] indices = {
                0, 1, 2, 2, 3, 0, // Back
                4, 5, 6, 6, 7, 4, // Front
                0, 4, 7, 7, 3, 0, // Left
                1, 5, 6, 6, 2, 1, // Right
                3, 2, 6, 6, 7, 3, // Top
                0, 1, 5, 5, 4, 0 // Bottom
        };

        int[] vao = new int[1];
        gl.glGenVertexArrays(1, vao, 0);
        vaoId = vao[0];
        gl.glBindVertexArray(vaoId);

        int[] vbo = new int[2];
        gl.glGenBuffers(2, vbo, 0);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[0]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.length * Float.BYTES, FloatBuffer.wrap(vertices), GL.GL_STATIC_DRAW);

        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 6 * Float.BYTES, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, vbo[1]);
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indices.length * Integer.BYTES, IntBuffer.wrap(indices), GL.GL_STATIC_DRAW);

        gl.glBindVertexArray(0);
        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    private int compileShaders(GL3 gl) {
        int vShader = gl.glCreateShader(GL3.GL_VERTEX_SHADER);
        gl.glShaderSource(vShader, 1, new String[]{VERTEX_SHADER}, null, 0);
        gl.glCompileShader(vShader);

        int fShader = gl.glCreateShader(GL3.GL_FRAGMENT_SHADER);
        gl.glShaderSource(fShader, 1, new String[]{FRAGMENT_SHADER}, null, 0);
        gl.glCompileShader(fShader);

        int program = gl.glCreateProgram();
        gl.glAttachShader(program, vShader);
        gl.glAttachShader(program, fShader);
        gl.glLinkProgram(program);

        gl.glDeleteShader(vShader);
        gl.glDeleteShader(fShader);

        return program;
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glDeleteProgram(shaderProgram);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glUseProgram(shaderProgram);

        int angleLoc = gl.glGetUniformLocation(shaderProgram, "angle");
        gl.glUniform1f(angleLoc, angle);

        int projLoc = gl.glGetUniformLocation(shaderProgram, "projection");
        int viewLoc = gl.glGetUniformLocation(shaderProgram, "view");

        float aspect = (float) drawable.getSurfaceWidth() / drawable.getSurfaceHeight();
        float[] projection = perspective(45, aspect, 0.1f, 100f);
        float[] view = lookAt(
                new float[]{0, 0, 6},
                new float[]{0, 0, 0},
                new float[]{0, 1, 0}
        );

        gl.glUniformMatrix4fv(projLoc, 1, false, projection, 0);
        gl.glUniformMatrix4fv(viewLoc, 1, false, view, 0);

        gl.glBindVertexArray(vaoId);
        gl.glDrawElements(GL.GL_TRIANGLES, 36, GL.GL_UNSIGNED_INT, 0);
        gl.glBindVertexArray(0);

        angle += 0.02f;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        drawable.getGL().glViewport(0, 0, w, h);
    }

    private float[] perspective(float fov, float aspect, float zNear, float zFar) {
        float f = 1.0f / (float) Math.tan(fov * Math.PI / 360.0);
        float range = zNear - zFar;

        return new float[]{
                f / aspect, 0, 0, 0,
                0, f, 0, 0,
                0, 0, (zFar + zNear) / range, -1,
                0, 0, (2 * zFar * zNear) / range, 0
        };
    }

    private float[] lookAt(float[] eye, float[] center, float[] up) {
        float[] f = normalize(subtract(center, eye));
        float[] s = normalize(cross(f, up));
        float[] u = cross(s, f);

        return new float[]{
                s[0], u[0], -f[0], 0,
                s[1], u[1], -f[1], 0,
                s[2], u[2], -f[2], 0,
                -dot(s, eye), -dot(u, eye), dot(f, eye), 1
        };
    }

    private float[] subtract(float[] a, float[] b) {
        return new float[]{a[0] - b[0], a[1] - b[1], a[2] - b[2]};
    }

    private float[] normalize(float[] v) {
        float len = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        return new float[]{v[0] / len, v[1] / len, v[2] / len};
    }

    private float[] cross(float[] a, float[] b) {
        return new float[]{
                a[1] * b[2] - a[2] * b[1],
                a[2] * b[0] - a[0] * b[2],
                a[0] * b[1] - a[1] * b[0]
        };
    }

    private float dot(float[] a, float[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    public  void run() {
        GLProfile profile = GLProfile.get(GLProfile.GL3);
        GLCapabilities caps = new GLCapabilities(profile);
        GLCanvas canvas = new GLCanvas(caps);

        JFrame frame = new JFrame("Shader-Based Rotating Cube");
        ShaderCube renderer = new ShaderCube();
        canvas.addGLEventListener(renderer);
        frame.add(canvas);
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new FPSAnimator(canvas, 60).start();
    }
}