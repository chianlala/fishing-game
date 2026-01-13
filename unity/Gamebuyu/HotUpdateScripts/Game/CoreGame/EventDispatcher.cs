using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System;
using JEngine.Core;

public class EventHandler
{
    public UnityEngine.Object Target { get; set; }
    Action<UEventContext> callback;

    public EventHandler(UnityEngine.Object target, Action<UEventContext> handler)
    {
        Target = target;
        callback = handler;
    }
    public void Excute(UEventContext context)
    {
        if (callback != null)
            callback(context);
    }
}

public class EventDispatcher:Singleton<EventDispatcher>
{
    Dictionary<string, List<EventHandler>> _dict = new Dictionary<string, List<EventHandler>>();

    public void AddEventHandler(string name,EventHandler handler)
    {
        if (!_dict.ContainsKey(name))
        {
            List<EventHandler> list = new List<EventHandler>();
            list.Add(handler);
            _dict.Add(name, list);
        }
        else
        {
            _dict[name].Add(handler);
        }
    }
    public void AddEventHandler(string name, UnityEngine.Object target,Action<UEventContext> handler)
    {
        AddEventHandler(name, new EventHandler(target, handler));
    }
    public void TriggerEvent(UEventContext context)
    {
        if (_dict.ContainsKey(context.Name))
        {
            var list = _dict[context.Name];
            var cache = new List<EventHandler>();

            for (int i = 0; i < list.Count; i++)
            {
                Debug.Log(list[i].Target.GetType());
                if (list[i].Target == null)
                {
                    cache.Add(list[i]);
                    continue;
                }
                else
                {
                    list[i].Excute(context);
                }
            }

            for (int i = 0; i < cache.Count; i++)
            {
                list.Remove(cache[i]);

                if (list.Count < 1)
                {
                    _dict.Remove(context.Name);
                }
            }
        }
    }
    public void TriggerEvent(string eventName, object sender=null, object param = null)
    {
        TriggerEvent(new UEventContext(eventName, sender, param));
    }
}
