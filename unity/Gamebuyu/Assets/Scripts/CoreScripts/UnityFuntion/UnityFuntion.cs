using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Scripting;

[Preserve]
public static class UnityFuntion
{
    public static Vector2 GetClickPos()
    {
        Vector2 v2 = Input.mousePosition;
        Debug.Log(v2.x + "unity角2度" + v2.y);
        return v2;
    }
}
