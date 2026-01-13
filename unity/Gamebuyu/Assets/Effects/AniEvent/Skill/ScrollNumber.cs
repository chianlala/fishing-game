using DG.Tweening;
using System.Collections;
using System.Collections.Generic;
using System.Reflection;
using UnityEngine;

public class ScrollNumber : MonoBehaviour
{
    public  Transform[] ScrollChilds;  

    public int nStIndex;
    public float fSpeed= 80f;

    public int LastTarget = 5;
    void OnEnable()
    { 
        StartCoroutine(MoveAll());
    }
    IEnumerator MoveAll()
    {
        for (int i = 0; i < 20; i++)
        {
            yield return new WaitForSeconds(0.2f);
            MoveDownOne();
        }
        //最后五个
        for (int i = 0; i < 5; i++)
        {
            yield return new WaitForSeconds(0.2f);
            if (i==2)
            {
                //显示最终倍数
                MoveDownLast(i, LastTarget);
            }
            else
            {
                MoveDownLast(i);
            }
        }
    }
    void MoveDownOne() {
        int n = nStIndex;
        SetParticleNumber(ScrollChilds[n]);
        ScrollChilds[n].localPosition = new Vector3(0, 70, 0);
        ScrollChilds[n].gameObject.SetActive(true);
        ScrollChilds[n].DOLocalMoveY(-70, fSpeed).SetEase(Ease.Flash).SetSpeedBased(true).OnComplete(()=> {
           ScrollChilds[n].gameObject.SetActive(false);
        });
        nStIndex++;
        if (nStIndex>= ScrollChilds.Length)
        {
            nStIndex = 0;
        }
    }
    void SetParticleNumber(Transform TransGo,int num=-1) {
        if (num==-1)
        {
            num = UnityEngine.Random.Range(2, 10);
        }
        //-------设置值----------
        num = num - 2;
        float tmp = num/10f;
        ParticleSystem part = TransGo.Find("light").GetComponent<ParticleSystem>();
        System.Type type = part.textureSheetAnimation.GetType();
        PropertyInfo property = type.GetProperty("startFrameMultiplier");
        property.SetValue(part.textureSheetAnimation, tmp, null);
    }
    void MoveDownLast(int nIdexpos,  int num = -1)
    {
        int n = nStIndex;
        //更改显示
        Transform TransGo = ScrollChilds[nStIndex];//  TransGo
        if (num == -1)
        {
            num = UnityEngine.Random.Range(2, 10);
        }
        num = num - 2+1;
        float tmp = num / 10f;
        ParticleSystem part = TransGo.Find("light").GetComponent<ParticleSystem>();
        System.Type type = part.textureSheetAnimation.GetType();
        PropertyInfo property = type.GetProperty("startFrameMultiplier");
        property.SetValue(part.textureSheetAnimation, tmp, null);

   
        ScrollChilds[n].localPosition = new Vector3(0, 70, 0);
        if (ScrollChilds[n].gameObject.activeSelf == false)
        {
            ScrollChilds[n].gameObject.SetActive(true);
        }

        if (nIdexpos == 0)
        {
            ScrollChilds[n].DOLocalMoveY(-60, fSpeed).SetEase(Ease.Flash).SetSpeedBased(true).OnComplete(() => {
            });
        }
        if (nIdexpos == 1)
        {
            ScrollChilds[n].DOLocalMoveY(-30, fSpeed).SetEase(Ease.Flash).SetSpeedBased(true).OnComplete(() => {
            });
        }
        if (nIdexpos == 2)
        {
            ScrollChilds[n].DOLocalMoveY(0, fSpeed).SetEase(Ease.Flash).SetSpeedBased(true).OnComplete(() => {

                if (nIdexpos == 2)//代表为中间一个
                {
                    ParticleSystem part2 = TransGo.Find("number2").GetComponent<ParticleSystem>();
                    System.Type type2 = part2.textureSheetAnimation.GetType();
                    PropertyInfo property2 = type2.GetProperty("startFrameMultiplier");
                    property2.SetValue(part2.textureSheetAnimation, tmp, null);
                    TransGo.Find("number2").gameObject.SetActive(true);
                }
            });
        }
        if (nIdexpos == 3)
        {
            ScrollChilds[n].DOLocalMoveY(30, fSpeed).SetEase(Ease.Flash).SetSpeedBased(true).OnComplete(() => {
            });
        }
        if (nIdexpos == 4)
        {
            ScrollChilds[n].DOLocalMoveY(60, fSpeed).SetEase(Ease.Flash).SetSpeedBased(true).OnComplete(() => {
            });
        }
        nStIndex++;
        if (nStIndex >= ScrollChilds.Length)
        {
            nStIndex = 0;
        }
    }
    private void OnDisable()
    {
        for (int i = 0; i < ScrollChilds.Length; i++)
        {
            ScrollChilds[i].transform.position = new Vector3(0,100,0);
        }
    }
}
