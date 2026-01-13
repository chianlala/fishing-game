using CoreGame;
using Game.UI;
using JEngine.Core;
using System;
using System.Collections.Generic;
using UnityEngine;

namespace CoreGame
{
    public static class UIMgr
    {
        public static Dictionary<string, GameObject> _allPage = new Dictionary<string, GameObject>();

        /// <summary>
        /// 异步不回调
        /// </summary>
        /// <param name="varUIcon"></param>
        public static void ShowUI(UIContext varUIcon)
        {
            if (_allPage.ContainsKey(varUIcon.name))
            {
                _allPage[varUIcon.name].transform.SetAsLastSibling();
                _allPage[varUIcon.name].gameObject.SetActive(true);                
            }
            else
            {
               var prefab = common4.LoadPrefab(varUIcon.name);
                if (varUIcon.uiType == UIType.NormalUICanvas)
                {
                    GameObject g1 = UnityEngine.Object.Instantiate(prefab, common2.NormalUICanvas);
                    g1.transform.localScale = Vector3.one;
                    g1.name = varUIcon.name;
                    g1.transform.SetAsLastSibling();
                    _allPage.Add(varUIcon.name, g1);
                }
                else if (varUIcon.uiType == UIType.FixedUICanvas)
                {
                    GameObject g1 = UnityEngine.Object.Instantiate(prefab, common2.FixedUICanvas);
                    g1.transform.localScale = Vector3.one;
                    g1.name = varUIcon.name;
                    g1.transform.SetAsLastSibling();
                    _allPage.Add(varUIcon.name, g1);
                }
                else if (varUIcon.uiType == UIType.PopUpUICanvas)
                {
                    GameObject g1 = UnityEngine.Object.Instantiate(prefab, common2.PopUpUICanvas);
                    g1.transform.localScale = Vector3.one;
                    g1.name = varUIcon.name;
                    g1.transform.SetAsLastSibling();
                    _allPage.Add(varUIcon.name, g1);
                }
            }
        }

        public static bool IsContains(UIContext varUIcon) {
            if (_allPage.ContainsKey(varUIcon.name))
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        public static void ShowUI(UIContext varUIcon, GameObject varGo)
        {
            if (varUIcon.name == null)
                return;
            if (_allPage.ContainsKey(varUIcon.name))
            { 
                _allPage[varUIcon.name].transform.SetAsLastSibling();
                _allPage[varUIcon.name].gameObject.SetActive(true);
            }
            else
            {

                if (varUIcon.uiType == UIType.NormalUICanvas)
                {
                    GameObject g1 = UnityEngine.Object.Instantiate(varGo, common2.NormalUICanvas);

                    g1.transform.localScale = Vector3.one;
                    g1.name = varUIcon.name;
                    g1.transform.SetAsLastSibling();
                    _allPage.Add(varUIcon.name, g1);
                }
                else if (varUIcon.uiType == UIType.FixedUICanvas)
                {
                    GameObject g1 = UnityEngine.Object.Instantiate(varGo, common2.FixedUICanvas);

                    g1.transform.localScale = Vector3.one;
                    g1.name = varUIcon.name;
                    g1.transform.SetAsLastSibling();
                    _allPage.Add(varUIcon.name, g1);
                }
                else if (varUIcon.uiType == UIType.PopUpUICanvas)
                {
                    GameObject g1 = UnityEngine.Object.Instantiate(varGo, common2.PopUpUICanvas);

                    g1.transform.localScale = Vector3.one;
                    g1.name = varUIcon.name;
                    g1.transform.SetAsLastSibling();
                    _allPage.Add(varUIcon.name, g1);
                }
                else if (varUIcon.uiType == UIType.BuyuUICanvas)
                {
                    GameObject g1 = UnityEngine.Object.Instantiate(varGo, common2.BuyuUICanvas);

                    g1.transform.localScale = Vector3.one;
                    g1.name = varUIcon.name;
                    g1.transform.SetAsLastSibling();
                    _allPage.Add(varUIcon.name, g1);
                }

            }
        }
        //获取父级
        public static Transform GetTrasParentPos(UIType uiType)
        {
            if (uiType == UIType.TwoAttackCanvas)
            {
                return common2.TwoAttackCanvas; 
            }
            else if (uiType == UIType.NormalUICanvas)
            {
                return common2.NormalUICanvas;
            }
            else if (uiType == UIType.FixedUICanvas)
            {
                return common2.FixedUICanvas;
            }
            else if (uiType == UIType.PopUpUICanvas)
            {
                return common2.PopUpUICanvas;
            }
            else
            {
                return common2.BuyuUICanvas;
            }
            //else if (uiType == 5)
            //{
            //    return Root3D.Instance.transform;
            //}
        }
        ///// <summary>
        ///// 异步打开UI
        ///// </summary>
        ///// <param name="varUIcon"></param>
        ///// <param name="complete"></param>
        //public static void ShowUI(UIContext varUIcon, Action complete) 
        //{ 
        //    if (_allPage.ContainsKey(varUIcon.name)) 
        //    {
        //        _allPage[varUIcon.name].transform.SetAsLastSibling();
        //        _allPage[varUIcon.name].gameObject.SetActive(true);
        //        complete();
        //    }
        //    else
        //    {

        //        new JPrefab(varUIcon.name, (result, prefab) =>
        //        {

        //            GameObject g1 = UnityEngine.Object.Instantiate(prefab.Instance, GetTrasParentPos(varUIcon.uiType));

        //            g1.transform.localScale = Vector3.one;
        //            g1.name = varUIcon.name;
        //            g1.transform.SetAsLastSibling();
        //            _allPage.Add(varUIcon.name, g1);                    
        //            complete();
        //        });
        //    }
        //}
        /// <summary>
        /// 同步打开UI
        /// </summary>
        /// <param name="varUIcon"></param>
        /// <returns></returns>
        public static GameObject ShowUISynchronize(UIContext varUIcon)
        {
            if (_allPage.ContainsKey(varUIcon.name))
            {
                if (_allPage[varUIcon.name]!=null)
                {
                    _allPage[varUIcon.name].transform.SetAsLastSibling();
                    _allPage[varUIcon.name].gameObject.SetActive(true);
                    return _allPage[varUIcon.name].gameObject;
                }
                else
                {
                    var prefab = common4.LoadPrefab(varUIcon.name);
                    GameObject g1 = UnityEngine.Object.Instantiate(prefab, GetTrasParentPos(varUIcon.uiType));
                    g1.transform.localScale = Vector3.one;
                    g1.name = varUIcon.name;
                    g1.transform.SetAsLastSibling();
                    _allPage[varUIcon.name]= g1;
                    g1.gameObject.SetActive(true);
                    return g1;
                }
            }
            else
            {
                var prefab = common4.LoadPrefab(varUIcon.name);
                GameObject g1 = UnityEngine.Object.Instantiate(prefab, GetTrasParentPos(varUIcon.uiType));
                g1.transform.localScale = Vector3.one;
                g1.name = varUIcon.name;
                g1.transform.SetAsLastSibling();
                _allPage.Add(varUIcon.name, g1);
                g1.gameObject.SetActive(true);
                return g1;
            }
        }
        /// <summary>
        /// 同步获取UI
        /// </summary>
        /// <param name="varUIcon"></param>
        /// <returns></returns>
        public static GameObject GetShowUISynchronize(UIContext varUIcon)
        {
            if (_allPage.ContainsKey(varUIcon.name))
            {
                _allPage[varUIcon.name].gameObject.SetActive(true);
                return _allPage[varUIcon.name];
            }
            else
            {
                var prefab = common4.LoadPrefab(varUIcon.name);
                GameObject g1 = UnityEngine.Object.Instantiate(prefab, GetTrasParentPos(varUIcon.uiType));

                g1.transform.localScale = Vector3.one;
                g1.name = varUIcon.name;
                g1.transform.SetAsLastSibling();
                _allPage.Add(varUIcon.name, g1);
                return g1;
           
            }

        }
        /// <summary>
        /// 关闭所有UI
        /// </summary>
        public static void CloseAll()  
        {
            foreach (var item in _allPage)
            {
                item.Value.gameObject.SetActive(false);
            }          
        }
        /// <summary>
        /// 关闭除自己 的所有UI
        /// </summary> 
        public static void CloseAllwithOut(UIContext varUICon)
        {
            foreach (var item in _allPage)
            {
                if (item.Key== varUICon.name)
                {

                }
                else
                {
                    if (item.Value!=null)
                    {
                        item.Value.gameObject.SetActive(false);
                    }
                }
                
            }
        }
        public static void CloseAllwithOutTwo(UIContext varUICon, UIContext varUICon2)
        {
            foreach (var item in _allPage)
            {
                if (item.Key == varUICon.name || item.Key == varUICon2.name)
                {

                }
                else
                {
                    if (item.Value != null)
                    {
                        item.Value.gameObject.SetActive(false);
                    }
                }

            }
        }
        /// <summary>
        /// 关闭UI
        /// </summary>
        /// <param name="ObjectName"></param>
        public static void CloseUI(UIContext ObjectName)
        {
            if (_allPage.ContainsKey(ObjectName.name))
            {
                _allPage[ObjectName.name].gameObject.SetActive(false);
            }
            else
            {

            }
        }
        /// <summary>
        /// 删除UI
        /// </summary>
        /// <param name="ObjectName"></param>
        public static void DestroyUI(UIContext ObjectName)
        {
            if (_allPage.ContainsKey(ObjectName.name))
            {
                var mGo = _allPage[ObjectName.name].gameObject;
                UnityEngine.Object.DestroyImmediate(mGo);
                _allPage.Remove(ObjectName.name);
            }
            else
            {

            }
        }



        //此界面不唯一
        public static List<GameObject> AllCreateUI = new List<GameObject>();
        public static GameObject ShowCreateUI(UIContext varUIcon)
        {

            var prefab = common4.LoadPrefab(varUIcon.name);
            GameObject g1 = UnityEngine.Object.Instantiate(prefab, GetTrasParentPos(varUIcon.uiType));

            g1.transform.localScale = Vector3.one;
            g1.name = varUIcon.name;
            g1.transform.SetAsLastSibling();
            AllCreateUI.Add(g1);
            return g1;
        }

        public static void ShowAsynCreatePerfab(string varPerfabname, Transform PerfabParent)
        {
            var AssetR = common4.LoadAynsPrefab(varPerfabname);
            AssetR.completed += (arg) => {
                if (arg.isDone == true)
                {
                    var vprefab = (GameObject)AssetR.asset;
                    GameObject g1 = UnityEngine.Object.Instantiate(vprefab, PerfabParent);
                    g1.transform.localScale = Vector3.one;
                    g1.transform.SetAsLastSibling();
                    AllCreateUI.Add(g1);
                }
            };
        }
        /// <summary>
        /// 二次伤害鱼专用
        /// </summary>
        public static void DestroyCreateUI()
        {
            for (int i = AllCreateUI.Count - 1; i >= 0; i--)
            {
                if (AllCreateUI[i] != null)
                {
                    GameObject.Destroy(AllCreateUI[i]);
                }
            }
        }

        //此界面不唯一
        public static List<GameObject> AllCreatePerfab = new List<GameObject>();
        //boss来袭等
        public static GameObject ShowCreatePerfab(string varPerfabname,Transform PerfabParent) 
        {
            var prefab = common4.LoadPrefab(varPerfabname);
            GameObject g1 = UnityEngine.Object.Instantiate(prefab, PerfabParent);
            g1.transform.localScale = Vector3.one;
            g1.transform.SetAsLastSibling();
            AllCreateUI.Add(g1); 
            return g1;
        }
        public static void DestroyOneCreatePerfab(GameObject Go)
        {
            Go.SetActive(false);
            GameObject.Destroy(Go);
        }
        public static void DestroyAllCreatePerfab() 
        {
            for (int i = AllCreatePerfab.Count - 1; i >= 0; i--)
            {
                if (AllCreatePerfab[i] != null)
                {
                    GameObject.Destroy(AllCreateUI[i]);
                }
            }
        }
    }
}
