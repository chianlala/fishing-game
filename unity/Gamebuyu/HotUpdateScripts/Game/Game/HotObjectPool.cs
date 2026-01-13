using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using UnityEngine.UI;

//public class HotObjectPool<T> where T : class,new()
//{
//    protected Queue<T> m_queue = null;

//    public HotObjectPool()
//    { 
//        m_queue = new Queue<T>();
//    }
//    public HotObjectPool(int count)
//    {
//        m_queue = new Queue<T>(count);
//    }
//    public virtual void Recycle(T item)
//    {
//        if (!m_queue.Contains(item))
//            m_queue.Enqueue(item);
//    }
//    public virtual T Get()
//    {
//        if (m_queue.Count > 0)
//            return m_queue.Dequeue();
//        else
//            return new T();
//    }
//}
//public class HotObjPool<T> where T : class 
//{ 
//    protected Queue<T> m_queue = null;
//    T templete = null;
//    public HotObjPool() 
//    {
//        m_queue = new Queue<T>();
//    }
//    public HotObjPool(int count)
//    {
//        m_queue = new Queue<T>(count);
//    }
//    public T Templete
//    {
//        get { return templete; }
//        set { templete = value; }
//    }
//    public void SetTemplete(T item)
//    {
//        templete = item;
//    }
//    public virtual void Recycle(T item)
//    {
//        if (!m_queue.Contains(item))
//            m_queue.Enqueue(item);        
//    }
//    public virtual T Get()
//    {
//        if (m_queue.Count > 0)
//            return m_queue.Dequeue();
//        return null;
//    }
//}
public class HotGameObjectPool
{
    protected Queue<GameObject> m_queue = null;

    public HotGameObjectPool()
    {
        m_queue = new Queue<GameObject>();
    }
    public  void baseRecycle(GameObject item)
    { 
        if (!m_queue.Contains(item))
            m_queue.Enqueue(item);
    }
    GameObject templete = null;
    public bool IsTemplete()
    {
        if (templete == null)
        {
            return false;
        }
        return true;
    }
    public void SetTemplete(GameObject item)
    {
        templete = item;
    }
    public  void Recycle(GameObject item)
    {        
        if (item!=null)
        {
            baseRecycle(item);
            
            item.SetActive(false);
        }
        else
        {
           
        }
    }

    public  GameObject Get() 
    {
        if (templete == null)
        {
            Debug.LogError("you have not set a templete or the templete has been destroy.");
            return null;
        }
        else
        {
            if (m_queue.Count > 0)
            {
                while (m_queue.Count > 0)
                {
                    var go = m_queue.Dequeue();
                    if (go != null)
                    {
                        return go;
                    }
                }
                return GameObject.Instantiate(templete);
            }
            else {
                return GameObject.Instantiate(templete);
            }
            
        }
    }
    public GameObject Get(Transform parent)
    { 
        var go = Get();
        go.SetActive(true);
        go.transform.SetParent(parent, false);
        return go;
    }
    public void QueueClear()
    {
        for (int i = m_queue.Count-1; i >=0 ; i--)
        {          
            GameObject go = m_queue.Dequeue();
            if (go!=null)
            {
                go.gameObject.SetActive(false);
                UnityEngine.Object.Destroy(go);
            }          
        }
        m_queue.Clear(); 
    }
   
}

