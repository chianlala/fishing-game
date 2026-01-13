using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ChangeLightPoint : MonoBehaviour
{
    void Update()
    {
        //灯笼鱼灯的位置
        BmController.instance.SetPoint(this.transform.position);
    }
}
