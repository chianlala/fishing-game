using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace NetLib
{
    public class DebugSystem
    {
        static Action<object> debugCall = null;
        static bool isEnabled = true;

        /// <summary>
        /// 是否开启调试功能
        /// </summary>
        public static bool IsEnabled
        {
            get { return isEnabled; }
            set { isEnabled = value; }
        }

        /// <summary>
        /// 记录日志信息
        /// </summary>
        /// <param name="message"></param>
        public static void Log(object message)
        {
            if (!IsEnabled)
                return;

            if (debugCall != null)
                debugCall(message);
        }
        /// <summary>
        /// 注册调试调用函数
        /// </summary>
        /// <param name="func"></param>
        public static void RegisterDebugFunc(Action<object> func)
        {
            debugCall = func;
        }
    }
}
