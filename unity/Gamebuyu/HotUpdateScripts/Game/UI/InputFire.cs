//
// InstantiateDemo.cs
//
// Author:
//       JasonXuDeveloper（傑） <jasonxudeveloper@gmail.com>
//
// Copyright (c) 2021 JEngine
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
using UnityEngine;
using JEngine.Core;
using JEngine.UI;
using CoreGame;
using NetLib;
using System;
using com.maple.game.osee.proto;
using UnityEngine.EventSystems;

namespace Game.UI
{

    public class InputFire : MonoBehaviour, IPointerClickHandler, IPointerDownHandler, IPointerUpHandler
    {
        public bool bPress = false;
        public bool bClick = false;
        private float fBianjie = 50;
        // Use this for initialization
        void Start()
        {
            UIEventListener.Get(this.gameObject).onPress = ((a, b, c) =>
            {
                bPress = c;
            });
            UIEventListener.Get(this.gameObject).onClick = ((a, b) =>
            {
                //bClick = true;
                Vector2 vInput = commonunity.GetClickPos();
                common.OldInput = vInput;
                //commonhot._UIFishingInterface.GetobjPlayer()[commonhot.listPlayer[PlayerData.PlayerId].pos].ChangeAutoTarget(vInput);
                EventManager.ChangeAutoTarget?.Invoke(vInput);
            });
        }

        private Texture2D m_screenRenderTexture;
        private Color m_pickedColor = Color.white;


        void Update()
        {
            if (bPress || bClick)
            {
                if (bClick)
                {
                    bClick = false;
                }
                Vector2 vInput = commonunity.GetClickPos();
               // Debug.LogWarning(vInput.x + "角度：" + vInput.y);
                common.OldInput = vInput;
                common.OldInputAngle = 0f;
                EventManager.ClickDoFire?.Invoke(vInput);
            } 
            //自动射击        
            if(PlayerData._RootbZiDong) 
            {
                EventManager.DoFire?.Invoke(common.OldInput);
                return;
            }
            ////触摸滑动条事件
            //if (common.bAlwals == true)
            //{
            //    EventManager.Req_DoFire?.Invoke(common.OldInput, common.OldInputAngle);
            //}


        }

        public void OnPointerClick(PointerEventData eventData)
        {
            //bPress = eventData.;
            //Debug.Log("次数"+eventData.clickCount);
            bClick = true;
        }

        public void OnPointerDown(PointerEventData eventData)
        {
    
            bPress = true;
        }

        public void OnPointerUp(PointerEventData eventData)
        {
            bPress = false;
 
        }
    }

}