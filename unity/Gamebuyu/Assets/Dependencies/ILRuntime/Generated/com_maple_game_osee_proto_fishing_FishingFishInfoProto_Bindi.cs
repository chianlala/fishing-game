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
    unsafe class com_maple_game_osee_proto_fishing_FishingFishInfoProto_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            MethodBase method;
            Type[] args;
            Type type = typeof(com.maple.game.osee.proto.fishing.FishingFishInfoProto);
            args = new Type[]{};
            method = type.GetMethod("get_routeId", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_routeId_0);
            args = new Type[]{};
            method = type.GetMethod("get_fishId", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_fishId_1);
            args = new Type[]{};
            method = type.GetMethod("get_id", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_id_2);
            args = new Type[]{};
            method = type.GetMethod("get_clientLifeTime", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_clientLifeTime_3);
            args = new Type[]{};
            method = type.GetMethod("get_createTime", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_createTime_4);
            args = new Type[]{typeof(System.Single)};
            method = type.GetMethod("set_clientLifeTime", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, set_clientLifeTime_5);
            args = new Type[]{typeof(System.Int64)};
            method = type.GetMethod("set_id", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, set_id_6);
            args = new Type[]{typeof(System.Int64)};
            method = type.GetMethod("set_fishId", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, set_fishId_7);
            args = new Type[]{typeof(System.Int64)};
            method = type.GetMethod("set_routeId", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, set_routeId_8);
            args = new Type[]{typeof(System.Int64)};
            method = type.GetMethod("set_createTime", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, set_createTime_9);
            args = new Type[]{typeof(System.Int64)};
            method = type.GetMethod("set_isBossBulge", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, set_isBossBulge_10);
            args = new Type[]{};
            method = type.GetMethod("get_isBossBulge", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_isBossBulge_11);

            args = new Type[]{};
            method = type.GetConstructor(flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, Ctor_0);

        }


        static StackObject* get_routeId_0(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.fishing.FishingFishInfoProto instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingFishInfoProto)typeof(com.maple.game.osee.proto.fishing.FishingFishInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.routeId;

            __ret->ObjectType = ObjectTypes.Long;
            *(long*)&__ret->Value = result_of_this_method;
            return __ret + 1;
        }

        static StackObject* get_fishId_1(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.fishing.FishingFishInfoProto instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingFishInfoProto)typeof(com.maple.game.osee.proto.fishing.FishingFishInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.fishId;

            __ret->ObjectType = ObjectTypes.Long;
            *(long*)&__ret->Value = result_of_this_method;
            return __ret + 1;
        }

        static StackObject* get_id_2(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.fishing.FishingFishInfoProto instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingFishInfoProto)typeof(com.maple.game.osee.proto.fishing.FishingFishInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.id;

            __ret->ObjectType = ObjectTypes.Long;
            *(long*)&__ret->Value = result_of_this_method;
            return __ret + 1;
        }

        static StackObject* get_clientLifeTime_3(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.fishing.FishingFishInfoProto instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingFishInfoProto)typeof(com.maple.game.osee.proto.fishing.FishingFishInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.clientLifeTime;

            __ret->ObjectType = ObjectTypes.Float;
            *(float*)&__ret->Value = result_of_this_method;
            return __ret + 1;
        }

        static StackObject* get_createTime_4(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.fishing.FishingFishInfoProto instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingFishInfoProto)typeof(com.maple.game.osee.proto.fishing.FishingFishInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.createTime;

            __ret->ObjectType = ObjectTypes.Long;
            *(long*)&__ret->Value = result_of_this_method;
            return __ret + 1;
        }

        static StackObject* set_clientLifeTime_5(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 2);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Single @value = *(float*)&ptr_of_this_method->Value;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            com.maple.game.osee.proto.fishing.FishingFishInfoProto instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingFishInfoProto)typeof(com.maple.game.osee.proto.fishing.FishingFishInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.clientLifeTime = value;

            return __ret;
        }

        static StackObject* set_id_6(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 2);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Int64 @value = *(long*)&ptr_of_this_method->Value;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            com.maple.game.osee.proto.fishing.FishingFishInfoProto instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingFishInfoProto)typeof(com.maple.game.osee.proto.fishing.FishingFishInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.id = value;

            return __ret;
        }

        static StackObject* set_fishId_7(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 2);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Int64 @value = *(long*)&ptr_of_this_method->Value;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            com.maple.game.osee.proto.fishing.FishingFishInfoProto instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingFishInfoProto)typeof(com.maple.game.osee.proto.fishing.FishingFishInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.fishId = value;

            return __ret;
        }

        static StackObject* set_routeId_8(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 2);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Int64 @value = *(long*)&ptr_of_this_method->Value;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            com.maple.game.osee.proto.fishing.FishingFishInfoProto instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingFishInfoProto)typeof(com.maple.game.osee.proto.fishing.FishingFishInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.routeId = value;

            return __ret;
        }

        static StackObject* set_createTime_9(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 2);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Int64 @value = *(long*)&ptr_of_this_method->Value;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            com.maple.game.osee.proto.fishing.FishingFishInfoProto instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingFishInfoProto)typeof(com.maple.game.osee.proto.fishing.FishingFishInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.createTime = value;

            return __ret;
        }

        static StackObject* set_isBossBulge_10(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 2);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Int64 @value = *(long*)&ptr_of_this_method->Value;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            com.maple.game.osee.proto.fishing.FishingFishInfoProto instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingFishInfoProto)typeof(com.maple.game.osee.proto.fishing.FishingFishInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.isBossBulge = value;

            return __ret;
        }

        static StackObject* get_isBossBulge_11(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.fishing.FishingFishInfoProto instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingFishInfoProto)typeof(com.maple.game.osee.proto.fishing.FishingFishInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.isBossBulge;

            __ret->ObjectType = ObjectTypes.Long;
            *(long*)&__ret->Value = result_of_this_method;
            return __ret + 1;
        }


        static StackObject* Ctor_0(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* __ret = ILIntepreter.Minus(__esp, 0);

            var result_of_this_method = new com.maple.game.osee.proto.fishing.FishingFishInfoProto();

            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }


    }
}
