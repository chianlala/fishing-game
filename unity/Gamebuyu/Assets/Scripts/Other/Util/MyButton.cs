using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.EventSystems;
#if UNITY_EDITOR
using UnityEditor;
#endif
public class MyButton : Button //,IPointerDownHandler, IPointerUpHandler
{
//#if UNITY_EDITOR
//    [MenuItem("GameObject/UI/MyButton")]
//    static void MenuAddChild()
//    {
//        Transform[] transforms = Selection.GetTransforms(SelectionMode.TopLevel | SelectionMode.OnlyUserModifiable);

//        foreach (Transform transform in transforms)
//        {
//            GameObject newChild = new GameObject("MyButton");
//            newChild.transform.SetParent(transform, false);
//            MyButton my = newChild.AddComponent<MyButton>();
//            Image img = newChild.AddComponent<Image>();
//            img.rectTransform.sizeDelta = my.GetComponent<RectTransform>().sizeDelta = new Vector2(160, 30);
//        }
//    }
//#endif
    private Vector2 m_ptInit;

    override protected void Awake()
    {
        base.Awake();
        m_ptInit = this.transform.GetComponent<RectTransform>().anchoredPosition;
    }

    override public void OnPointerDown(PointerEventData eventData)
    {
        base.OnPointerDown(eventData);
        this.transform.GetComponent<RectTransform>().anchoredPosition = m_ptInit + new Vector2(2, -2);
    }

    override public void OnPointerUp(PointerEventData eventData)
    {
        base.OnPointerUp(eventData);
        this.transform.GetComponent<RectTransform>().anchoredPosition = m_ptInit;
    }

}
