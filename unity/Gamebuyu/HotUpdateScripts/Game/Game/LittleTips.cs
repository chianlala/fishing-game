using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

namespace Game.UI
{

    public class LittleTips : MonoBehaviour
    {

        private Text txtTips;
        private List<string> m_listMsg;
        void Awake()
        {
            txtTips = this.GetComponent<Text>();
            m_listMsg = new List<string>() {
        "电磁炮、狂暴、自动、锁定等技能均可免费使用！",
        "击杀boss和特殊鱼最高可中6万倍奖励！",
        "每日消耗金币参加大奖赛，上榜可以获得弹头奖励！",
        "经典渔场“熔岩龙龟”为体验场，在这里可练习捕鱼技术！",
        "在商城中消耗核弹可购买金币和钻石！",
        "在商城中消耗金币可购买核弹！",
        "游戏问题请通过客服反馈窗口提交，收到后会第一时间通过邮件回复！",
        "当屏幕中鱼不够打时，使用召唤技能可召唤出黄金鱼！",
        "boss鱼儿受到攻击会逃跑，赶紧使用冰冻技能冻住它！",
        "攻击目标被其他目标挡住了？别担心，咱们可以使用锁定技能锁定目标！",
        "想要更加刺激的捕鱼吗？同时使用暴击+电磁炮技能击杀威力更大！",
        "破产后记得次日00:00登录游戏查看邮件，有机会获得神秘鱼奖励，将通过邮件发送！",
        "房间场次等级越高，击杀鱼使用炮倍越高，最高可达到100万炮倍！",
        "开心休闲娱乐最重要，记得要理性消费哦！",
        "如果手机发烫可在设置中选择画质，画质越高对手机性能要求也就越高哦！",
        "画质太模糊的话，可以在设置中选择高画质哦！",
        "苹果点击下载后，桌面一直显示正在载入，切换网络环境，再次点击桌面图标！",
        "每次版本大更新时，请卸载后再重新安装新版本！",
        };

            //m_listMsg.Add("龙晶战场中超大倍率BOSS，等你来挑战！");
            //m_listMsg.Add("参加大奖赛可以获得大量龙珠奖励");
            //m_listMsg.Add("奖券可通过砸金猪、捕鱼掉落获得");
            //m_listMsg.Add("使用技能可以更好的进行捕鱼哦");
            //m_listMsg.Add("使用龙珠可以立即获得大量金币");
            //m_listMsg.Add("自动攻击可以通过购买月卡解锁哦");
            //m_listMsg.Add("龙晶可在龙晶战场中直接兑换成龙珠哦");
            //m_listMsg.Add("在经典渔场捕获boss有机会获得大量龙珠。");
            //m_listMsg.Add("使用号角召唤出的boss，被捕获后必掉龙珠哦。");
            //m_listMsg.Add("在企鹅大冒险中也有几率获得龙珠。");     
        }
        IEnumerator TmpTips()
        {
            yield return new  WaitForSeconds(5f);
            txtTips.text = m_listMsg[Random.Range(0, m_listMsg.Count - 1)];
            StartCoroutine(TmpTips());
        }
        private void OnEnable()
        {
            txtTips.text = m_listMsg[Random.Range(0, m_listMsg.Count - 1)];
            StartCoroutine(TmpTips());
        }
        private void OnDisable()
        {
            StopCoroutine(TmpTips());
        }
    }
}