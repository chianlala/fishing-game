using UnityEngine;
using System.Collections;
using System;

//委托的简单封装
public class UEventListener
{
    public UEventDispatcher.ContextDelegate callback;

    public void Excute(UEventContext context)
    {
        if (callback == null)
            return;

        //if (context.Sender == null)
        //    return;

        if (callback != null)
        {
            this.callback(context);
        }
    }
}
