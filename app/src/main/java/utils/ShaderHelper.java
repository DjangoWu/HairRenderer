package utils;

import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

/**
 * Created by Wujiqiang on 2017/2/15.
 */

public class ShaderHelper {
    private static final String TAG = "ShaderHelper";

    /**
     * Loads and compiles a vertex shader, returning the OpenGL object ID.
     */
    public static int compileVertexShader(String shaderCode) {
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }

    /**
     * Loads and compiles a fragment shader, returning the OpenGL object ID.
     */
    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    /**
     * Compiles a shader, returning the OpenGL object ID.
     */
    private static int compileShader(int type, String shaderCode) {
        // Create a new shader object.
        final int shaderObjectId = glCreateShader(type);

        if (shaderObjectId == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not create new shader.");
            }

            return 0;
        }

        // Pass in the shader source.
        glShaderSource(shaderObjectId, shaderCode);

        // Compile the shader.
        glCompileShader(shaderObjectId);

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS,
                compileStatus, 0);

        if (LoggerConfig.ON) {
            // Print the shader info log to the Android log output.
            Log.v(TAG, "Results of compiling source:" + "\n" + shaderCode
                    + "\n:" + glGetShaderInfoLog(shaderObjectId));
        }

        // Verify the compile status.
        if (compileStatus[0] == 0) {
            // If it failed, delete the shader object.
            glDeleteShader(shaderObjectId);

            if (LoggerConfig.ON) {
                Log.w(TAG, "Compilation of shader failed.");
            }

            return 0;
        }

        // Return the shader object ID.
        return shaderObjectId;
    }

    /**
     * Links a vertex shader and a fragment shader together into an OpenGL
     * program. Returns the OpenGL program object ID, or 0 if linking failed.
     */
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {

        // Create a new program object.
        final int programObjectId = glCreateProgram();

        if (programObjectId == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not create new program");
            }

            return 0;
        }

        // Attach the vertex shader to the program.
        glAttachShader(programObjectId, vertexShaderId);

        // Attach the fragment shader to the program.
        glAttachShader(programObjectId, fragmentShaderId);

        // Link the two shaders together into a program.
        glLinkProgram(programObjectId);

        // Get the link status.
        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS,
                linkStatus, 0);

        if (LoggerConfig.ON) {
            // Print the program info log to the Android log output.
            Log.v(
                    TAG,
                    "Results of linking program:\n"
                            + glGetProgramInfoLog(programObjectId));
        }

        // Verify the link status.
        if (linkStatus[0] == 0) {
            // If it failed, delete the program object.
            glDeleteProgram(programObjectId);

            if (LoggerConfig.ON) {
                Log.w(TAG, "Linking of program failed.");
            }

            return 0;
        }

        // Return the program object ID.
        return programObjectId;
    }

    /**
     * Validates an OpenGL program. Should only be called when developing the
     * application.
     */
    public static boolean validateProgram(int programObjectId) {
        glValidateProgram(programObjectId);
        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS,
                validateStatus, 0);
        Log.v(TAG, "Results of validating program: " + validateStatus[0]
                + "\nLog:" + glGetProgramInfoLog(programObjectId));

        return validateStatus[0] != 0;
    }

    /**
     * Helper function that compiles the shaders, links and validates the
     * program, returning the program ID.
     */
    public static int buildProgram(String vertexShaderSource,
                                   String fragmentShaderSource) {
        int program;

        // Compile the shaders.
        int vertexShader = compileVertexShader(vertexShaderSource);
        int fragmentShader = compileFragmentShader(fragmentShaderSource);

        // Link them into a shader program.
        program = linkProgram(vertexShader, fragmentShader);

        if (LoggerConfig.ON) {
            validateProgram(program);
        }

        return program;
    }
}

/*public class ShaderHelper {

    private static final String TAG = "ShaderHelper";

    // 加载并编译顶点着色器，返回得到的opengles id
    public static int compileVertexShader(String shaderCode)
    {
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode);
    }

    // 加载并编译片段着色器，返回得到opengles id
    public static int compileFragmentShader(String shaderCode)
    {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode);
    }

    private static int compileShader(int type, String shaderCode)
    {
        // 建立新的着色器对象
        final int shaderObjectId = GLES20.glCreateShader(type);

        if (shaderObjectId == 0)
        {
            if (LoggerConfig.ON)
            {
                Log.w(TAG, "不能创建新的着色器");
            }
            return 0;
        }

        // 传递着色器资源代码
        GLES20.glShaderSource(shaderObjectId, shaderCode);

        // 编译着色器
        GLES20.glCompileShader(shaderObjectId);

        // 获取编译的状态
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        if (LoggerConfig.ON)
        {
            // 打印Log
            Log.v(TAG, "代码编译结果:" + "\n" + shaderCode + "\n:" + GLES20.glGetShaderInfoLog(shaderObjectId));
        }

        // 确认编译的状态
        if (compileStatus[0] == 0)
        {
            // 如果编译失败，则删除该对象
            GLES20.glDeleteShader(shaderObjectId);

            if(LoggerConfig.ON)
            {
                Log.w(TAG, "编译失败！");
            }
            return 0;
        }

        // 返回着色器的opengles id
        return shaderObjectId;
    }

    *//*
    * 链接顶点着色器和片段着色器成一个program
    * 并返回这个program的opengles id
    * @param vertexShaderID
    * @param fragmentShaderID*//*
    public static int linkProgram(int vertexShaderId, int fragmentShaderId)
    {
        // 新建一个program对象
        final int programObjectId = GLES20.glCreateProgram();

        if (programObjectId == 0)
        {
            if (LoggerConfig.ON){
                Log.w(TAG, "不能新建一个program");
            }
            return 0;
        }

        // Attach the vertex shader to the program
        GLES20.glAttachShader(programObjectId, vertexShaderId);

        // Attach the fragment shader to the program
        GLES20.glAttachShader(programObjectId, fragmentShaderId);

        // 将两个着色器链接成一个program
        GLES20.glLinkProgram(programObjectId);

        // 获取连接状态
        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_LINK_STATUS, linkStatus, 0);

        if (LoggerConfig.ON)
        {
            // Print the program info to the Android log output
            Log.v(
                    TAG,
                    "Results of linking program:\n"
                            + GLES20.glGetProgramInfoLog(programObjectId)
            );
        }

        // 验证连接状态
        if (linkStatus[0] == 0)
        {
            // if it failed, delete the program object
            GLES20.glDeleteProgram(programObjectId);

            if (LoggerConfig.ON)
            {
                Log.w(TAG, "连接 program 失败！");
            }

            return 0;
        }

        // Return the program object ID
        return programObjectId;
    }

    *//*
    * Validates an OpenGL program. Should only be called when developing the application
    * *//*
    public static boolean validateProgram(int programObjectId)
    {
        GLES20.glValidateProgram(programObjectId);
        final int[] validateStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0);
        Log.v(TAG, "Results of validating program: " + validateStatus[0] + "\nLog: " + GLES20.glGetProgramInfoLog(programObjectId));

        return validateStatus[0] != 0;
    }

    *//*
    * 编译，连接，返回 program 的 ID
    * @param vertexShaderSource
    * @param fragmentShaderSource
    * @return*//*
    public static int buildProgram(String vertexShaderSource, String fragmentShaderSource)
    {
        int program;

        // Compile the shaders
        int vertexShader = compileVertexShader(vertexShaderSource);
        int fragmentShader = compileFragmentShader(fragmentShaderSource);

        // Link them into a shader program
        program = linkProgram(vertexShader, fragmentShader);

        if (LoggerConfig.ON){
            validateProgram(program);
        }

        return program;
    }

}*/

