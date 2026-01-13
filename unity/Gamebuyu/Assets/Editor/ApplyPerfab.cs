using UnityEngine;

using System.Collections;

using UnityEditor;

public class ApplyPrefabEditor : Editor {

    [MenuItem("Charles/Batch Apply Prefab")]

    public static void BatchApplyPrefab()

    {

        GameObject[] objs = Selection.gameObjects;

        if (null == objs || objs.Length < 1)

        {

            Debug.LogError("没有选中prefab");

            return;

        }

        for (int i = 0; i < objs.Length; i++)

        {

            ApplyPrefab(objs[i]);

        }

    }

    public static void ApplyPrefab(GameObject obj)

    {

        if (null == obj)

        {

            Debug.LogError("选中的obj 是 null");

            return;

        }

        PrefabType type = EditorUtility.GetPrefabType(obj);

        if (type != PrefabType.PrefabInstance)

        {

            Debug.LogError("选中的obj " + obj.name + "  不是 PrefabInstance ");

            return;

        }

        //这里必须获取到prefab实例的根节点，否则ReplacePrefab保存不了

        GameObject prefabObj = GetPrefabInstanceParent(obj);

        UnityEngine.Object prefabAsset = null;

        if (prefabObj != null)

        {

            prefabAsset = PrefabUtility.GetPrefabParent(prefabObj);

            if (prefabAsset != null)

            {

                PrefabUtility.ReplacePrefab(prefabObj, prefabAsset, ReplacePrefabOptions.ConnectToPrefab);

                Debug.Log("PrefabInstance ：" + prefabObj.name + "  Apply 成功");

            }

        }

        AssetDatabase.SaveAssets();

    }

    //遍历获取prefab节点所在的根prefab节点

    static GameObject GetPrefabInstanceParent(GameObject obj)

    {

        if (obj == null)

        {

            return null;

        }

        PrefabType pType = EditorUtility.GetPrefabType(obj);

        if (pType != PrefabType.PrefabInstance)

        {

            return null;

        }

        if (obj.transform.parent == null)

        {

            return obj;

        }

        pType = EditorUtility.GetPrefabType(obj.transform.parent.gameObject);

        if (pType != PrefabType.PrefabInstance)

        {

            return obj;

        }

        return GetPrefabInstanceParent(obj.transform.parent.gameObject);

    }


    [MenuItem("Charles/Move Prefab")]

    public static void MovePrefab() 
    {

        GameObject[] objs = Selection.gameObjects;
        if (null == objs || objs.Length < 1)
        {
            Debug.LogError("没有选中prefab");
            return;
        }
        //int mv = 0;
        //int mv = 42;
        //for (int i = 0; i < objs.Length; i++)
        //{
        //    objs[i].transform.position = new Vector3(int.Parse(objs[i].name) * 25, 0f, 0f);
        //}
        int mv = 42;
        for (int i = 0; i < objs.Length; i++)
        {
            objs[i].transform.position = new Vector3(i * 1280, 0f, 0f);
        }
    }
}
