using UnityEngine;
using System.Collections;

/// <summary>
/// 事件内容
/// </summary>
public class UEventContext
{
    /// <summary>
    /// 事件名
    /// </summary>
    public string Name { get; set; }
    /// <summary>
    /// 事件发出者
    /// </summary>
    public object Sender { get; set; }
    /// <summary>
    /// 参数
    /// </summary>
    public object Param { get; set; }

    public UEventContext(string name,object sender,object param)
    {
        Name = name;
        Sender = sender;
        Param = param;
    }
    public T GetData<T>()
    {
        return (T)Param;
    }
}