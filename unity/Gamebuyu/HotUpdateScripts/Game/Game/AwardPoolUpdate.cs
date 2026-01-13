using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using DG.Tweening;
using System;

namespace Game.UI
{
    public class AwardPoolUpdate : MonoBehaviour
    {
        private Text thisText;
        private int Num = 1980000;
        public int RoomType;

        //随机范围
        private int Min = 500000;
        private int Max = 900000000;

        //获取当前的小时 的随机数 加上当前秒  
        void Awake()
        {
            thisText = this.GetComponent<Text>();
        }
        void OnEnable()
        {
            if (RoomType == 0)
            {
                Min = 450000;
                Max = 400000000;
            }
            else if (RoomType == 1)
            {
                Min = 450000;
                Max = 500000000;
            }
            else if (RoomType == 2)
            {
                Min = 500000;
                Max = 600000000;
            }
            else if (RoomType == 3)
            {
                Min = 550000;
                Max = 700000000;
            }
            else if (RoomType == 4)
            {
                Min = 1000000;
                Max = 800000000;
            }
            else if (RoomType == 5)
            {
                Min = 1500000;
                Max = 900000000;
            }
            int timestamp = GetTimeStamp();
            int nowAdd = timestamp % 60;
            //需为60的倍数  即这数60秒变一次
            int min = timestamp - nowAdd;
            Num = new System.Random(min).Next(Min, Max) + DateTime.Now.Millisecond;
            thisText.text = Num.ToString("N0");
        }

        public void SetType(int roomType)
        {
            RoomType = roomType;
            if (RoomType == 0)
            {
                Min = 450000;
                Max = 400000000;
            }
            else if (RoomType == 11)
            {
                Min = 450000;
                Max = 500000000;
            }
            else if (RoomType == 12)
            {
                Min = 500000;
                Max = 600000000;
            }
            else if (RoomType == 13)
            {
                Min = 550000;
                Max = 700000000;
            }
            else if (RoomType == 14)
            {
                Min = 1000000;
                Max = 800000000;
            }
            else if (RoomType == 15)
            {
                Min = 1500000;
                Max = 900000000;
            }
            int timestamp = GetTimeStamp();
            int nowAdd = timestamp % 60;
            //需为60的倍数  即这数60秒变一次
            int min = timestamp - nowAdd;
            Num = new System.Random(min).Next(Min, Max) + DateTime.Now.Millisecond;
            thisText.text = Num.ToString("N0");
        }

        public int GetTimeStamp()
        {
            TimeSpan ts = DateTime.Now - new DateTime(1970, 1, 1, 0, 0, 0, 0);
            return Convert.ToInt32(ts.TotalSeconds);
        }
        void Update()
        {
            int timestamp = GetTimeStamp();
            int nowAdd = timestamp % 60;
            //需为60的倍数  即这数60秒变一次
            int min = timestamp - nowAdd;
            Num = new System.Random(min - RoomType).Next(Min, Max) + DateTime.Now.Second * 1000 + DateTime.Now.Millisecond;
            thisText.text = Num.ToString("N0");
        }
    }
}
