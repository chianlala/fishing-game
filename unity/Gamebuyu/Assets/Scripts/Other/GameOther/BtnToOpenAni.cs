using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Scripting;
using UnityEngine.UI;
[Preserve]
public class BtnToOpenAni : MonoBehaviour
{
    [Header("勾选为true时必须填两个动画")]
    public bool isToggle;
    [Header("是否动画播完才能再次点击")]
    public bool isCickWaitAniOver = true; 

    public string nameStart; 
    public string nameOver;

    public Animator myAnimator;
    public Button myButton;
    int m = 0;
    void Start()
    {
        myButton = this.GetComponent<Button>();
        myButton.onClick.AddListener(() =>
        {
            if (isToggle==true)
            {
           
                if (m == 0) {
                    //第一次点击
                    if (isCickWaitAniOver)
                    {
                        var animatorInfo = myAnimator.GetCurrentAnimatorStateInfo(0);
                        if ((animatorInfo.normalizedTime >= 1.0f))//normalizedTime: 范围0 -- 1,  0是动作开始，1是动作结束
                        {
                            myAnimator.Play(nameStart);
                        }
                        else
                        {
                            return;
                        }
                    }
                    else
                    {
                        myAnimator.Play(nameStart);
                    }
                }
                else
                {
                    //第二次次点击
                    if (isCickWaitAniOver)
                    {
                        var animatorInfo = myAnimator.GetCurrentAnimatorStateInfo(0);
                        if ((animatorInfo.normalizedTime >= 1.0f))//normalizedTime: 范围0 -- 1,  0是动作开始，1是动作结束
                        {
                            myAnimator.Play(nameOver);
                        }
                        else
                        {
                            return;
                        }
                    }
                    else
                    {
                        myAnimator.Play(nameOver);
                    }
                }
                //计数增加
                m++;
                if (m >= 2)
                {
                    m = 0;
                }

            }
            else
            {
                myAnimator.Play(nameStart);
            }
        });
    }

    // Update is called once per frame
    void Update()
    {
        
    }
}
