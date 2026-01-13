using System.Collections.Generic;

namespace Game.UI
{
    /// <summary>
    /// JEngine's UI
    /// </summary>
    public static class MjCanShu
    {
        public static Dictionary<int, string> BY_ItemLName = new Dictionary<int, string>()
        {
            {1, "金币"  },
            {2, "金币大"  },
            {3, "奖券"  },
            {4, "钻石"  },
            {7, "核弹"  },
            {8, "锁定"  },
            {9, "冰冻"  },
            {11, "暴击"  },
            {12, "月卡"  },
            {13, "号角"  },
            {18, "龙晶"  },
            {19, "分身"  },
            {100, "自动"  },
        };
    }
    public class BY_SESSION
    {
        public static int 普通场 = 0;
        public static int 龙晶场 = 1;
        public static int 大奖赛 = 2;
    }
    public enum BY_SESSIONNAME
    {
        普通1号场 = 1,
        普通2号场 = 2,
        普通3号场 = 3,
        普通4号场 = 4,
        普通5号场 = 5, 
        //普通5号场 = 5,
        龙晶1号场 = 6,
        龙晶2号场 = 7,
        龙晶3号场 = 8,
        大奖赛 = 9,
    }
    public class BY_SKILL
    {
        public static int LOCK = 8; //锁定
        public static int ICE = 9; //冰冻
        public static int FAST = 10;
        public static int FURY = 11;//暴击    
        public static int SUMMON = 38;//神灯  

        public static int FENGSHEN = 19;//分身      
        public static int SHANDIANPAO = 50;//闪电炮
        public static int ZHUANTOU = 64;//钻头 
        public static int BLACKHOLE = 51;//黑洞  
        public static int BOOM = 52;//炸弹   
        public static int QINGTONGYULEI = 5;
        public static int BAIYINYULEI = 6;
        public static int HUANGJINYULEI = 7;
    }
}