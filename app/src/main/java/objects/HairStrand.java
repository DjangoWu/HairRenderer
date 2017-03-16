package objects;

import utils.Vector3;

/**
 * Created by Wujiqiang on 2017/3/2.
 */

public class HairStrand {
    public int m_uNumVertices;
    public BUFFER_VERTEX[] m_pVertices;
    //public List<BUFFER_VERTEX> m_pVertices;

    //----------------------------------------------------------------------
    // Constructor for HairStrand.
    //----------------------------------------------------------------------
    public HairStrand(final int uNumVertices, Vector3[] pVertices) {
        Create(uNumVertices, pVertices);
        //m_uNumVertices = 0;
        //m_pVertices = new BUFFER_VERTEX[512];
        //m_pVertices = new ArrayList<>();
    }

    public HairStrand(final HairStrand pRef){
        Create(pRef);
    }

    //----------------------------------------------------------------------
    // Getters
    //----------------------------------------------------------------------
    public int GetNumVertices() {
        return m_uNumVertices;
    }

    /*public List<BUFFER_VERTEX> GetVertices() {
        return m_pVertices;
    }*/

    public BUFFER_VERTEX[] GetVertices() {
        return m_pVertices;
    }

    //------------------------------------------------------
    // Create the strand
    //------------------------------------------------------
    public boolean Create(final int uNumVertices, Vector3[] pVertices) {//List<Vector.Vector3> pVertices
        // Delete old data
        // m_pVertices.clear();
        m_uNumVertices = 0;

        if (uNumVertices < 2 || pVertices == null) {
            return false;
        }

        // Create new
        m_pVertices = new BUFFER_VERTEX[uNumVertices];
        //m_pVertices = new ArrayList<BUFFER_VERTEX>(uNumVertices);
        m_uNumVertices = uNumVertices;

        // 作用于reserved，具体作用暂不明确（暂时看来是用于纹理坐标）
        float fRand = (float) Math.random(); // 返回 0-1 之间的浮点数
        for (int i = 0; i < uNumVertices; i++) {
            BUFFER_VERTEX temp_Vertex = new BUFFER_VERTEX();
            temp_Vertex.position = pVertices[i];
            temp_Vertex.reserved.setX((float)i / (float)(uNumVertices - 1));
            temp_Vertex.reserved.setY(fRand);
            m_pVertices[i] = temp_Vertex;
            //m_pVertices.add(temp_Vertex);
        }

        //  Compute tangent
        return ComputeTangents();
    }

    //----------------------------------------------------------------------
    // Create a duplicate of a given strand.
    //----------------------------------------------------------------------
    public boolean Create(final HairStrand pRef) {
        //m_pVertices.clear();
        m_uNumVertices = 0;

        if (pRef.m_pVertices != null && pRef.m_uNumVertices > 0) {
            m_uNumVertices = pRef.m_uNumVertices;

            //m_pVertices = new ArrayList<BUFFER_VERTEX>(m_uNumVertices);
            //m_pVertices.addAll(pRef.m_pVertices);
            m_pVertices = new BUFFER_VERTEX[m_uNumVertices];
            for (int i = 0; i < m_uNumVertices; i++) {
                m_pVertices[i] = pRef.m_pVertices[i];
            }
            return true;
        }
        return false;
    }

    //----------------------------------------------------------------------
    // Compute per-vertex tangent.
    //----------------------------------------------------------------------
    public boolean ComputeTangents() {
        if (m_uNumVertices < 2 || m_pVertices == null) {
            return false;
        }

        // Root tangent
        Vector3 vDiff = m_pVertices[1].position.minus(m_pVertices[0].position);
        m_pVertices[0].tangent = vDiff.Normalzied();

        // Middle tangents
        final int uLast = m_uNumVertices -1;
        for (int i = 1; i < uLast; i++) {
            vDiff = m_pVertices[i+1].position.minus(m_pVertices[i-1].position);
            m_pVertices[i].tangent = vDiff.Normalzied();
        }

        // Tip tangent
        vDiff = m_pVertices[uLast].position.minus(m_pVertices[uLast-1].position);
        m_pVertices[uLast].tangent = vDiff.Normalzied();

        return true;
    }
}
