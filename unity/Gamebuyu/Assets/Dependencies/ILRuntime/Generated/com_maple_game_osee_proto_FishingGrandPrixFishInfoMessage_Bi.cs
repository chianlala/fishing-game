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
    unsafe class com_maple_game_osee_proto_FishingGrandPrixFishInfoMessage_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            MethodBase method;
            Type[] args;
            Type type = typeof(com.maple.game.osee.proto.FishingGrandPrixFishInfoMessage);
            args = new Type[]{};
            method = type.GetMethod("get_id", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_id_0);
            args = new Type[]{};
            method = type.GetMethod("get_fishId", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_fishId_1);
            args = new Type[]{};
            method = type.GetMethod("get_clientLifeTime", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_clientLifeTime_2);
            args = new Type[]{typeof(System.Single)};
            method = type.GetMethod("set_clientLifeTime", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, set_clientLifeTime_3);
            args = new Type[]{};
            method = type.GetMethod("get_routeId", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_routeId_4);
            args = new Type[]{};
            method = type.GetMethod("get_createTime", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_createTime_5);


        }


        static StackObject* get_id_0(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.FishingGrandPrixFishInfoMessage instance_of_this_method = (com.maple.game.osee.proto.FishingGrandPrixFishInfoMessage)typeof(com.maple.game.osee.proto.FishingGrandPrixFishInfoMessage).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.id;

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
            com.maple.game.osee.proto.FishingGrandPrixFishInfoMessage instance_of_this_method = (com.maple.game.osee.proto.FishingGrandPrixFishInfoMessage)typeof(com.maple.game.osee.proto.FishingGrandPrixFishInfoMessage).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.fishId;

            __ret->ObjectType = ObjectTypes.Long;
            *(long*)&__ret->Value = result_of_this_method;
            return __ret + 1;
        }

        static StackObject* get_clientLifeTime_2(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.FishingGrandPrixFishInfoMessage instance_of_this_method = (com.maple.game.osee.proto.FishingGrandPrixFishInfoMessage)typeof(com.maple.game.osee.proto.FishingGrandPrixFishInfoMessage).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.clientLifeTime;

            __ret->ObjectType = ObjectTypes.Float;
            *(float*)&__ret->Value = result_of_this_method;
            return __ret + 1;
        }

        static StackObject* set_clientLifeTime_3(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 2);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Single @value = *(float*)&ptr_of_this_method->Value;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            com.maple.game.osee.proto.FishingGrandPrixFishInfoMessage instance_of_this_method = (com.maple.game.osee.proto.FishingGrandPrixFishInfoMessage)typeof(com.maple.game.osee.proto.FishingGrandPrixFishInfoMessage).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.clientLifeTime = value;

            return __ret;
        }

        static StackObject* get_routeId_4(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.FishingGrandPrixFishInfoMessage instance_of_this_method = (com.maple.game.osee.proto.FishingGrandPrixFishInfoMessage)typeof(com.maple.game.osee.proto.FishingGrandPrixFishInfoMessage).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.routeId;

            __ret->ObjectType = ObjectTypes.Long;
            *(long*)&__ret->Value = result_of_this_method;
            return __ret + 1;
        }

        static StackObject* get_createTime_5(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.FishingGrandPrixFishInfoMessage instance_of_this_method = (com.maple.game.osee.proto.FishingGrandPrixFishInfoMessage)typeof(com.maple.game.osee.proto.FishingGrandPrixFishInfoMessage).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.createTime;

            __ret->ObjectType = ObjectTypes.Long;
            *(long*)&__ret->Value = result_of_this_method;
            return __ret + 1;
        }



    }
}
