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
    unsafe class com_maple_game_osee_proto_fishing_FishingPlayerInfoProto_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            MethodBase method;
            Type[] args;
            Type type = typeof(com.maple.game.osee.proto.fishing.FishingPlayerInfoProto);
            args = new Type[]{};
            method = type.GetMethod("get_playerId", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_playerId_0);
            args = new Type[]{typeof(System.Int64)};
            method = type.GetMethod("set_playerId", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, set_playerId_1);
            args = new Type[]{typeof(System.String)};
            method = type.GetMethod("set_name", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, set_name_2);
            args = new Type[]{typeof(System.Int32)};
            method = type.GetMethod("set_headIndex", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, set_headIndex_3);
            args = new Type[]{typeof(System.Int32)};
            method = type.GetMethod("set_viewIndex", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, set_viewIndex_4);
            args = new Type[]{typeof(System.Int32)};
            method = type.GetMethod("set_batteryLevel", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, set_batteryLevel_5);
            args = new Type[]{typeof(System.String)};
            method = type.GetMethod("set_diamond", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, set_diamond_6);
            args = new Type[]{typeof(System.Int64)};
            method = type.GetMethod("set_money", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, set_money_7);
            args = new Type[]{typeof(System.Int32)};
            method = type.GetMethod("set_seat", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, set_seat_8);
            args = new Type[]{typeof(System.Int32)};
            method = type.GetMethod("set_batteryMult", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, set_batteryMult_9);
            args = new Type[]{};
            method = type.GetMethod("get_seat", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_seat_10);

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
            com.maple.game.osee.proto.fishing.FishingPlayerInfoProto instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingPlayerInfoProto)typeof(com.maple.game.osee.proto.fishing.FishingPlayerInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
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
            com.maple.game.osee.proto.fishing.FishingPlayerInfoProto instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingPlayerInfoProto)typeof(com.maple.game.osee.proto.fishing.FishingPlayerInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.playerId = value;

            return __ret;
        }

        static StackObject* set_name_2(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 2);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.String @value = (System.String)typeof(System.String).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            com.maple.game.osee.proto.fishing.FishingPlayerInfoProto instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingPlayerInfoProto)typeof(com.maple.game.osee.proto.fishing.FishingPlayerInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.name = value;

            return __ret;
        }

        static StackObject* set_headIndex_3(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 2);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Int32 @value = ptr_of_this_method->Value;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            com.maple.game.osee.proto.fishing.FishingPlayerInfoProto instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingPlayerInfoProto)typeof(com.maple.game.osee.proto.fishing.FishingPlayerInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.headIndex = value;

            return __ret;
        }

        static StackObject* set_viewIndex_4(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 2);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Int32 @value = ptr_of_this_method->Value;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            com.maple.game.osee.proto.fishing.FishingPlayerInfoProto instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingPlayerInfoProto)typeof(com.maple.game.osee.proto.fishing.FishingPlayerInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.viewIndex = value;

            return __ret;
        }

        static StackObject* set_batteryLevel_5(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 2);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Int32 @value = ptr_of_this_method->Value;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            com.maple.game.osee.proto.fishing.FishingPlayerInfoProto instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingPlayerInfoProto)typeof(com.maple.game.osee.proto.fishing.FishingPlayerInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.batteryLevel = value;

            return __ret;
        }

        static StackObject* set_diamond_6(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 2);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.String @value = (System.String)typeof(System.String).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            com.maple.game.osee.proto.fishing.FishingPlayerInfoProto instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingPlayerInfoProto)typeof(com.maple.game.osee.proto.fishing.FishingPlayerInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.diamond = value;

            return __ret;
        }

        static StackObject* set_money_7(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 2);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Int64 @value = *(long*)&ptr_of_this_method->Value;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            com.maple.game.osee.proto.fishing.FishingPlayerInfoProto instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingPlayerInfoProto)typeof(com.maple.game.osee.proto.fishing.FishingPlayerInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.money = value;

            return __ret;
        }

        static StackObject* set_seat_8(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 2);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Int32 @value = ptr_of_this_method->Value;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            com.maple.game.osee.proto.fishing.FishingPlayerInfoProto instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingPlayerInfoProto)typeof(com.maple.game.osee.proto.fishing.FishingPlayerInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.seat = value;

            return __ret;
        }

        static StackObject* set_batteryMult_9(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 2);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Int32 @value = ptr_of_this_method->Value;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            com.maple.game.osee.proto.fishing.FishingPlayerInfoProto instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingPlayerInfoProto)typeof(com.maple.game.osee.proto.fishing.FishingPlayerInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.batteryMult = value;

            return __ret;
        }

        static StackObject* get_seat_10(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.fishing.FishingPlayerInfoProto instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingPlayerInfoProto)typeof(com.maple.game.osee.proto.fishing.FishingPlayerInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.seat;

            __ret->ObjectType = ObjectTypes.Integer;
            __ret->Value = result_of_this_method;
            return __ret + 1;
        }


        static StackObject* Ctor_0(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* __ret = ILIntepreter.Minus(__esp, 0);

            var result_of_this_method = new com.maple.game.osee.proto.fishing.FishingPlayerInfoProto();

            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }


    }
}
