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
    unsafe class com_maple_game_osee_proto_fishing_FishingFireResponse_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            MethodBase method;
            Type[] args;
            Type type = typeof(com.maple.game.osee.proto.fishing.FishingFireResponse);
            args = new Type[]{};
            method = type.GetMethod("get_playerId", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_playerId_0);
            args = new Type[]{typeof(System.Int64)};
            method = type.GetMethod("set_playerId", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, set_playerId_1);
            args = new Type[]{typeof(System.Int64)};
            method = type.GetMethod("set_fireId", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, set_fireId_2);
            args = new Type[]{typeof(System.Single)};
            method = type.GetMethod("set_angle", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, set_angle_3);
            args = new Type[]{};
            method = type.GetMethod("get_angle", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_angle_4);
            args = new Type[]{};
            method = type.GetMethod("get_fireId", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_fireId_5);
            args = new Type[]{};
            method = type.GetMethod("get_restMoney", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_restMoney_6);

            args = new Type[]{};
            method = type.GetConstructor(flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, Ctor_0);

        }


        static StackObject* get_playerId_0(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.fishing.FishingFireResponse instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingFireResponse)typeof(com.maple.game.osee.proto.fishing.FishingFireResponse).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.playerId;

            __ret->ObjectType = ObjectTypes.Long;
            *(long*)&__ret->Value = result_of_this_method;
            return __ret + 1;
        }

        static StackObject* set_playerId_1(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 2);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Int64 @value = *(long*)&ptr_of_this_method->Value;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            com.maple.game.osee.proto.fishing.FishingFireResponse instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingFireResponse)typeof(com.maple.game.osee.proto.fishing.FishingFireResponse).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.playerId = value;

            return __ret;
        }

        static StackObject* set_fireId_2(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 2);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Int64 @value = *(long*)&ptr_of_this_method->Value;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            com.maple.game.osee.proto.fishing.FishingFireResponse instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingFireResponse)typeof(com.maple.game.osee.proto.fishing.FishingFireResponse).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.fireId = value;

            return __ret;
        }

        static StackObject* set_angle_3(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 2);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Single @value = *(float*)&ptr_of_this_method->Value;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            com.maple.game.osee.proto.fishing.FishingFireResponse instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingFireResponse)typeof(com.maple.game.osee.proto.fishing.FishingFireResponse).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.angle = value;

            return __ret;
        }

        static StackObject* get_angle_4(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.fishing.FishingFireResponse instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingFireResponse)typeof(com.maple.game.osee.proto.fishing.FishingFireResponse).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.angle;

            __ret->ObjectType = ObjectTypes.Float;
            *(float*)&__ret->Value = result_of_this_method;
            return __ret + 1;
        }

        static StackObject* get_fireId_5(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.fishing.FishingFireResponse instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingFireResponse)typeof(com.maple.game.osee.proto.fishing.FishingFireResponse).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.fireId;

            __ret->ObjectType = ObjectTypes.Long;
            *(long*)&__ret->Value = result_of_this_method;
            return __ret + 1;
        }

        static StackObject* get_restMoney_6(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.fishing.FishingFireResponse instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingFireResponse)typeof(com.maple.game.osee.proto.fishing.FishingFireResponse).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.restMoney;

            __ret->ObjectType = ObjectTypes.Long;
            *(long*)&__ret->Value = result_of_this_method;
            return __ret + 1;
        }


        static StackObject* Ctor_0(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* __ret = ILIntepreter.Minus(__esp, 0);

            var result_of_this_method = new com.maple.game.osee.proto.fishing.FishingFireResponse();

            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }


    }
}
