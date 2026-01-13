using Spine.Unity;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class OperateSkeletonGraphic : MonoBehaviour
{
    public SkeletonGraphic Graphic;
    private void Awake()
    {
        Graphic = this.GetComponent<SkeletonGraphic>();
    }
    public void SetAniName(string strname) {

        Graphic.AnimationState.SetAnimation(0, strname, true);
    }
}
