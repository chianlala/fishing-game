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
    unsafe class com_maple_game_osee_proto_fishing_FishingChallengeCatchSpecialFishRequest_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            MethodBase method;
            Type[] args;
            Type type = typeof(com.maple.game.osee.proto.fishing.FishingChallengeCatchSpecialFishRequest);
            args = new Type[]{};
            method = type.GetMethod("get_fishIds", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_fishIds_0);
            args = new Type[]{typeof(System.Int64)};
            method = type.GetMethod("set_specialFishId", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, set_specialFishId_1);
            args = new Type[]{typeof(System.Int64)};
            method = type.GetMethod("set_playerId", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, set_playerId_2);

            args = new Type[]{};
            method = type.GetConstructor(flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, Ctor_0);

        }


        static StackObject* get_fishIds_0(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.fishing.FishingChallengeCatchSpecialFishRequest instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingChallengeCatchSpecialFishRequest)typeof(com.maple.game.osee.proto.fishing.FishingChallengeCatchSpecialFishRequest).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.fishIds;

            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static StackObject* set_specialFishId_1(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 2);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Int64 @value = *(long*)&ptr_of_this_method->Value;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            com.maple.game.osee.proto.fishing.FishingChallengeCatchSpecialFishRequest instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingChallengeCatchSpecialFishRequest)typeof(com.maple.game.osee.proto.fishing.FishingChallengeCatchSpecialFishRequest).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.specialFishId = value;

            return __ret;
        }

        static StackObject* set_playerId_2(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 2);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Int64 @value = *(long*)&ptr_of_this_method->Value;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            com.maple.game.osee.proto.fishing.FishingChallengeCatchSpecialFishRequest instance_of_this_method = (com.maple.game.osee.proto.fishing.FishingChallengeCatchSpecialFishRequest)typeof(com.maple.game.osee.proto.fishing.FishingChallengeCatchSpecialFishRequest).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.playerId = value;

            return __ret;
        }


        static StackObject* Ctor_0(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* __ret = ILIntepreter.Minus(__esp, 0);

            var result_of_this_method = new com.maple.game.osee.proto.fishing.FishingChallengeCatchSpecialFishRequest();

            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }


    }
}
