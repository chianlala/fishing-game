using System.Collections;
using System.Collections.Generic;
using UnityEditor;
using UnityEngine;

public class RankInName : Editor
{
    //GameObject 会在视窗内显示
    //Assets 资源内显示

    [MenuItem("Window/按数字大小排序", false, 10)]
    public static void tmpRankInName()
    { 
        GameObject[] objs = Selection.gameObjects;

        List<GameObject> all = new List<GameObject>();
        for (int i = 0; i < objs.Length; i++)
        {
            all.Add(objs[i]);
        }
        all.Sort((a, b) =>
        {
            return (int.Parse(a.transform.name)).CompareTo(int.Parse(b.transform.name));
        });
        for (int i = 0; i < all.Count; i++)
        {
            all[i].transform.SetSiblingIndex(i);
        }
        AssetDatabase.Refresh();
    }
}
