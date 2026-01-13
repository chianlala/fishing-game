using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class testmove : MonoBehaviour
{
    public Transform DrillDullet;
    public float commonW=640f;
    public float commonH = 360f;
    private void Awake()
    {
        DrillDullet = this.transform;
    }
    void FixedUpdate()
    {
        DrillDullet.transform.Translate(0f, fSpeed, 0f, Space.Self);
        CheckBianjie();
    }
    int nOldDir = 0;
    float fSpeed = 5f;
    void CheckBianjie()
    {
        if (DrillDullet.transform.localPosition.x < 0 || DrillDullet.transform.localPosition.x > commonW * 2 || DrillDullet.transform.localPosition.y < 0 || DrillDullet.transform.localPosition.y > commonH* 2)//该物体的中心点出边界
        {
            int nDir = 0;
            if (DrillDullet.transform.localPosition.x > commonW * 2 && DrillDullet.transform.localPosition.y > commonH* 2)//大于右上角
            {
                nDir = 11;
            }
            else if (DrillDullet.transform.localPosition.x < 0 && DrillDullet.transform.localPosition.y < 0)//左下角
            {
                nDir = 12;
            }
            else if (DrillDullet.transform.localPosition.x > commonW * 2 && DrillDullet.transform.localPosition.y < 0) //右下角
            {
                nDir = 13;
            }
            else if (DrillDullet.transform.localPosition.x < 0 && DrillDullet.transform.localPosition.y > commonH* 2)//左上角
            {
                nDir = 14;
            }
            else if (DrillDullet.transform.localPosition.x > commonW * 2)
            {
                Debug.Log("6位置" + commonW);
                nDir = 6;
            }
            else if (DrillDullet.transform.localPosition.x < 0)
            {
                nDir = 4;
            }

            else if (DrillDullet.transform.localPosition.y > commonH* 2)
            {
                nDir = 8;
            }

            else if (DrillDullet.transform.localPosition.y < 0)
            {
                nDir = 2;
            }



            nOldDir = nDir;
            float fAngle = 0f;
            switch (nDir)
            {
                case 4://左边越界
                    DrillDullet.transform.localPosition = new Vector3(0f, DrillDullet.transform.localPosition.y, DrillDullet.transform.localPosition.z);// =new Vector3(0, DrillDullet.transform.localPosition.y, DrillDullet.transform.localPosition.z);
                    fAngle = -this.transform.localEulerAngles.z;
                    break;
                case 6://右边越界
                    DrillDullet.transform.localPosition = new Vector3(commonW * 2, DrillDullet.transform.localPosition.y, DrillDullet.transform.localPosition.z);
                    fAngle = -this.transform.localEulerAngles.z;
                    break;
                case 8://上边越界
                    DrillDullet.transform.localPosition = new Vector3(DrillDullet.transform.localPosition.x, commonH* 2, DrillDullet.transform.localPosition.z);
                    fAngle = 180f - this.transform.localEulerAngles.z;
                    break;
                case 2://下边越界
                    DrillDullet.transform.localPosition = new Vector3(DrillDullet.transform.localPosition.x, 0f, DrillDullet.transform.localPosition.z);
                    fAngle = 180f - this.transform.localEulerAngles.z;
                    break;
                case 11://右上角越界
                    DrillDullet.transform.localPosition = new Vector3(commonW * 2, commonH* 2, DrillDullet.transform.localPosition.z);
                    fAngle = 180f + this.transform.localEulerAngles.z;
                    break;
                case 12://左下角
                    DrillDullet.transform.localPosition = new Vector3(0f, 0f, DrillDullet.transform.localPosition.z);
                    fAngle = -180f + this.transform.localEulerAngles.z;
                    break;
                case 13://右下角
                    DrillDullet.transform.localPosition = new Vector3(commonW * 2, 0f, DrillDullet.transform.localPosition.z);
                    fAngle = -180f + this.transform.localEulerAngles.z;
                    break;
                case 14://左上角
                    DrillDullet.transform.localPosition = new Vector3(0f, commonH* 2, DrillDullet.transform.localPosition.z);
                    fAngle = 180f + this.transform.localEulerAngles.z;
                    break;
            }
            CheckAngle(ref fAngle);
            DrillDullet.transform.localEulerAngles = new Vector3(this.transform.localEulerAngles.x, this.transform.localEulerAngles.y, fAngle);
        }
        else
        {

        }
    }
    void CheckAngle(ref float angle)
    {
        if (angle > 360)
            angle -= 360;
        else if (angle < 0)
            angle += 360;
    }
}
