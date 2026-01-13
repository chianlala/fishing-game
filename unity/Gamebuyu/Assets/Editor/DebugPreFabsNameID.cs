using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Text.RegularExpressions;
using UnityEditor;
using UnityEngine;

public class DebugPreFabsNameID
{
    //GUID,FileID,LocalID    
    //guid是meta中最最最重要的数据。这个guid代表了这个文件，无论这个文件是什么类型（甚至是文件夹）。换句话说，通过GUID就可
    //以找到工程中的这个文件，无论它在项目的什么位置。在编辑器中使用AssetDatabase.GUIDToAssetPath和AssetDatabase.AssetPathToGUID进行互转。
    //所以在每次svn提交时如果发现有meta文件变更，一定要打开看一下。看看这个guid是否被更改。理论上是不需要更改的。


    [MenuItem("Assets/打印该资源预设引用位置", false, 10)]
    private static void FindAss()
    {
        //路径
        //string path = AssetDatabase.GetAssetPath(Selection.activeObject);
        //string guid = AssetDatabase.AssetPathToGUID(path);
        //Debug.Log(path);
        //Debug.Log(guid); 

       var Go = Selection.gameObjects;
        for (int i = 0; i < Go.Length; i++)
        {
            var mp = Go[i].GetComponentsInChildren<Transform>(true);
            foreach (Transform item in mp)
            {
                string m1;
                long m2;
                if (AssetDatabase.TryGetGUIDAndLocalFileIdentifier(item.gameObject,out m1, out m2))
                {
                    Debug.Log(item.name+ m1);
                    Debug.Log(item.name + m2);
                }
            
                //Debug.Log(AssetDatabase.TryGetGUIDAndLocalFileIdentifier(item.gameObject));
            }
  
        }

    }

}
