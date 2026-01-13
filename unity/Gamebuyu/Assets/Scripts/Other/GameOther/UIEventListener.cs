using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;

/// <summary>
/// Event Hook class lets you easily add remote event listener functions to an object.
/// <para>Example usage: UIEventListener.Get(gameObject).onClick += MyClickFunction;</para>
/// </summary>
public class UIEventListener : MonoBehaviour, IPointerClickHandler,IPointerDownHandler,IPointerUpHandler, IBeginDragHandler, IDragHandler, IEndDragHandler
{
    public delegate void VoidDelegate(GameObject go, PointerEventData data);
    public delegate void BoolDelegate(GameObject go, PointerEventData data, bool state);

    public VoidDelegate onClick;
    public BoolDelegate onPress;
    public VoidDelegate onBeginDrag, onDrag, onEndDrag;
    public object parameter;

    // Use this for initialization
    void Start()
    {

    }

    public static UIEventListener Get(GameObject go)
    {
        UIEventListener listener = go.GetComponent<UIEventListener>();
        if (listener == null)
            listener = go.AddComponent<UIEventListener>();

        return listener;
    }

    public void OnPointerClick(PointerEventData eventData)
    {
        if (onClick != null)
            onClick(gameObject, eventData);
    }

    public void OnPointerDown(PointerEventData eventData)
    {
        if (onPress != null)
            onPress(gameObject, eventData, true);
    }

    public void OnPointerUp(PointerEventData eventData)
    {
        if (onPress != null)
            onPress(gameObject, eventData, false);
    }

    public void OnBeginDrag(PointerEventData eventData)
    {
        if (onBeginDrag != null)
            onBeginDrag(gameObject, eventData);
    }

    public void OnEndDrag(PointerEventData eventData)
    {
        if (onEndDrag != null)
            onEndDrag(gameObject, eventData);
    }

    public void OnDrag(PointerEventData eventData)
    {
        if (onDrag != null)
            onDrag(gameObject, eventData);
    }
}
public class UIEventListenerClick : MonoBehaviour, IPointerClickHandler, IPointerDownHandler, IPointerUpHandler
{
    public delegate void VoidDelegate(GameObject go, PointerEventData data);
    public delegate void BoolDelegate(GameObject go, PointerEventData data, bool state);

    public VoidDelegate onClick;
    public BoolDelegate onPress;

    public object parameter;

    // Use this for initialization
    void Start()
    {

    }

    public static UIEventListenerClick Get(GameObject go)
    {
        UIEventListenerClick listener = go.GetComponent<UIEventListenerClick>();
        if (listener == null)
            listener = go.AddComponent<UIEventListenerClick>();

        return listener;
    }

    public void OnPointerClick(PointerEventData eventData)
    {
        if (onClick != null)
            onClick(gameObject, eventData);
    }

    public void OnPointerDown(PointerEventData eventData)
    {
        if (onPress != null)
            onPress(gameObject, eventData, true);
    }

    public void OnPointerUp(PointerEventData eventData)
    {
        if (onPress != null)
            onPress(gameObject, eventData, false);
    }
}