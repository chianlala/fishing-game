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

using UnityEngine;
using UnityEngine.UI;
using CoreGame;

namespace Game.UI
{
    public class BtnDuiHuanJq : MonoBehaviour
    { 
        public Button Btn_this;
        void Awake() 
        {
            Btn_this = this.transform.GetComponent<Button>();
            Btn_this.onClick.AddListener(() =>
            {
                UIMgr.ShowUI(UIPath.UIDuiHuanJq);
            }); 
        }
   
    }
}