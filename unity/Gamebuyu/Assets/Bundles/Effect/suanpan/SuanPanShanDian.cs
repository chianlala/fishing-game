using System.Collections;
using System.Collections.Generic;
using UnityEngine;
//[RequireComponent(typeof(LineRenderer))]
//[ExecuteInEditMode]  //普通的类，加上ExecuteInEditMode， 就可以在编辑器模式中运行
public class SuanPanShanDian : MonoBehaviour {
    public float detail = 1;//增加后，线条数量会减少，每个线条会更长。  
    public float displacement = 15;//位移量，也就是线条数值方向偏移的最大值  
    public Transform EndPostion;//链接目标  
    public Transform StartPosition;
    public float yOffset = 0;
    private LineRenderer _lineRender;
    private List<Vector3> _linePosList;

    private Material _ThisM; 
    private void Awake()
    {
        _lineRender = GetComponent<LineRenderer>();
        _linePosList = new List<Vector3>();
        _ThisM = _lineRender.material; 
    }

    private void OnEnable()
    {
        _lineRender.SetPosition(0, StartPosition.position);
        _lineRender.SetPosition(1, EndPostion.position);
    }
    //float WeiYI;
    //int m = 0;
    //private void Update()
    //{
    //    WeiYI += Time.deltaTime;
    //    _ThisM.SetTextureOffset("_MainTex", new Vector2(WeiYI, 0));
    //}

    float WeiYI;
    int m = 0;
    private void Update()
    {

        if (m > 3)
        {
            m = 0;

        }
        else
        {
            m++;
            return;
        }

        if (WeiYI > 1)
        {
            WeiYI = 0;
        }
        WeiYI = WeiYI + 0.125f;
        _ThisM.SetTextureOffset("_MainTex", new Vector2(0.5f, WeiYI));
    }
    ////收集顶点，中点分形法插值抖动  
    //private void CollectLinPos(Vector3 startPos, Vector3 destPos, float displace)
    //{
    //    //递归结束的条件
    //    if (displace < detail)
    //    {
    //        _linePosList.Add(startPos);
    //    }
    //    else
    //    {
    //        float midX = (startPos.x + destPos.x) / 2;
    //        float midY = (startPos.y + destPos.y) / 2;
    //        float midZ = (startPos.z + destPos.z) / 2;
    //        midX += (float)(UnityEngine.Random.value - 0.5) * displace;
    //        midY += (float)(UnityEngine.Random.value - 0.5) * displace;
    //        midZ += (float)(UnityEngine.Random.value - 0.5) * displace;
    //        Vector3 midPos = new Vector3(midX, midY, midZ);
    //        //递归获得点
    //        CollectLinPos(startPos, midPos, displace / 2);
    //        CollectLinPos(midPos, destPos, displace / 2);
    //    }
    //}
}
