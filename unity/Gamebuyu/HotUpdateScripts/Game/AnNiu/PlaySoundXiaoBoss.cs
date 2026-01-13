
using UnityEngine;
using UnityEngine.UI;
using CoreGame;
using DG.Tweening;
using com.maple.game.osee.proto.fishing;
using System.Collections.Generic;
using System;
using System.Reflection;
using Game.UI;
using System.Collections;
using static UnityEngine.UI.Dropdown;
using com.maple.game.osee.proto;

namespace Game.UI
{ 
    public class PlaySoundXiaoBoss : MonoBehaviour
    {

        void OnEnable() {
            SoundLoadPlay.PlaySound("sd_t2_xiaoboss_laixi");
        }
    }
}