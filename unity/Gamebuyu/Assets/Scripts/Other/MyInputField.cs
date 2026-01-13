using System.Collections;
using System.Collections.Generic;

#if UNITY_EDITOR
using UnityEditor;
#endif
using UnityEngine;
using UnityEngine.UI;

public class MyInputField :  InputField{
//#if UNITY_EDITOR
//    [MenuItem("GameObject/UI/MyInputField")] 
//    static void MenuAddChild()
//    {
//        Transform[] transforms = Selection.GetTransforms(SelectionMode.TopLevel | SelectionMode.OnlyUserModifiable);

//        foreach (Transform transform in transforms)
//        {
//            GameObject newChild = new GameObject("MyInputField");
//            newChild.transform.SetParent(transform, false);
//            MyInputField my = newChild.AddComponent<MyInputField>();
//            Image img = newChild.AddComponent<Image>();
//            GameObject p = new GameObject("placeholder");
//            p.transform.SetParent(newChild.transform, false);
//            p.AddComponent<Text>();
//            p.GetComponent<Text>().text = "请输入";
//            my.placeholder = p.GetComponent<Text>();
//            GameObject t = new GameObject("text");
//            t.transform.SetParent(newChild.transform, false);
//            t.AddComponent<Text>();
//            my.textComponent = t.GetComponent<Text>();
//            my.textComponent.color = Color.black;
//            my.placeholder.color = Color.gray;
//            img.rectTransform.sizeDelta = my.GetComponent<RectTransform>().sizeDelta = new Vector2(160, 30);
//            p.GetComponent<RectTransform>().anchorMin = t.GetComponent<RectTransform>().anchorMin = Vector2.zero;
//            p.GetComponent<RectTransform>().anchorMax = t.GetComponent<RectTransform>().anchorMax = Vector2.one;
//            p.GetComponent<RectTransform>().sizeDelta = t.GetComponent<RectTransform>().sizeDelta = Vector2.zero;
//            p.GetComponent<Text>().alignment = t.GetComponent<Text>().alignment = TextAnchor.MiddleLeft;
//            my.textComponent.fontSize = p.GetComponent<Text>().fontSize= 20;
//        }
//    }
//#endif
    private void Update()
    {
        if(text=="")
            placeholder.enabled = !this.isFocused;
    }
}
