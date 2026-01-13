using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BmController : MonoBehaviour
{
    public static BmController instance;
    public Transform traLightBlackMask;
     
    public Transform BlackMask;
    public MeshRenderer MeshR;
    public Animator animator; 
    private void Awake()
    { 
        instance = this;
        animator = this.GetComponent<Animator>();
    }
    public void CloseZheZhao() {
        animator.gameObject.SetActive(false);
        animator.enabled = false;
        //关闭黑暗遮罩
        traLightBlackMask.gameObject.SetActive(false);
    }
    //private void OnEnable()
    //{
    //    SetStage(4f);
    //}

    //出现动画结束后打开遮罩
    public void OpenZheZhao() {
        //打开关闭黑暗遮罩
        traLightBlackMask.gameObject.SetActive(true);
    }
    //开始出场动画 设置时间 在5秒内
    public void SetStage(float ftime)
    {
        //淡入淡出  逻辑 淡黑-> 黑  boos出场->前移   淡黑->黑  boos出场效果消失 可以直接做一个动画
        //animator.gameObject.SetActive(true);
        animator.enabled = true;
        //animator["SytmStageAnimation"].time = ftime;
        animator.Play("sytm",0,ftime/5f);

    }
   
    //更新坐标
    public void SetPoint(Vector2 V2)
    {
        if (traLightBlackMask.gameObject.activeSelf)
        {
            //Debug.Log(V2.x + "y坐标" + V2.y);
            traLightBlackMask.transform.position = V2;
        }
    }
}
