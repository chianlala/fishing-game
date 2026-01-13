//
// Downloader.cs
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
using System.Collections.Generic;
using System.IO;
using UnityEngine;

namespace libx
{
    public class Downloader : MonoBehaviour
    {
        private const float BYTES_2_MB = 1f / (1024 * 1024);
        
        public int maxDownloads = 3;
        
        //所有待下载的
        private readonly List<Download> _downloads = new List<Download>();
        //开始下载的
        private readonly List<Download> _tostart = new List<Download>();
        //正在下载的
        private readonly List<Download> _progressing = new List<Download>();
        public Action<long, long, float> onUpdate;
        public Action onFinished;

        //已经完成的个数
        private int _finishedIndex;
        //正在下载的个数
        private int _downloadIndex;

        //开始下载时间
        private float _startTime;
        
        private float _lastTime;
        private long _lastSize;

        public long size { get; private set; }

        public long position { get; private set; }

        public float speed { get; private set; }

        public List<Download> downloads { get { return _downloads; } }

        /// <summary>
        ///获取下载的尺寸
        /// </summary>
        /// <returns></returns>
        private long GetDownloadSize()
        {
            var len = 0L;
            var downloadSize = 0L;
            foreach (var download in _downloads)
            {
                downloadSize += download.position;
                len += download.len;
            } 
            return downloadSize - (len - size);
        }

        private bool _started;
        [SerializeField]private float sampleTime = 0.5f;

        /// <summary>
        /// 开始下载
        /// </summary>
        public void StartDownload()
        {
            _tostart.Clear(); 
            _finishedIndex = 0; 
            _lastSize = 0L;
            Restart();
        }

        /// <summary>
        /// 重新开始
        /// </summary>
        public void Restart()
        {
         
            _startTime = Time.realtimeSinceStartup;
            _lastTime = 0;
            _started = true;
            _downloadIndex = _finishedIndex;

            int max = 0;
         
            //var max = _downloads.Count <= maxDownloads ? _downloads.Count : _finishedIndex + maxDownloads;
            if (_downloads.Count <= maxDownloads)
            {
                max = _downloads.Count;
            }
            else
            {
                max = _finishedIndex + maxDownloads;
            }

            if (max > _downloads.Count)
                max = _downloads.Count;

            for (var i = _finishedIndex; i < max; i++)
            {
                var item = _downloads[i];
                _tostart.Add(item);
                _downloadIndex++;
            }
        }

        /// <summary>
        /// 下载停止
        /// </summary>
        public void Stop()
        {
            _tostart.Clear();
            foreach (var download in _progressing)
            {
                download.Complete(true); 
                _downloads[download.id] = download.Clone() as Download;

            } 
            _progressing.Clear();
            _started = false;
        }
        /// <summary>
        /// 清除
        /// </summary>
        public void Clear()
        {
            size = 0;
            position = 0;
            
            _downloadIndex = 0;
            _finishedIndex = 0;
            _lastTime = 0f;
            _lastSize = 0L;
            _startTime = 0;
            _started = false; 
            foreach (var item in _progressing)
            {
                item.Complete(true);
            }
            _progressing.Clear();
            _downloads.Clear();
            _tostart.Clear();
        }

        /// <summary>
        /// 添加到下载列表
        /// </summary>
        /// <param name="url"></param>
        /// <param name="filename"></param>
        /// <param name="savePath"></param>
        /// <param name="hash"></param>
        /// <param name="len"></param>
        public void AddDownload(string url, string filename, string savePath, string hash, long len)
        {
            var download = new Download
            {
                id = _downloads.Count,
                url = url,
                name = filename,
                hash = hash,
                len = len,
                savePath = savePath,
                completed = OnFinished
            };
            _downloads.Add(download);
            var info = new FileInfo(download.tempPath);
            if (info.Exists)
            {
                size += len - info.Length; 
            }
            else
            {
                size += len; 
            }
        }
        /// <summary>
        /// 一个下载完成
        /// </summary>
        /// <param name="download"></param>
        private void OnFinished(Download download)
        {
            //一个下载完成
            if (_downloadIndex < _downloads.Count)
            {
                _tostart.Add(_downloads[_downloadIndex]);
                _downloadIndex++;    
            } 
            _finishedIndex++;
            Debug.Log(string.Format("OnFinished:{0}, {1}", _finishedIndex, _downloads.Count));
            if (_finishedIndex != downloads.Count)
                return;
            if (onFinished != null)
            {
                onFinished.Invoke(); 
            } 
            _started = false;
        }
        /// <summary>
        /// 下载速度
        /// </summary>
        /// <param name="downloadSpeed"></param>
        /// <returns></returns>
        public static string GetDisplaySpeed(float downloadSpeed)
        {
            if (downloadSpeed >= 1024 * 1024)
            {
                return string.Format("{0:f2}MB/s", downloadSpeed * BYTES_2_MB);
            }
            if (downloadSpeed >= 1024)
            {
                return string.Format("{0:f2}KB/s", downloadSpeed / 1024);
            }
            return string.Format("{0:f2}B/s", downloadSpeed);
        }

        public static string GetDisplaySize(long downloadSize)
        {
            if (downloadSize >= 1024 * 1024)
            {
                return string.Format("{0:f2}MB", downloadSize * BYTES_2_MB);
            }
            if (downloadSize >= 1024)
            {
                return string.Format("{0:f2}KB", downloadSize / 1024);
            }
            return string.Format("{0:f2}B", downloadSize);
        }

        /// <summary>
        /// 更新
        /// </summary>
        private void Update()
        {
            if (!_started)
                return;

            if (_tostart.Count > 0)
            {
                for (var i = 0; i < Math.Min(maxDownloads, _tostart.Count); ++i)
                {
                    var item = _tostart[i];
                    item.Start();
                    Debug.Log("Start Download:" + item.url);
                    _tostart.RemoveAt(i);
                    _progressing.Add(item);
                    --i;
                }
            }
            
            for (var index = 0; index < _progressing.Count; ++index)
            {
                var download = _progressing[index];
                download.Update();
                if (!download.finished)
                    continue;
                if (!string.IsNullOrEmpty(download.error))
                {
                    ////报错再次尝试
                    Debug.LogError(string.Format("Download Error:{0}, {1}", download.url, download.error));
                    download.Retry();
                    //Debug.Log("Retry Download：" + download.url);   
                    //Restart();
                }
                else
                {
                    _progressing.RemoveAt(index);
                    --index;
                    Debug.Log("Finish Download：" + download.url); 
                }
            }
            position = GetDownloadSize(); 

            //当前时间 - 开始时间 得到下载间隔
            var elapsed = Time.realtimeSinceStartup - _startTime;

            //<0.5f;
            if (elapsed - _lastTime < sampleTime)
                return;
            
            var deltaTime = elapsed - _lastTime; 

            //(当前位置 -上一个位置)/  片段时间
            speed = (position - _lastSize) / deltaTime;
            
            if (onUpdate != null)
            {
                onUpdate(position, size, speed);
            }
            
            //上一个时间
            _lastTime = elapsed;  

            //上一个尺寸
            _lastSize = position;
        }
    }
}
