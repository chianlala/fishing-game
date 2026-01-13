using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System;
using JEngine.Core;

/// <summary>
/// 事件管理器，提供增加、移除事件监听、分发事件的功能
/// </summary>
public class UEventDispatcher : Singleton<UEventDispatcher>
{
    public delegate void ContextDelegate(UEventContext context);

    Dictionary<string, UEventListener> _dict = new Dictionary<string, UEventListener>();

    /// <summary>
    /// 增加事件回调 且多个地方注册 只能执行一个
    /// </summary>
    /// <param name="eventName">事件名字</param>
    /// <param name="callback">事件回调</param>
    public void AddEventListener(string eventName, ContextDelegate callback)
    {
        if (!_dict.ContainsKey(eventName))
        {
            _dict.Add(eventName, new UEventListener());
        }
        _dict[eventName].callback += callback;
    }
    /// <summary>
    /// 移除事件回调
    /// </summary>
    /// <param name="eventName">事件名字</param>
    /// <param name="callback">事件回调</param>
    public void RemoveEventListener(string eventName, ContextDelegate callback)
    {
        if (_dict.ContainsKey(eventName))
        {
            _dict[eventName].callback -= callback;

            //如果callback为空应当移除，否则调用null delgate会报错
            if (_dict[eventName].callback == null)
            {
                _dict.Remove(eventName);
            }
        }
    }
    public void DispatchEvent(UEventContext context)
    {
        if (string.IsNullOrEmpty(context.Name))
        {
            Debug.LogError("UEventContext Name Can not be null or empty.");
            return;
        }

        if (!_dict.ContainsKey(context.Name))
            return;

        UEventListener listener = _dict[context.Name];

        if (listener == null)
            return;

        listener.Excute(context);
    }
    /// <summary>
    /// 分发/触发事件
    /// </summary>
    /// <param name="eventName"></param>
    /// <param name="sender"></param>
    /// <param name="param"></param>
    public void DispatchEvent(string eventName, object sender=null, object param = null)
    {
        DispatchEvent(new UEventContext(eventName, sender, param));
    }
    
}

