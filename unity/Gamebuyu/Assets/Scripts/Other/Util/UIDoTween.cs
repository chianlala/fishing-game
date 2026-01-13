using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using DG.Tweening;
using UnityEngine.UI;

public class UIDoTween : MonoBehaviour
{
    private Vector2 m_ptStart;
    public Vector2 m_ptEnd = Vector2.zero;

    public float m_fDuration = 0.5f;

    private float m_fScaleStart;
    public float m_fScaleEnd = 1;
    public bool m_bChangeScale = true;

    private float m_fAlphaStart = 1;
    public float m_fAlphaEnd = 1;

    private Vector3 m_ptRotateStart;
    public Vector3 m_ptRotateEnd = Vector3.zero;

    public float m_fDelay = 0;

    public Ease m_ease = Ease.OutCirc;

    void Awake()
    {
        m_ptStart = this.transform.GetComponent<RectTransform>().anchoredPosition;
        m_fScaleStart = this.transform.localScale.x;
        m_ptRotateStart = this.transform.localEulerAngles;
        if (this.transform.GetComponent<Image>() != null)
            m_fAlphaStart = this.transform.GetComponent<Image>().color.a;
        if (m_ptEnd == Vector2.zero)
        {
            m_ptEnd = m_ptStart;
        }
        if (m_ptRotateEnd == Vector3.zero)
        {
            m_ptRotateEnd = m_ptRotateStart;
        }
    }

    // Use this for initialization
    void Start()
    {
        if (m_bChangeScale)
        {
            this.transform.localScale = new Vector3(m_fScaleStart, m_fScaleStart, m_fScaleStart);
            this.transform.DOScale(m_fScaleEnd, m_fDuration).SetEase(m_ease).SetDelay(m_fDelay).SetLoops(-1, LoopType.Yoyo);
        }
        if (m_fAlphaStart != m_fAlphaEnd)
        {
            if (this.transform.GetComponent<Image>() != null)
            {
                Image img = this.transform.GetComponent<Image>();
                img.color = new Color(1, 1, 1, m_fAlphaStart);
                img.DOColor(new Color(1, 1, 1, m_fAlphaEnd), m_fDuration).SetEase(m_ease).SetDelay(m_fDelay).SetLoops(-1, LoopType.Yoyo);
            }
        }
        if (m_ptStart != m_ptEnd)
        {
            this.transform.GetComponent<RectTransform>().anchoredPosition = m_ptStart;
            this.transform.GetComponent<RectTransform>().DOAnchorPos(m_ptEnd, m_fDuration).SetEase(m_ease).SetDelay(m_fDelay).SetLoops(-1, LoopType.Yoyo);
        }
        if (m_ptRotateStart != m_ptRotateEnd)
        {
            this.transform.localEulerAngles = m_ptRotateStart;
            this.transform.DOLocalRotate(m_ptRotateEnd, m_fDuration).SetEase(m_ease).SetDelay(m_fDelay).SetLoops(-1, LoopType.Yoyo);
        }
    }

}
