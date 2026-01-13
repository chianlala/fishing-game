
//删除所有Miss的脚本

using UnityEngine;
using UnityEditor;

public class DeleteMissingScripts
{
    [MenuItem("MyTools/Cleanup Missing Scripts")]
    static void CleanupMissingScripts()
    {
        for (int i = 0; i < Selection.gameObjects.Length; i++)
        {
            var gameObject = Selection.gameObjects[i];
            Transform[] components = gameObject.GetComponentsInChildren<Transform>(true);
            for (int j = 0; j < components.Length; j++)
            {
                GameObjectUtility.RemoveMonoBehavioursWithMissingScript(components[j].gameObject);// prop.Re();
            }
        }
    }
}