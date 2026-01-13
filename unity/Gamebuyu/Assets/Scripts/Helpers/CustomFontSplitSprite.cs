
using UnityEngine;
using System.Collections;
using System.Xml;
using System;
using UnityEditor;
using System.Collections.Generic;

//struct tmpFont
//{ 
//    public int id;
//    public int x;
//    public int y;
//    public int width;
//    public int height;
//    public int xoffset;
//    public int yoffset;
//    public int xadvance;
//    public int page;
//    public int chnl;
//};
public class CustomFontSplitSprite : MonoBehaviour 
{
    public Font font;
    public Texture BigTuji; 
    public List<Sprite> tmpList;
    public int advance=10; 
    // 离文本框上面的距离]
    [Header("离文本框上面的距离")]
    public int jlminY = -20; 
    private void OnEnable()   
    {
        Debug.LogError("CustomFontSplitSprite");
        ArrayList characterInfoList = new ArrayList();
        for (int i = 0; i < tmpList.Count; i++)
        {
            CharacterInfo info = new CharacterInfo();
            Rect uv = new Rect();
            //uv 的x y从左下角开始
            uv.x = tmpList[i].textureRect.x / BigTuji.width;//(int)tmpList[i].textureRect.x;
            uv.y = (tmpList[i].textureRect.y / BigTuji.height); //(int)tmpList[i].textureRect.y;
            //uv 长宽
            uv.width = tmpList[i].textureRect.width / BigTuji.width; // (int)tmpList[i].textureRect.width;
            uv.height = tmpList[i].textureRect.height / BigTuji.height;  //(int)tmpList[i].textureRect.height;

            uv.xMin = tmpList[i].textureRect.xMin / BigTuji.width;
            uv.yMin = tmpList[i].textureRect.yMin / BigTuji.height;

            uv.xMax = tmpList[i].textureRect.xMax / BigTuji.width;
            uv.yMax = tmpList[i].textureRect.yMax / BigTuji.height;

            //赋值
            info.index = int.Parse(tmpList[i].name);
            info.uvBottomLeft = new Vector2(uv.xMin, uv.yMin);
            info.uvBottomRight = new Vector2(uv.xMax, uv.yMin);
            info.uvTopLeft = new Vector2(uv.xMin, uv.yMax);
            info.uvTopRight = new Vector2(uv.xMax, uv.yMax);
            //info.minX = 0;// (int)tmpList[i].textureRect.xMin;
            //info.maxX = 0;//(int)tmpList[i].textureRect.xMax;
            //info.minY = 0;// (int)tmpList[i].textureRect.yMin;
            //info.maxY = 0;// (int)tmpList[i].textureRect.yMax;
            //info.minX = (int)tmpList[i].textureRect.xMin;
            //info.maxX = (int)tmpList[i].textureRect.xMax;
            //info.minY = (int)tmpList[i].textureRect.yMin;

            info.advance = (int)tmpList[i].textureRect.width+ advance;//
             // info.advance = advance;
           //这个即是显示面板的  vert的W 和 H
            info.minY= jlminY;
            info.glyphWidth = (int)tmpList[i].textureRect.width;
            info.glyphHeight = (int)tmpList[i].textureRect.height;

            characterInfoList.Add(info);
        }
        font.characterInfo = characterInfoList.ToArray(typeof(CharacterInfo)) as CharacterInfo[];

    }
}