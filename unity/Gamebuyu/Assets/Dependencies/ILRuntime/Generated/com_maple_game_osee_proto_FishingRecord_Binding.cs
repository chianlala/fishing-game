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
    unsafe class com_maple_game_osee_proto_FishingRecord_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            MethodBase method;
            Type[] args;
            Type type = typeof(com.maple.game.osee.proto.FishingRecord);
            args = new Type[]{};
            method = type.GetMethod("get_sceneName", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_sceneName_0);
            args = new Type[]{};
            method = type.GetMethod("get_joinTime", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_joinTime_1);
            args = new Type[]{};
            method = type.GetMethod("get_exitTime", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_exitTime_2);
            args = new Type[]{};
            method = type.GetMethod("get_moneyChange", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_moneyChange_3);


        }


        static StackObject* get_sceneName_0(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.FishingRecord instance_of_this_method = (com.maple.game.osee.proto.FishingRecord)typeof(com.maple.game.osee.proto.FishingRecord).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.sceneName;

            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static StackObject* get_joinTime_1(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.FishingRecord instance_of_this_method = (com.maple.game.osee.proto.FishingRecord)typeof(com.maple.game.osee.proto.FishingRecord).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.joinTime;

            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static StackObject* get_exitTime_2(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.FishingRecord instance_of_this_method = (com.maple.game.osee.proto.FishingRecord)typeof(com.maple.game.osee.proto.FishingRecord).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.exitTime;

            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static StackObject* get_moneyChange_3(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.FishingRecord instance_of_this_method = (com.maple.game.osee.proto.FishingRecord)typeof(com.maple.game.osee.proto.FishingRecord).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.moneyChange;

            __ret->ObjectType = ObjectTypes.Long;
            *(long*)&__ret->Value = result_of_this_method;
            return __ret + 1;
        }



    }
}
