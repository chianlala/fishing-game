using UnityEngine;
using System.Collections;

/// <summary>
/// 方便调试Camera的视距框
/// </summary>
[ExecuteInEditMode]//在非运行模式下也会执行，改脚本是更改画布的拉伸
public class CameraViewEx : MonoBehaviour
{
    private static Camera theCameraNew;
    private Camera theCamera;

    //距离摄像机8.5米 用黄色表示
    private float upperDistance = 8.5f;
    //距离摄像机12米 用红色表示
    private float lowerDistance = 12.0f;

    private Transform tx;

    /// <summary>
    /// 是否显示轨迹有没有出界
    /// </summary>
    public static bool isShowLine = false;

    void Start()
    {

        if (!theCamera)
        {
            theCamera = this.GetComponent<Camera>();
            theCameraNew = this.GetComponent<Camera>();
        }
        
        upperDistance = theCamera.farClipPlane;
        lowerDistance = theCamera.nearClipPlane;
        tx = theCamera.transform;
    }


    void Update()
    {
       
        //for (int i = 1; i < theCamera.farClipPlane; i++)
        //{
            //upperDistance = i;
            FindUpperCorners();
            FindLowerCorners();

            FindLower2UpperCorners();
        //}
        
    }


    void FindUpperCorners()
    {
        Vector3[] corners = GetCorners(upperDistance);

        // for debugging
        Debug.DrawLine(corners[0], corners[1], Color.black); // UpperLeft -> UpperRight
        Debug.DrawLine(corners[1], corners[3], Color.black); // UpperRight -> LowerRight
        Debug.DrawLine(corners[3], corners[2], Color.black); // LowerRight -> LowerLeft
        Debug.DrawLine(corners[2], corners[0], Color.black); // LowerLeft -> UpperLeft
    }


    void FindLowerCorners()
    {
        Vector3[] corners = GetCorners(lowerDistance);

        // for debugging
        Debug.DrawLine(corners[0], corners[1], Color.red);
        Debug.DrawLine(corners[1], corners[3], Color.red);
        Debug.DrawLine(corners[3], corners[2], Color.red);
        Debug.DrawLine(corners[2], corners[0], Color.red);
    }

    void FindLower2UpperCorners()
    {
        Vector3[] corners_upper = GetCorners(upperDistance);
        Vector3[] corners_lower = GetCorners(lowerDistance);

        Debug.DrawLine(corners_lower[0], corners_upper[0], Color.blue);
        Debug.DrawLine(corners_lower[1], corners_upper[1], Color.blue);
        Debug.DrawLine(corners_lower[2], corners_upper[2], Color.blue);
        Debug.DrawLine(corners_lower[3], corners_upper[3], Color.blue);
    }


    Vector3[] GetCorners(float distance)
    {
        Vector3[] corners = new Vector3[4];

        float halfFOV = (theCamera.fieldOfView * 0.5f) * Mathf.Deg2Rad;
        float aspect = theCamera.aspect;

        float height = distance * Mathf.Tan(halfFOV);
        float width = height * aspect;

        // UpperLeft
        corners[0] = tx.position - (tx.right * width);
        corners[0] += tx.up * height;
        corners[0] += tx.forward * distance;

        // UpperRight
        corners[1] = tx.position + (tx.right * width);
        corners[1] += tx.up * height;
        corners[1] += tx.forward * distance;

        // LowerLeft
        corners[2] = tx.position - (tx.right * width);
        corners[2] -= tx.up * height;
        corners[2] += tx.forward * distance;

        // LowerRight
        corners[3] = tx.position + (tx.right * width);
        corners[3] -= tx.up * height;
        corners[3] += tx.forward * distance;

        return corners;
    }


    //计算摄像机的视景并返回它的六个面
    static Plane[] GetFrustumPlanes()
    {
        return GeometryUtility.CalculateFrustumPlanes(theCameraNew);
    }

    public static bool IsPointInFrustum(Vector3 point)
    {
        Plane[] planes = GetFrustumPlanes();

        for (int i = 0, iMax = planes.Length; i < iMax; ++i)
        {
            //判断一个点是否在平面的正方向上
            if (!planes[i].GetSide(point))
                return false;
        }

        return true;
    }
}