using UnityEngine;
using System.Collections;

public class MonoSingletion<T> : MonoBehaviour where T : MonoBehaviour
{
    private static T _instance;

    public static T Instance
    {
        get
        {
            if (_instance == null)
                _instance = FindObjectOfType<T>();

            return _instance;
        }
    }

    protected virtual void Awake()
    {
        _instance = this as T;
    }
}