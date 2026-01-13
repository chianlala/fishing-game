//
// Updater.cs
//
// Author:
//       fjy <jiyuan.feng@live.com>
//
// Copyright (c) 2020 fjy
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

using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using JEngine.Core;
using UnityEditor;
using UnityEngine;
using UnityEngine.Networking;
using UnityEngine.UI;

namespace libx
{
    public interface IUpdater
    {
        void OnStart();

        void OnMessage(string msg);

        void OnProgress(float progress);

        void OnVersion(string ver);

        void OnClear();
    }

    [RequireComponent(typeof(Downloader))]
    [RequireComponent(typeof(NetworkMonitor))]
    public class Updater : MonoBehaviour, IUpdater, INetworkMonitorListener
    {
        enum Step
        {
            Wait,
            Copy,
            Coping,
            Versions,
            Prepared,
            Download,
        }

        private Step _step;

        [SerializeField] private string baseURL = "http://127.0.0.1:7888/DLC/";
        [SerializeField] private string gameScene = "Game.unity";
        [SerializeField] private bool development;
        [SerializeField] public bool enableVFS = true;
        [Tooltip("离线模式")] [SerializeField] public bool offline;
        
        public static Action<string,Action<float>> OnAssetsInitialized;

        public IUpdater listener { get; set; }

        private Downloader _downloader;
        private NetworkMonitor _monitor;
        private string _platform;
        private string _savePath;
        private List<VFile> _versions = new List<VFile>();
        public Button UiChange;
        private void Start() 
        {
            UiChange.onClick.AddListener(() =>
            {
                OnApplicationFocus(false);

                OnApplicationFocus(true);
            });
            baseURL = baseURL.EndsWith("/") ? baseURL : baseURL + "/";
            
            _downloader = gameObject.GetComponent<Downloader>();
            _downloader.onUpdate = OnUpdate;
            _downloader.onFinished = OnComplete;

            _monitor = gameObject.GetComponent<NetworkMonitor>();
            _monitor.listener = this;

            _savePath = string.Format("{0}/DLC/", Application.persistentDataPath);
            _platform = GetPlatformForAssetBundles(Application.platform);

            _step = Step.Wait;

            Assets.updatePath = _savePath;

            StartUpdate();
        }
        private void OnEnable()
        {
            ToWeiZhi = 0;
        }

        public void StartUpdate()
        {
#if UNITY_EDITOR
            if (development)
            {
                Assets.runtimeMode = false;
                StartCoroutine(LoadGameScene());
                return;
            }
#endif
            OnStart();
            //开始检查
            StartCoroutine("Checking");
        }
        
        public void OnStart()
        {
            if (listener != null)
            {
                listener.OnStart();
            }
        }
        
        private IEnumerator LoadGameScene()
        {
            OnMessage("正在初始化");
            Assets.runtimeMode = !development;
            var init = Assets.Initialize();
            yield return init;
            if (string.IsNullOrEmpty(init.error))
            {
                OnProgress(0);
                OnMessage("加载游戏场景");
                init.Release();
                OnAssetsInitialized?.Invoke(gameScene, OnProgress);
            }
            else
            {
                init.Release();
                var mb = JMessageBox.Show("提示", "初始化异常错误：" + init.error + "请联系技术支持");
                yield return mb;
                Quit();
            }
        }

        private void OnDestroy()
        {
            JMessageBox.Dispose();
        }

        private void Quit()
        {
#if UNITY_EDITOR
            UnityEditor.EditorApplication.isPlaying = false;
#else
            Application.Quit();
#endif
        }

        private void OnApplicationFocus(bool hasFocus)
        {
            if (_reachabilityChanged || _step == Step.Wait)
            {
                return;
            }

            if (hasFocus)
            {
                JMessageBox.CloseAll();
                if (_step == Step.Download)
                {
                    //直接重新验证
                    _step =Step.Versions;
                    _downloader.Restart();
                }
                else
                {
                    StopAllCoroutines();
                    //开始更新
                    StartUpdate();
                }
            }
            else
            {
                if (_step == Step.Download)
                {
                    _downloader.Stop();
                }
            }
        }

        private bool _reachabilityChanged;

        public void OnReachablityChanged(NetworkReachability reachability)
        {
            if (_step == Step.Wait)
            {
                return;
            }

            _reachabilityChanged = true;
            if (_step == Step.Download)
            {
                _downloader.Stop();
            }

            if (reachability == NetworkReachability.NotReachable)
            {
                JMessageBox.Show("提示！", "找不到网络，请确保手机已经联网", "确定", "退出").onComplete += delegate(JMessageBox.EventId id)
                {
                    if (id == JMessageBox.EventId.Ok)
                    {
                        if (_step == Step.Download)
                        {
                            _downloader.Restart();
                        }
                        else
                        {
                            StartUpdate();
                        }

                        _reachabilityChanged = false;
                    }
                    else
                    {
                        Quit();
                    }
                };
            }
            else
            {
                if (_step == Step.Download)
                {
                    _downloader.Restart();
                }
                else
                {
                    StartUpdate();
                }

                _reachabilityChanged = false;
                JMessageBox.CloseAll();
            }
        }
        
        public void OnMessage(string msg)
        {
            if (listener != null)
            {
                listener.OnMessage(msg);
            }
        }

        public void OnProgress(float progress)
        {
            if (listener != null)
            {
                listener.OnProgress(progress);
            }
        }

        public void OnVersion(string ver)
        {
            if (listener != null)
            {
                listener.OnVersion(ver);
            }
        }

        
        private void OnUpdate(long progress, long size, float speed)
        {
            OnMessage(string.Format("下载中...{0}/{1}, 速度：{2}",
                Downloader.GetDisplaySize(progress),
                Downloader.GetDisplaySize(size),
                Downloader.GetDisplaySpeed(speed)));

            OnProgress(progress * 1f / size);
        }

        public void Clear()
        {
            JMessageBox.Show("提示", "清除数据后所有数据需要重新下载，请确认！", "清除").onComplete += id =>
            {
                if (id != JMessageBox.EventId.Ok)
                    return;
                OnClear();
            };
        }

        public void OnClear()
        {
            OnMessage("数据清除完毕");
            OnProgress(0);
            _versions.Clear();
            _downloader.Clear();
            _step = Step.Wait;
            _reachabilityChanged = false;

            Assets.Clear();

            if (listener != null)
            {
                listener.OnClear();
            }

            if (Directory.Exists(_savePath))
            {
                Directory.Delete(_savePath, true);
            }
        }

        private void AddDownload(VFile item)
        {
            _downloader.AddDownload(GetDownloadURL(item.name), item.name, _savePath + item.name, item.hash, item.len);
        }

        private void PrepareDownloads()
        {
            if (enableVFS)
            {
                var path = string.Format("{0}{1}", _savePath, Versions.Dataname);
                if (!File.Exists(path))
                {
                    AddDownload(_versions[0]);
                    return;
                }

                Versions.LoadDisk(path);
            }

            //清除所有
            _downloader.Clear();//
            for (var i = 1; i < _versions.Count; i++)
            {
                //获取版本文件 后作比较
                var item = _versions[i];
                //是新的就添加到下载集合去
                if (Versions.IsNew(string.Format("{0}{1}", _savePath, item.name), item.len, item.hash))
                {
                    AddDownload(item);
                }
            }
        }

        private static string GetPlatformForAssetBundles(RuntimePlatform target)
        {
#if UNITY_EDITOR
            var t = EditorUserBuildSettings.activeBuildTarget;
            switch (t)
            {
                case BuildTarget.Android:
                    return "Android";
                case BuildTarget.iOS:
                    return "iOS";
                case BuildTarget.WebGL:
                    return "WebGL";
                case BuildTarget.StandaloneWindows:
                case BuildTarget.StandaloneWindows64:
                    return "Windows";
                case BuildTarget.StandaloneOSX:
                    return "OSX";
                default:
                    return null;
            }
#endif
            switch (target)
            {
                case RuntimePlatform.Android:
                    return "Android";
                case RuntimePlatform.IPhonePlayer:
                    return "iOS";
                case RuntimePlatform.WebGLPlayer:
                    return "WebGL";
                case RuntimePlatform.WindowsPlayer:
                case RuntimePlatform.WindowsEditor:
                    return "Windows";
                case RuntimePlatform.OSXEditor:
                case RuntimePlatform.OSXPlayer:
                    return "OSX"; // OSX
                default:
                    return null;
            }
            return null;
        }

        private string GetDownloadURL(string filename)
        {
            return string.Format("{0}{1}/{2}", baseURL, _platform, filename);
        }

        private IEnumerator Checking()
        {
            if (!Directory.Exists(_savePath))
            {
                Directory.CreateDirectory(_savePath);
            }

            if (_step == Step.Wait)
            {
                _step = Step.Copy;
            }

            if (_step == Step.Copy)
            {
                yield return RequestCopy();
            }

            //步骤 StreamingAssets拷贝中
            if (_step == Step.Coping)
            {
                var path = _savePath + Versions.Filename + ".tmp";
                var versions = Versions.LoadVersions(path);
                var basePath = GetBasePath();
                yield return UpdateCopy(versions, basePath);
                _step = Step.Versions;
            }

            //版本验证
            if (_step == Step.Versions)
            {
                yield return RequestVersions();
            }

            //准备下载步骤
            if (_step == Step.Prepared)
            {
                OnMessage("正在检查版本信息...");
                var totalSize = _downloader.size;
                if (totalSize > 0)
                {
                    var tips = string.Format("发现内容更新，总计需要下载 {0} 内容", Downloader.GetDisplaySize(totalSize));
                    var mb = JMessageBox.Show("提示", tips, "下载", "退出");
                    yield return mb;
                    if (mb.isOk)
                    {
                        _downloader.StartDownload();
                        _step = Step.Download;
                    }
                    else
                    {
                        Quit();
                    }
                }
                else
                {
                    OnComplete();
                }
            }
        }

        private IEnumerator RequestVersions()
        {
            if (offline)
            {
                OnComplete();
                yield break;
            }
            OnMessage("正在获取版本信息...");
            if (Application.internetReachability == NetworkReachability.NotReachable)
            {
                var mb = JMessageBox.Show("提示", "请检查网络连接状态", "重试", "退出");
                yield return mb;
                if (mb.isOk)
                {
                    StartUpdate();
                }
                else
                {
                    Quit();
                }

                yield break;
            }

            var request = UnityWebRequest.Get(GetDownloadURL(Versions.Filename));
            request.downloadHandler = new DownloadHandlerFile(_savePath + Versions.Filename);
            yield return request.SendWebRequest();
            var error = request.error;
            request.Dispose();
            if (!string.IsNullOrEmpty(error))
            {
                var mb = JMessageBox.Show("提示", string.Format("获取服务器版本失败：{0}", error), "重试", "退出");
                yield return mb;
                if (mb.isOk)
                {
                    StartUpdate();
                }
                else
                {
                    Quit();
                }

                yield break;
            }

            try
            {
                var v1 = Versions.LoadVersion(_savePath + Versions.Filename);           //网络版本文件
                var v2 = Versions.LoadVersion(_savePath + Versions.Filename + ".tmp");  //本地临时文件

                //if (v2 > v1)
                //{
                //    //如果本地版本高于网络版本，就别更新了
                //    OnComplete();
                //    yield break;
                //}

                //网络版本高于或者等于本地版本，则检查更新
                _versions = Versions.LoadVersions(_savePath + Versions.Filename, true);
                if (_versions.Count > 0)
                {
                    PrepareDownloads();
                    _step = Step.Prepared;
                }
                else
                {
                    OnComplete();
                }
            }
            catch (Exception e)
            {
                Debug.LogException(e);
                JMessageBox.Show("提示", "版本文件加载失败", "重试", "退出").onComplete +=
                    delegate(JMessageBox.EventId id)
                    {
                        if (id == JMessageBox.EventId.Ok)
                        {
                            StartUpdate();
                        }
                        else
                        {
                            Quit();
                        }
                    };
            }
        }

        private static string GetBasePath()
        {
            if (Application.platform == RuntimePlatform.Android)
            {
                return Application.streamingAssetsPath + "/";
            }

            if (Application.platform == RuntimePlatform.WindowsPlayer ||
                Application.platform == RuntimePlatform.WindowsEditor)
            {
                return "file:///" + Application.streamingAssetsPath + "/";
            }

            return "file://" + Application.streamingAssetsPath + "/";
        }

        private IEnumerator RequestCopy()
        {
            var v1 = offline? -1 : Versions.LoadVersion(_savePath + Versions.Filename);//这个是服务器版本
            //streamingAssetsPaths路径下的
            var basePath = GetBasePath();
            var request = UnityWebRequest.Get(Path.Combine(basePath, Versions.Filename));
            var path = _savePath + Versions.Filename + ".tmp";
            request.downloadHandler = new DownloadHandlerFile(path);
            yield return request.SendWebRequest();
            var v2 = -1;//这个是本地版本
            var hasFile = string.IsNullOrEmpty(request.error);
            if (hasFile) { v2 = Versions.LoadVersion(path); }
            var steamFileThenSave = v2 >= v1;
            if (steamFileThenSave) { Debug.LogWarning(offline?"离线模式直接解压本地资源":"本地流目录版本高于或等于网络目录版本"); }


            var tmppath = _savePath + Versions.Dataname;
            //存在路径
            if (File.Exists(tmppath)) 
            {
                //存在res
                //没有报错 且文件流已经保存
                //if (hasFile && steamFileThenSave)
                //{
                //    _step = Step.Versions;
                //}
                //else
                //{
                //    _step = Step.Coping;
                //}
                _step = Step.Versions;
            }
            else
            {
                //不存在
                _step = Step.Coping;
            }
            //_step = hasFile && steamFileThenSave ? Step.Coping : Step.Versions;
            if (!hasFile && offline)
            {
                var mb = JMessageBox.Show("提示", "离线模式需要将资源拷贝到本地，目前检测不到，因此游戏无法正常运行");
                yield return mb;
                Quit();
            }
            request.Dispose();
        }

        int ToWeiZhi = 0;
        private IEnumerator UpdateCopy(IList<VFile> versions, string basePath)
        {
            //var version = versions[0];
            //if (version.name.Equals(Versions.Dataname))
            //{
            //    var request = UnityWebRequest.Get(Path.Combine(basePath, version.name));
            //    var path = _savePath + version.name;
            //    if (File.Exists(path))
            //    {
            //        File.Delete(path);
            //    }

            //    request.downloadHandler = new DownloadHandlerFile(path);
            //    var req = request.SendWebRequest();
            //    while (!req.isDone)
            //    {
            //        OnMessage("正在复制文件");
            //        OnProgress(req.progress);
            //        yield return null;
            //    }

            //    request.Dispose();
            //}
            //else
            //{
                for (var index = ToWeiZhi; index < versions.Count; index++)
                {
                    var item = versions[index];
                    var request = UnityWebRequest.Get(Path.Combine(basePath, item.name));
                    var path = _savePath + item.name;
                    if (File.Exists(path))
                    {
                        File.Delete(path);
                    }

                    request.downloadHandler = new DownloadHandlerFile(path);
                    yield return request.SendWebRequest();
                    request.Dispose();
                    OnMessage(string.Format("正在复制文件：{0}/{1}", index, versions.Count));
                    OnProgress(index * 1f / versions.Count);
                    ToWeiZhi = index;
                }
            //}
        }

        private void OnComplete()
        {
            if (enableVFS)
            {
                var dataPath = _savePath + Versions.Dataname;
                var downloads = _downloader.downloads;
                if (downloads.Count > 0 && File.Exists(dataPath))
                {
                    OnMessage("更新本地版本信息");
                    var files = new List<VFile>(downloads.Count);
                    foreach (var download in downloads)
                    {
                        files.Add(new VFile
                        {
                            name = download.name,
                            hash = download.hash,
                            len = download.len,
                        });
                    }

                    var file = files[0];
                    if (!file.name.Equals(Versions.Dataname))
                    {
                        Versions.UpdateDisk(dataPath, files);
                    }
                }

                Versions.LoadDisk(dataPath);
            }

            OnProgress(1);
            OnMessage("更新完成");
            var version = Versions.LoadVersion(_savePath + Versions.Filename);
            if (version > 0)
            {
                OnVersion("资源版本号: v" + Application.version + "res" + version.ToString());
            }

            StartCoroutine(LoadGameScene());
        }
    }
}
