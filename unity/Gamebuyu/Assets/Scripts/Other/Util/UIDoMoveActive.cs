using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using DG.Tweening;
using UnityEngine.UI;
using System;

public class UIDoMoveActive : MonoBehaviour
{
    public Vector2 m_ptOut;
    public float m_fDuration = 0.5f;
    public float m_fScaleOut = 1;
    public float m_fAlphaOut = 1;
    public float m_fDelay = 0;
    public Ease m_ease = Ease.OutCirc;
    private Vector2 m_ptInit;
    void Awake()
    {
        m_ptInit = this.transform.GetComponent<RectTransform>().anchoredPosition;
    }

    void OnEnable()
    {
       // UIDoPool.g_listDoMove.Add(this);
        this.transform.GetComponent<RectTransform>().anchoredPosition = m_ptOut;
        this.transform.GetComponent<RectTransform>().DOAnchorPos(m_ptInit, m_fDuration).SetEase(m_ease).SetDelay(m_fDelay);
        if (m_fScaleOut != 1)
        {
            this.transform.localScale = new Vector3(m_fScaleOut, m_fScaleOut, m_fScaleOut);
            this.transform.DOScale(1, m_fDuration).SetEase(m_ease).SetDelay(m_fDelay);
        }
        if (m_fAlphaOut != 1)
        {
            if (this.transform.GetComponent<Image>() != null)
            {
                Image img = this.transform.GetComponent<Image>();
                img.color = new Color(1, 1, 1, m_fAlphaOut);
                img.DOColor(new Color(1, 1, 1, 1), m_fDuration);
            }
        }
    }

    //public void BeforeDisable()
    //{
    //    this.transform.DOLocalMove(m_ptOut, m_fDuration).SetEase(m_ease).OnComplete(() =>
    //    {
    //        if (UIDoPool.g_listDoMove.Contains(this))
    //            UIDoPool.g_listDoMove.Remove(this);
    //    });
    //    if (m_fScaleOut != 1)
    //    {
    //        this.transform.DOScale(m_fScaleOut, m_fDuration).SetEase(m_ease);
    //    }
    //    if (m_fAlphaOut != 1)
    //    {
    //        if (this.transform.GetComponent<Image>() != null)
    //        {
    //            Image img = this.transform.GetComponent<Image>();
    //            img.DOColor(new Color(1, 1, 1, m_fAlphaOut), m_fDuration);
    //        }
    //    }
    //}
}
