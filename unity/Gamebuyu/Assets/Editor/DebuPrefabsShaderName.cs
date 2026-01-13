using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Text.RegularExpressions;
using UnityEditor;
using UnityEngine;

public class DebuPrefabsShaderName
{

    [MenuItem("Assets/打印该资源Shader同名路径", false, 10)]
    private static void ShaderLUjin()
    {
        //路径
        //string path = AssetDatabase.GetAssetPath(Selection.activeObject);
        //string guid = AssetDatabase.AssetPathToGUID(path);
        //Debug.Log(path);
        //Debug.Log(guid); 

        var Go = Selection.gameObjects;
        for (int i = 0; i < Go.Length; i++)
        {
            try
            {
                Material[] Render = Go[i].GetComponentsInChildren<Material>();
                for (int mx = 0; mx < Render.Length; mx++)
                {
                    if (Render[mx].shader.name.Contains("_not"))
                    {
                        Debug.Log(Render[mx].name);
                        Debug.Log(Render[mx].shader.name);
                    }
                }
            }
            catch 
            {
             
            }
        }

    }

}
