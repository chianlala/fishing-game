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
    unsafe class UserInfo_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            MethodBase method;
            FieldInfo field;
            Type[] args;
            Type type = typeof(global::UserInfo);
            args = new Type[]{};
            method = type.GetMethod("SavePlayerData", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, SavePlayerData_0);
            args = new Type[]{};
            method = type.GetMethod("LoadPlayerData", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, LoadPlayerData_1);

            field = type.GetField("playerData", flag);
            app.RegisterCLRFieldGetter(field, get_playerData_0);
            app.RegisterCLRFieldSetter(field, set_playerData_0);
            app.RegisterCLRFieldBinding(field, CopyToStack_playerData_0, AssignFromStack_playerData_0);
            field = type.GetField("tmpPlayer", flag);
            app.RegisterCLRFieldGetter(field, get_tmpPlayer_1);
            app.RegisterCLRFieldSetter(field, set_tmpPlayer_1);
            app.RegisterCLRFieldBinding(field, CopyToStack_tmpPlayer_1, AssignFromStack_tmpPlayer_1);


        }


        static StackObject* SavePlayerData_0(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* __ret = ILIntepreter.Minus(__esp, 0);


            global::UserInfo.SavePlayerData();

            return __ret;
        }

        static StackObject* LoadPlayerData_1(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* __ret = ILIntepreter.Minus(__esp, 0);


            var result_of_this_method = global::UserInfo.LoadPlayerData();

            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }


        static object get_playerData_0(ref object o)
        {
            return global::UserInfo.playerData;
        }

        static StackObject* CopyToStack_playerData_0(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = global::UserInfo.playerData;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_playerData_0(ref object o, object v)
        {
            global::UserInfo.playerData = (global::PlayerUserData)v;
        }

        static StackObject* AssignFromStack_playerData_0(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            global::PlayerUserData @playerData = (global::PlayerUserData)typeof(global::PlayerUserData).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            global::UserInfo.playerData = @playerData;
            return ptr_of_this_method;
        }

        static object get_tmpPlayer_1(ref object o)
        {
            return global::UserInfo.tmpPlayer;
        }

        static StackObject* CopyToStack_tmpPlayer_1(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = global::UserInfo.tmpPlayer;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_tmpPlayer_1(ref object o, object v)
        {
            global::UserInfo.tmpPlayer = (global::PlayerAccount)v;
        }

        static StackObject* AssignFromStack_tmpPlayer_1(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            global::PlayerAccount @tmpPlayer = (global::PlayerAccount)typeof(global::PlayerAccount).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)16);
            global::UserInfo.tmpPlayer = @tmpPlayer;
            return ptr_of_this_method;
        }



    }
}
