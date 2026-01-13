using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Scripting;
using UnityEngine.UI;
[Preserve]
public class BossTips : MonoBehaviour {

    public Image fishName;
    //public Text name;
    //public Text bei1;
    public Text bei2;

    public void InitBoosTips(string varname,int index,int varbei1,int varbei2)
    {
        //name.text = varname.ToString();
        fishName.sprite = Resources.Load<Sprite>("fishBoosName/" + index);
        fishName.SetNativeSize();

        //bei1.text = varbei1.ToString();
        bei2.text = varbei2.ToString();


    }
}
 