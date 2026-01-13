using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Runtime.Serialization.Formatters.Binary;
using UnityEngine;

public static class UserInfo{
    public static PlayerUserData playerData;///<玩家对象

    private static string path;///<文件的路径

    // Use this for initialization
    //void Awake()
    //{
    //    //在游戏刚刚运行时，根据平台，选择好对应的路径
    //    SetPath();
    //}
    public static PlayerAccount tmpPlayer;//临时记录
    //读取玩家的数据
    public static PlayerUserData LoadPlayerData()
    {
        SetPath();
        //如果路径上有文件，就读取文件
        if (File.Exists(path))
        {
            //读取数据
            BinaryFormatter bf = new BinaryFormatter();
            FileStream file = File.Open(path, FileMode.Open);
            playerData = (PlayerUserData)bf.Deserialize(file);
            file.Close();
        }
        //如果没有文件，就new出一个PlayerData
        else
        {
            playerData = new PlayerUserData();
        }

        return playerData;
    }

    //保存玩家的数据
    public static void SavePlayerData()
    {
        SetPath();
        //保存数据      
        BinaryFormatter bf = new BinaryFormatter();
        if (File.Exists(path))
        {
            File.Delete(path);
        }
        FileStream file = File.Create(path);
        bf.Serialize(file, playerData);
        file.Close();

    }

    //设置文件的路径，在手机上运行时Application.persistentDataPath这个路径才是可以读写的路径
    public static void SetPath()
    {
        //安卓平台
        if (Application.platform == RuntimePlatform.Android)
        {
            path = Application.persistentDataPath + "/playerData.gd";
        }
        //windows编辑器
        else if (Application.platform == RuntimePlatform.WindowsEditor)
        {
            //path = Application.persistentDataPath + "/playerData.gd";
            path = Application.persistentDataPath + "/playerData.gd";
        }
        else if (Application.platform == RuntimePlatform.WindowsPlayer)
        {
            path = Application.persistentDataPath + "/playerData.gd";
        }
        else if (Application.platform == RuntimePlatform.IPhonePlayer)
        {
            path = Application.persistentDataPath + "/playerData.gd";
        }
        else if (Application.platform == RuntimePlatform.OSXEditor)
        {
            path = Application.persistentDataPath + "/playerData.gd";
        }
    }
}
//玩家所有账户
[System.Serializable]
public struct PlayerAccount 
{
    public string Account;///账户
    public string PassWorld;///密码
}

//玩家数据类
[System.Serializable]
public class PlayerUserData 
{

    //玩家登陆状态
    public int nState = 0;//0 游客  1 账号登陆 昵称登陆 2微信登陆

    public List<PlayerAccount> list_Account = new List<PlayerAccount>();///所有账户
    //构造函数
    public PlayerUserData() 
    {
        PlayerAccount varAccount = new PlayerAccount();
       // varAccount.Account = "";
        //varAccount.PassWorld = "";
        nState = 0;
        //list_Account.Add(varAccount); 
    }
}
 