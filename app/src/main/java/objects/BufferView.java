package objects;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import example.com.hairrenderer.R;
import utils.LogUtil;
import utils.MatrixState;
import utils.ShaderHelper;
import utils.TextResourceReader;
import utils.TextureHelper;

/**
 * Created by Wujiqiang on 2017/3/3.
 */

public class BufferView {

    private Context context;

    private HairBuffer m_hairBuffer;

    private static final String TAG = "BufferView";

    // float 类型的字节数
    private static final int BYTES_PER_FLOAT = 4;
    // 纹理坐标数
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    // 每个顶点所含坐标数
    private static final int COORDS_PER_VERTEX = 3;

    private FloatBuffer[] vertexBuffer;
    private FloatBuffer[] textureBuffer;

    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    private static final String U_MATRIX = "u_Matrix";
    private int uMatrixLocation;

    private static final String U_COLOR = "u_Color";
    private int uColorLocation;

    private static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
    private int aTextureCoordinates;

    private static final String U_TEXTURE_UNIT = "u_TextureUnit";
    private int uTextureUnitLocation;

    private int texture;

    private int program;


    float[][] hairVertices;
    float[][] hairTextureCoords;

    static int temp = 0;
    public BufferView(Context context) {
        m_hairBuffer = new HairBuffer(context);
        this.context = context;

        String filename = "2000point.txt";
        m_hairBuffer.LoadFromFile(filename);

        if (m_hairBuffer.m_pStrands.length == m_hairBuffer.m_uNumStrands) {
            Log.d(TAG, "BufferView: equal length");
        } else {
            Log.d(TAG, "BufferView: not equal");
        }

        // 头发顶点和纹理坐标读取以及存储传递
        hairVertices = new float[m_hairBuffer.m_uNumStrands][];
        hairTextureCoords = new float[m_hairBuffer.m_uNumStrands][];
        for (int i = 0; i < m_hairBuffer.m_uNumStrands; i++) {
            hairVertices[i] = new float[m_hairBuffer.m_pStrands[i].m_uNumVertices * COORDS_PER_VERTEX];
            hairTextureCoords[i] = new float[m_hairBuffer.m_pStrands[i].m_uNumVertices * TEXTURE_COORDINATES_COMPONENT_COUNT];
        }
        // 逐根存储头发的顶点数据和纹理坐标（使用float数组存储）
        for (int i = 0; i < m_hairBuffer.m_uNumStrands; i++) {

            LogUtil.v(TAG, "Strand " + i + " : "+ m_hairBuffer.m_pStrands[i].m_uNumVertices);

            int vCount = 0;
            int tCount = 0;
            for (int j = 0; j < m_hairBuffer.m_pStrands[i].m_uNumVertices; j++ ) {
                hairVertices[i][vCount++] = m_hairBuffer.m_pStrands[i].m_pVertices[j].position.getX();
                hairVertices[i][vCount++] = m_hairBuffer.m_pStrands[i].m_pVertices[j].position.getY();
                hairVertices[i][vCount++] = m_hairBuffer.m_pStrands[i].m_pVertices[j].position.getZ();

                hairTextureCoords[i][tCount++] = m_hairBuffer.m_pStrands[i].m_pVertices[j].reserved.getX();
                hairTextureCoords[i][tCount++] = m_hairBuffer.m_pStrands[i].m_pVertices[j].reserved.getY();
            }
        }

        // 顶点数据和纹理坐标数据 缓存 （用于传递给着色器）
        vertexBuffer = new FloatBuffer[m_hairBuffer.m_uNumStrands];
        textureBuffer = new FloatBuffer[m_hairBuffer.m_uNumStrands];
        for (int i = 0; i < m_hairBuffer.m_uNumStrands; i++) {
            vertexBuffer[i] = ByteBuffer
                    .allocateDirect(hairVertices[i].length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            vertexBuffer[i].put(hairVertices[i]);
            vertexBuffer[i].position(0);

            textureBuffer[i] = ByteBuffer
                    .allocateDirect(hairTextureCoords[i].length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            textureBuffer[i].put(hairTextureCoords[i]);
            textureBuffer[i].position(0);
        }

        getProgram();
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);

        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);

            /*uColorLocation = GLES20.glGetUniformLocation(program, U_COLOR);
            GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);*/

        aTextureCoordinates = GLES20.glGetAttribLocation(program, A_TEXTURE_COORDINATES);
        uTextureUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);
        texture = TextureHelper.loadTexture(context, R.drawable.hairtexture, false);
        // Set the active texture unit to texture unit 0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // Bind the texture to this unit
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        // Tell the texture uniform sampler to use this texture in the shader by
        // telling it to read from texture unit 0
        GLES20.glUniform1i(uTextureUnitLocation, 0);


        LogUtil.v("Loading..", "done~");
    }

    public HairBuffer getM_hairBuffer() {
        return m_hairBuffer;
    }


    public void RenderHair() {
        // 逐根绘制头发
        for (int i = 0; i < m_hairBuffer.m_uNumStrands; i++){

            // Load verteices and textures coordinates
            GLES20.glVertexAttribPointer(aPositionLocation, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, vertexBuffer[i]);
            GLES20.glEnableVertexAttribArray(aPositionLocation);

            GLES20.glVertexAttribPointer(aTextureCoordinates, TEXTURE_COORDINATES_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, textureBuffer[i]);
            GLES20.glEnableVertexAttribArray(aTextureCoordinates);

            // Load matrix and draw hairs
            GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, MatrixState.getFinalMatrix(), 0);
            GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, m_hairBuffer.m_pStrands[i].m_uNumVertices);

        }

    }

    // Get program
    private void getProgram() {
        // 获取顶点着色器文本
        String vertexShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.vertex_shader);
        // 获取片段着色器文本
        String fragmentShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.fragment_shader);
        // 获取program 的id
        program = ShaderHelper.buildProgram(vertexShaderSource, fragmentShaderSource);
        GLES20.glUseProgram(program);
    }

}

