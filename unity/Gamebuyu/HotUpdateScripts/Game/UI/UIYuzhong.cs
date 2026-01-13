using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using DG.Tweening;
using CoreGame;
using LitJson;

namespace Game.UI
{
    public enum BY_SKILLName
    {
        锁定鱼 = 1,
        冰冻鱼 = 2,
        急速海豹 = 3,
        暴击鱼 = 4,
        全屏爆炸 = 5,
        炸弹 = 6,
        闪电鱼 = 7,
        黑洞鱼 = 8,
        奖券鱼 = 9,
        钻石鱼 = 10,
    }
    public class UIYuzhong : MonoBehaviour
    {
        public Button btn_close;
        public GameObject view_yuzhong;//, view_guize;
        public GameObject view_yuzhong_tsy;
        public GameObject view_yuzhong_boss;
        public Toggle tog_yuzhong;
        public Toggle tog_yuzhong1;
        public Toggle tog_yuzhong2;
        //---------------view_鱼种-------------
        public GameObject item;
        public GameObject item_tsy;
        public GameObject item_boss;
        public Transform grid;
        public Transform grid_tsy;
        public Transform grid_boss;
        GameObjectPool itemPool = new GameObjectPool();
        GameObjectPool itemPool_tsy = new GameObjectPool();
        GameObjectPool itemPool_boss = new GameObjectPool();

        JsonData datAll;
        private void Start()
        {
           
            tog_yuzhong.onValueChanged.AddListener((arg) =>
            {
                if (arg)
                {
                    view_yuzhong.gameObject.SetActive(true);
                    view_yuzhong_tsy.gameObject.SetActive(false);
                    view_yuzhong_boss.gameObject.SetActive(false);
                }
            });
            tog_yuzhong1.onValueChanged.AddListener((arg) =>
            {
                if (arg)
                {
                    view_yuzhong.gameObject.SetActive(false);
                    view_yuzhong_tsy.gameObject.SetActive(true);
                    view_yuzhong_boss.gameObject.SetActive(false);
                }
            });
            tog_yuzhong2.onValueChanged.AddListener((arg) =>
            {
                if (arg)
                {
                    view_yuzhong.gameObject.SetActive(false);
                    view_yuzhong_tsy.gameObject.SetActive(false);
                    view_yuzhong_boss.gameObject.SetActive(true);
                }
            });
        }
     
        FishJson GetJosnFishInfo(string name) {
            if (common4.dicMoldConfig.ContainsKey(name))
            {
                return common4.dicMoldConfig[name];
            }
            else
            {
                Debug.Log("不存在鱼名"+name);
            }
            return null;
        }
        List<string> AllFishID = new List<string>();
        void Show()
        {
            AllFishID.Clear();
            foreach (Transform item in grid)
            {
                itemPool.Recycle(item.gameObject);
            }
            foreach (Transform item in grid_tsy)
            {
                itemPool_tsy.Recycle(item.gameObject);
            }
            foreach (Transform item in grid_boss)
            {
                itemPool_boss.Recycle(item.gameObject);
            }
            Load();
            //int varModule = ByData.nModule;
            //foreach (var data in common4.dicFishConfig.Values)
            //{
            //    if (data.scene == varModule)  //判断场景
            //    {
            //        if (AllFishID.Contains(data.name))
            //        {

            //        }
            //        else
            //        {
            //            AllFishID.Add(data.name);
            //            var mJsonData = GetJosnFishInfo(data.name);
            //            if (mJsonData!=null)
            //            {
            //                if (mJsonData.isGold == true)// 特殊鱼
            //                {
            //                    var go = itemPool_tsy.Get();
            //                    go.SetActive(true);
            //                    go.transform.SetParent(grid_tsy, false);
            //                    go.transform.Find("txt_name").GetComponent<Text>().text = data.name;
            //                    go.transform.Find("img_fish").GetComponent<Image>().sprite = common4.LoadSprite(string.Format("FishIcon/{0}", mJsonData.modelID));
            //                    go.transform.Find("img_fish").GetComponent<Image>().SetNativeSize();
            //                    if (data.maxMoney > 0)
            //                    {
            //                        go.transform.Find("txt_money").GetComponent<Text>().text = data.money + "-" + data.maxMoney;
            //                    }
            //                    else
            //                    {
            //                        go.transform.Find("txt_money").GetComponent<Text>().text = data.money.ToString();
            //                    }
            //                    go.transform.SetAsLastSibling();
            //                    string strContent = "";
            //                }
            //                else if (mJsonData.isBoss == true || mJsonData.isTwoAttack == true|| mJsonData.xiaoboss == true)//boss
            //                {
            //                    var go = itemPool_boss.Get();
            //                    go.SetActive(true);
            //                    go.transform.SetParent(grid_boss, false);
            //                    go.transform.Find("txt_name").GetComponent<Text>().text = data.name;
            //                    go.transform.Find("img_fish").GetComponent<Image>().sprite = common4.LoadSprite(string.Format("FishIcon/{0}", mJsonData.modelID));
            //                    go.transform.Find("img_fish").GetComponent<Image>().SetNativeSize();
            //                    if (data.maxMoney > 0)
            //                    {
            //                        go.transform.Find("txt_money").GetComponent<Text>().text = data.money + "-" + data.maxMoney;
            //                    }
            //                    else
            //                    {
            //                        go.transform.Find("txt_money").GetComponent<Text>().text = data.money.ToString();
            //                    }
            //                    if (mJsonData.info != "")
            //                    {
            //                        go.transform.Find("txt_info").GetComponent<Text>().text = mJsonData.info;
            //                    }
            //                    go.transform.SetAsLastSibling();
            //                }
            //                else// 普通鱼
            //                {
            //                    var go = itemPool.Get();
            //                    go.SetActive(true);
            //                    go.transform.SetParent(grid, false);
            //                    go.transform.Find("txt_name").GetComponent<Text>().text = data.name;
            //                    go.transform.Find("img_fish").GetComponent<Image>().sprite = common4.LoadSprite(string.Format("FishIcon/{0}", mJsonData.modelID));
            //                    go.transform.Find("img_fish").GetComponent<Image>().SetNativeSize();
            //                    go.transform.Find("txt_money").GetComponent<Text>().text = data.money.ToString();
            //                    go.transform.SetAsLastSibling();
            //                }
            //            }
            //            else
            //            {
            //                Debug.LogError(data.name+ "dicMoldConfig未找到");
            //            }

            //        }
            //    }
            //}
        }
        void Load() {
            string varModule = ByData.nModule.ToString();
            foreach (string itemname in datAll[varModule].Keys)
            {
                var mJsonData = GetJosnFishInfo(itemname);
                if (mJsonData != null)
                {
                    if (mJsonData.isGold == true)// 特殊鱼
                    {
                        var go = itemPool_tsy.Get();
                        go.SetActive(true);
                        go.transform.SetParent(grid_tsy, false);
                        go.transform.Find("txt_name").GetComponent<Text>().text = itemname;
                        go.transform.Find("img_fish").GetComponent<Image>().sprite = common4.LoadSprite(string.Format("FishIcon/{0}", mJsonData.modelID));
                        go.transform.Find("img_fish").GetComponent<Image>().SetNativeSize();
                        go.transform.Find("txt_money").GetComponent<Text>().text = datAll[varModule][itemname].ToString();
                        go.transform.SetAsLastSibling();
                    }
                    else if (mJsonData.isBoss == true || mJsonData.isTwoAttack == true || mJsonData.xiaoboss == true)//boss
                    {
                        var go = itemPool_boss.Get();
                        go.SetActive(true);
                        go.transform.SetParent(grid_boss, false);
                        go.transform.Find("txt_name").GetComponent<Text>().text = itemname;
                        go.transform.Find("img_fish").GetComponent<Image>().sprite = common4.LoadSprite(string.Format("FishIcon/{0}", mJsonData.modelID));
                        go.transform.Find("img_fish").GetComponent<Image>().SetNativeSize();
                        go.transform.Find("txt_money").GetComponent<Text>().text = datAll[varModule][itemname].ToString();
                        if (mJsonData.info != "")
                        {
                            go.transform.Find("txt_info").GetComponent<Text>().text = mJsonData.info;
                        }
                        go.transform.SetAsLastSibling();
                    }
                    else// 普通鱼
                    {
                        var go = itemPool.Get();
                        go.SetActive(true);
                        go.transform.SetParent(grid, false);
                        go.transform.Find("txt_name").GetComponent<Text>().text = itemname;
                        go.transform.Find("img_fish").GetComponent<Image>().sprite = common4.LoadSprite(string.Format("FishIcon/{0}", mJsonData.modelID));
                        go.transform.Find("img_fish").GetComponent<Image>().SetNativeSize();
                        go.transform.Find("txt_money").GetComponent<Text>().text = datAll[varModule][itemname].ToString();
                        go.transform.SetAsLastSibling();
                    }
                }
                else
                {
                    Debug.LogError(itemname + "未找到");
                }
            }
        }
        void FindCompent() {

            btn_close = this.transform.Find("bg/btn_close").GetComponent<Button>();
            view_yuzhong = this.transform.Find("bg/view_yuzhong").gameObject;
            view_yuzhong_tsy = this.transform.Find("bg/view_yuzhong_tsy").gameObject;
            view_yuzhong_boss = this.transform.Find("bg/view_yuzhong_boss").gameObject;
            tog_yuzhong = this.transform.Find("bg/top_grid/tog_yuzhong").GetComponent<Toggle>();
            tog_yuzhong1 = this.transform.Find("bg/top_grid/tog_yuzhong1").GetComponent<Toggle>();
            tog_yuzhong2 = this.transform.Find("bg/top_grid/tog_yuzhong2").GetComponent<Toggle>();
       

            item = this.transform.Find("bg/view_yuzhong/Viewport/grid/item").gameObject;
            item_tsy = this.transform.Find("bg/view_yuzhong_tsy/Viewport/grid_tsy/item_tsy").gameObject;
            item_boss = this.transform.Find("bg/view_yuzhong_boss/Viewport/grid_boss/item_boss").gameObject;
            grid = this.transform.Find("bg/view_yuzhong/Viewport/grid");
            grid_tsy = this.transform.Find("bg/view_yuzhong_tsy/Viewport/grid_tsy");
            grid_boss = this.transform.Find("bg/view_yuzhong_boss/Viewport/grid_boss");
        }
        private void OnEnable()
        {
            Show();
        }
        private void Awake()
        {
            FindCompent();

            var result = common4.LoadAynsJson("Json/YuZhongJson");
            datAll = JsonMapper.ToObject(result.text);

            btn_close.onClick.AddListener(() => { UIMgr.CloseUI(UIPath.UIYuzhong); });
            tog_yuzhong.onValueChanged.AddListener((isOn) =>
            {
                view_yuzhong.SetActive(isOn);
                view_yuzhong_tsy.SetActive(!isOn);
            });

            //初始化鱼种 
            itemPool.SetTemplete(item);
            itemPool.Recycle(item);
            //初始化鱼种 
            itemPool_tsy.SetTemplete(item_tsy);
            itemPool_tsy.Recycle(item_tsy);

            itemPool_boss.SetTemplete(item_boss);
            itemPool_boss.Recycle(item_boss);

        }
    }
}