using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using ProtoBuf;
using System.IO;

namespace NetLib
{
    public abstract class NetHelper
    {
        public static byte[] SerializeObject(IExtensible obj)
        {
            try
            {
                Stream stream = new MemoryStream();
                Serializer.Serialize(stream, obj);
                byte[] results = new byte[stream.Length];
                stream.Position = 0;
                stream.Read(results, 0, results.Length);

                return results;
            }
            catch (Exception e)
            {
                DebugSystem.Log(string.Format("SerializeObject Exception!error:{0}", e.ToString()));
            }

            return null;
        }
        public static T DeSerializeBytes<T>(byte[] data)
        {
            try
            {
                Stream stream = new MemoryStream(data);
                return Serializer.Deserialize<T>(stream);
            }
            catch (Exception e)
            {
                DebugSystem.Log(string.Format("DeSerializeBytes Exception!error:{0}", e.ToString()));
            }
            return default(T);
        }
        public static object DeSerializeBytes(byte[] data, Type type)
        {
            try
            {
                Stream stream = new MemoryStream(data);
                //ProtosSerializer protosSerializer = new ProtosSerializer();
                //return protosSerializer.Deserialize(stream, null, type);
                return Serializer.Deserialize(type, stream);
            }
            catch (Exception e)
            {
                DebugSystem.Log(string.Format("DeSerializeBytes Exception!error:{0}", e.ToString()));
            }
            return null;
        }
        public static byte[] IntToBytes(int value)
        {
            byte[] src = new byte[4];
            src[0] = (byte)((value >> 24) & 0xFF);
            src[1] = (byte)((value >> 16) & 0xFF);
            src[2] = (byte)((value >> 8) & 0xFF);
            src[3] = (byte)((value) & 0xFF);
            return src;
        }

        public static int BytesToInt(byte[] value)
        {
            return (((value[0] & 0x000000FF) << 24) | ((value[1] & 0x000000FF) << 16) | ((value[2] & 0x000000FF) << 8) | ((value[3] & 0x000000FF)));
        }
    }
}
