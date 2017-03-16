package objects;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import utils.LogUtil;
import utils.Vector3;

/**
 * Created by Wujiqiang on 2017/3/2.
 */

public class HairBuffer {

    private final static String TAG = "HairBuffer";

    public HairStrand[] m_pStrands;
    //public List<HairStrand> m_pStrands;  如果List版本会出问题 就换这个对象数组版
    public int m_uNumStrands;
    public int m_uNumTotalVerts;
    public int u_MaxSegs;

    private Context context;

    public HairBuffer(Context context) {
        //m_pStrands = new ArrayList<>();
        this.context = context;

        m_uNumStrands = 0;
        m_uNumTotalVerts = 0;
        m_pStrands = new HairStrand[m_uNumStrands];
    }

    //------------------------------------------------------------------
    // Clear existing data.
    //------------------------------------------------------------------
    public void Clear() {
        //m_pStrands.clear();
        m_uNumStrands = 0;
        m_uNumTotalVerts = 0;
        m_pStrands = new HairStrand[0];
    }

    //------------------------------------------------------------------
    // Getters.
    //------------------------------------------------------------------

    /*public List<HairStrand> GetStrands() {
        return m_pStrands;
    }*/

    public HairStrand[] GetStrands() {
        return m_pStrands;
    }

    public int GetNumStrands() {
        return m_uNumStrands;
    }

    public int GetNumTotalVerts() {
        return m_uNumTotalVerts;
    }

    //------------------------------------------------------------------
    // Load from file.
    //------------------------------------------------------------------
    // Load hair data from SHD file.(txt.File)
    public boolean LoadFromFile(String strFilename) {
        if (strFilename.isEmpty()) {
            LogUtil.d(TAG, "LoadFromSHD: Invalid input!");
            return false;
        }

        // Clear existing data
        Clear();

        // Open data file
        LogUtil.d(TAG, "LoadFromFile: Loading..." + strFilename);

        try {
            //BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(strFilename))));
            FileInputStream in = context.openFileInput(strFilename);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            if (br == null) {
                LogUtil.d(TAG, "LoadFromSHD: Cannot open file!");
                return false;
            }
            String str = null;

            int uNumStrands = 0, uNumCurrent =0, uNumTotalVerts = 0;
            // Number of Strands
            uNumStrands = new Integer(br.readLine());
            LogUtil.d(TAG, "uNumStrands = " + uNumStrands);
            //List<HairStrand> pStrands = new ArrayList<>(uNumStrands);
            HairStrand[] pStrands = new HairStrand[uNumStrands];

            // Load strands
            u_MaxSegs = 0;       // Use to store hair has max vertex numbers
            int uTempSize = 512;    // Initialize the number of vertex (changeable)
            //List<Vector.Vector3> pTemp = new ArrayList<>(uTempSize);
            //Vector.Vector3[] pTemp = new Vector.Vector3[uTempSize];
            for (int i = 0; i < uNumStrands; i++) {
                //Read number of vertices in this strand
                int uNumVertices = new Integer(br.readLine());  // 读取第一个元素（当前头发所拥有的顶点数）

                // 测试读取头发数量是否正确 yes
                // LogUtil.d(TAG, "uNumVertices of " + i + " = " + uNumVertices);

                Vector3[] pTemp = new Vector3[uNumVertices];
                if (uNumVertices < 2) {
                    // Ignore hair strands with less than 2 vertices
                    for (int j = 0; j < uNumVertices; j++) {
                        br.readLine();
                    }
                    continue;
                }
                if (uNumVertices - 1 > u_MaxSegs) {
                    u_MaxSegs = uNumVertices - 1;
                }
                // Increase buffer if necessary
                if (uNumVertices > uTempSize) {
                    //pTemp = new Vector.Vector3[0];
                    pTemp = new Vector3[uNumVertices];
                    uTempSize = uNumVertices;
                }
                // Read vertices
                for (int j = 0; j < uNumVertices; j++) {
                    str = br.readLine();
                    float tempX = new Float(str);
                    str = br.readLine();
                    float tempY = new Float(str);
                    str = br.readLine();
                    float tempZ = new Float(str);

                    // 测试数据是否读到 tempX Y Z 中 Yes
                    // LogUtil.v(TAG, " " + tempX + " " + tempY + " " + tempZ);

                    pTemp[j] = new Vector3(tempX, tempY, tempZ);

                    // 如下
                    // LogUtil.v(TAG, "pTemp " + j + ": " + pTemp[j].getX() + " " + pTemp[j].getY() + " " + pTemp[j].getZ());
                }

                // 测试数据是否存到pTemp中  Yes
                /*for (int k = 0; k < uNumVertices; k++) {
                    LogUtil.v(TAG, "pTemp " + k + ": " + pTemp[k].getX() + " " + pTemp[k].getY() + " " + pTemp[k].getZ());
                }*/

                HairStrand tempStrand = new HairStrand(uNumVertices, pTemp);

                // Create hair strand
                if (tempStrand == null) {
                    LogUtil.e(TAG, "Failed to create hair " + uNumCurrent + ",i = " + i);
                    return false;
                }
                pStrands[uNumCurrent] = tempStrand;
                uNumCurrent++;
                uNumTotalVerts += uNumVertices;
            }


            // 测试数据是否存到pStrands中 yes
            /*for (int i = 0; i < uNumStrands; i++) {
                for (int j = 0; j < pStrands[i].m_uNumVertices; j++) {
                    LogUtil.v(TAG, "pStrands " + i + ":" + "Vertices :" + j + " :"
                            + pStrands[i].m_pVertices[j].position.getX() + " "
                            + pStrands[i].m_pVertices[j].position.getY() + " "
                            + pStrands[i].m_pVertices[j].position.getZ()
                    );
                }
            }*/

            br.close();
            LogUtil.v(TAG, "Strands in file: " + uNumStrands);
            LogUtil.v(TAG, "Strands loaded: " + uNumCurrent);
            LogUtil.v(TAG, "Num. Vertices: " + uNumTotalVerts);
            LogUtil.v(TAG, "Max. segments: " + u_MaxSegs);

            // Return values and clean-up
            if (uNumCurrent < uNumStrands) {
                m_pStrands = new HairStrand[uNumCurrent];
                //m_pStrands = new ArrayList<>(uNumCurrent);
                for (int i = 0; i < uNumCurrent; i++) {
                    m_pStrands[i].Create(pStrands[i]);
                    //pStrands.clear();
                    //pStrands = new HairStrand[0];
                }
            } else {
                m_pStrands = pStrands;
            }
            m_uNumStrands = uNumCurrent;
            m_uNumTotalVerts = uNumTotalVerts;
            // pTemp.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    //------------------------------------------------------------------
    // Functions for animation
    //------------------------------------------------------------------
    // Set the rand value of all strands to 0
    public void  ClearStrandRand() {
        for (int i = 0; i < m_uNumStrands; i++) {
            BUFFER_VERTEX[] pVertices = m_pStrands[i].GetVertices();
            //List<BUFFER_VERTEX> pVertices = m_pStrands[i].GetVertices();
            for (int j = 0; j < m_pStrands[i].GetNumVertices(); j++) {
                pVertices[j].reserved.setY(0.0f);
            }
        }
    }

    //----------------------------------------------------------------------
    // Set the rand value of a strand.
    //----------------------------------------------------------------------
    public void SetStrandRand(final int uSid, final float fRand) {
        if (uSid >= m_uNumStrands) {
            Log.d(TAG, "SetStrandRand: Invalid SID!");
            return;
        }
        BUFFER_VERTEX[] pVertices = m_pStrands[uSid].GetVertices();
        for (int i = 0; i < m_pStrands[uSid].GetNumVertices(); i++) {
            pVertices[i].reserved.setY(fRand);
        }
    }

    /*public:

    //------------------------------------------------------------------
    // Load from file.
    //------------------------------------------------------------------
    bool LoadFromSHD(const WCHAR* strFilename);
    bool LoadFromLV2(const WCHAR* strFilename);
    bool LoadAndOverrideFromLV2(const WCHAR* strFilename, const float fNewRand);
*/


}
