using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Runtime.InteropServices;

using ILRuntime.CLR.TypeSystem;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using ILRuntime.Runtime.Stack;
using ILRuntime.Reflection;
using ILRuntime.CLR.Utils;

namespace ILRuntime.Runtime.Generated
{
    unsafe class NetLib_SocketClient_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            MethodBase method;
            Type[] args;
            Type type = typeof(NetLib.SocketClient);
            //args = new Type[]{typeof(System.Int32), typeof(System.Type)};
            //method = type.GetMethod("RegisterCmd", flag, null, args, null);
            //app.RegisterCLRMethodRedirection(method, RegisterCmd_0);
            args = new Type[]{typeof(System.Int32), typeof(ProtoBuf.IExtensible)};
            method = type.GetMethod("SendMsg", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, SendMsg_1);
            args = new Type[]{};
            method = type.GetMethod("get_IsConnected", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_IsConnected_2);
            //args = new Type[]{};
            //method = type.GetMethod("GetNetMessage", flag, null, args, null);
            //app.RegisterCLRMethodRedirection(method, GetNetMessage_3);


        }


        //static StackObject* RegisterCmd_0(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        //{
        //    ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
        //    StackObject* ptr_of_this_method;
        //    StackObject* __ret = ILIntepreter.Minus(__esp, 3);

        //    ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
        //    System.Type @type = (System.Type)typeof(System.Type).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
        //    __intp.Free(ptr_of_this_method);

        //    ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
        //    System.Int32 @msgId = ptr_of_this_method->Value;

        //    ptr_of_this_method = ILIntepreter.Minus(__esp, 3);
        //    NetLib.SocketClient instance_of_this_method = (NetLib.SocketClient)typeof(NetLib.SocketClient).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
        //    __intp.Free(ptr_of_this_method);

        //    instance_of_this_method.RegisterCmd(@msgId, @type);

        //    return __ret;
        //}

        static StackObject* SendMsg_1(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 3);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            ProtoBuf.IExtensible @msg = (ProtoBuf.IExtensible)typeof(ProtoBuf.IExtensible).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            System.Int32 @cmd = ptr_of_this_method->Value;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 3);
            NetLib.SocketClient instance_of_this_method = (NetLib.SocketClient)typeof(NetLib.SocketClient).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.SendMsg(@cmd, @msg);

            return __ret;
        }

        static StackObject* get_IsConnected_2(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            NetLib.SocketClient instance_of_this_method = (NetLib.SocketClient)typeof(NetLib.SocketClient).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.IsConnected;

            __ret->ObjectType = ObjectTypes.Integer;
            __ret->Value = result_of_this_method ? 1 : 0;
            return __ret + 1;
        }

        //static StackObject* GetNetMessage_3(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        //{
        //    ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
        //    StackObject* ptr_of_this_method;
        //    StackObject* __ret = ILIntepreter.Minus(__esp, 1);

        //    ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
        //    NetLib.SocketClient instance_of_this_method = (NetLib.SocketClient)typeof(NetLib.SocketClient).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
        //    __intp.Free(ptr_of_this_method);

        //    var result_of_this_method = instance_of_this_method.GetNetMessage();

        //    return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        //}



    }
}
