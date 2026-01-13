
using UnityEngine;
using System.Collections;
using System.Xml;
using System;
using UnityEditor;
using System.Collections.Generic;

struct tmpFont
{ 
    public int id;
    public int x;
    public int y;
    public int width;
    public int height;
    public int xoffset;
    public int yoffset;
    public int xadvance;
    public int page;
    public int chnl;
};
public class CustomFontImportor : MonoBehaviour
{
    public Font font;
    public TextAsset textAsset;
    int totalWidth=0;
    int totalHeight=0;
    private void Awake()
    {
        if (font == null || textAsset == null)
        {
            Debug.LogError("请设置font和textAsset.");
            return;
        }
        string[] lines = textAsset.text.Split("\n"[0]);
        List<tmpFont> tmpList = new List<tmpFont>();
        for (int i = 0; i < lines.Length; i++)
        {
            string[] lines2 = lines[i].Split(" "[0]);
            if (i <= 4)
            {
                Debug.Log(textAsset.text);
                for (int j = 0; j < lines2.Length; j++)
                {
                    if (lines2[j].Contains("scaleW="))
                    {
                        totalWidth = int.Parse(lines2[j].Replace("scaleW=", ""));
                    }
                    if (lines2[j].Contains("scaleH="))
                    {
                        totalHeight = int.Parse(lines2[j].Replace("scaleH=", ""));
                    }
                }
            }

            if (i > 4)
            {
                var varFont = new tmpFont();
                for (int j = 0; j < lines2.Length; j++)
                {
                    Debug.Log(lines2[j]);

                    if (lines2[j].Contains("id="))
                    {
                        varFont.id = int.Parse(lines2[j].Replace("id=", ""));
                    }
                    if (lines2[j].Contains("x="))
                    {
                        varFont.x = int.Parse(lines2[j].Replace("x=", ""));

                    }
                    if (lines2[j].Contains("y="))
                    {
                        varFont.y = int.Parse(lines2[j].Replace("y=", ""));

                    }
                    if (lines2[j].Contains("height="))
                    {
                        varFont.height = int.Parse(lines2[j].Replace("height=", ""));
                    }
                    if (lines2[j].Contains("width="))
                    {
                        varFont.width = int.Parse(lines2[j].Replace("width=", ""));
                    }
                    if (lines2[j].Contains("xoffset="))
                    {
                        varFont.xoffset = int.Parse(lines2[j].Replace("xoffset=", ""));
                    }
                    if (lines2[j].Contains("yoffset="))
                    {
                        varFont.yoffset = int.Parse(lines2[j].Replace("yoffset=", ""));
                    }
                    if (lines2[j].Contains("xadvance="))
                    {
                        varFont.xadvance = int.Parse(lines2[j].Replace("xadvance=", ""));
                    }
                    if (lines2[j].Contains("page="))
                    {
                        varFont.page = int.Parse(lines2[j].Replace("page=", ""));
                    }
                    if (lines2[j].Contains("chnl="))
                    {
                        varFont.xadvance = int.Parse(lines2[j].Replace("chnl=", ""));
                    }
                }
                tmpList.Add(varFont);
            }
        }
        ArrayList characterInfoList = new ArrayList();

        for (int i = 0; i < tmpList.Count; i++)
        {
            CharacterInfo info = new CharacterInfo();
            Rect uv = new Rect();
            uv.x = (float)tmpList[i].x / totalWidth;
            uv.y = (float)(totalHeight - tmpList[i].y - tmpList[i].height) / totalHeight;
            uv.width = (float)tmpList[i].width / totalWidth;
            uv.height = (float)tmpList[i].height / totalHeight;
            info.index = tmpList[i].id;
            info.uvBottomLeft = new Vector2(uv.xMin, uv.yMin);
            info.uvBottomRight = new Vector2(uv.xMax, uv.yMin);
            info.uvTopLeft = new Vector2(uv.xMin, uv.yMax);
            info.uvTopRight = new Vector2(uv.xMax, uv.yMax);
            info.minX = tmpList[i].xoffset;
            info.maxX = tmpList[i].xoffset + tmpList[i].width;
            info.minY = -tmpList[i].yoffset - tmpList[i].height;
            info.maxY = -tmpList[i].yoffset;
            info.advance = tmpList[i].xadvance;
            info.glyphWidth = tmpList[i].width;
            info.glyphHeight = tmpList[i].height;

            characterInfoList.Add(info);
        }
        font.characterInfo = characterInfoList.ToArray(typeof(CharacterInfo)) as CharacterInfo[];
        //Debug.Log("生成成功.");
    }
    //void Awake()
    //{
    //    if (font == null || textAsset == null)
    //    {
    //        Debug.LogError("请设置font和textAsset.");
    //        return;
    //    }
    //    // TextAsset textAsset0 = textAsset.text;
    //    // Debug.Log(textAsset.text);           //输出验证

    //    ArrayList characterInfoList = new ArrayList();

    //    string[] lines = textAsset.text.Split("\n"[0]);

    //    int totalWidth=0; //= Convert.ToInt32(xmlDocument["font"]["common"].Attributes["scaleW"].InnerText);
    //    int totalHeight=0; //= Convert.ToInt32(xmlDocument["font"]["common"].Attributes["scaleH"].InnerText);

    //    for (int i = 0; i < lines.Length; i++)
    //    {

    //        string[] lines2 = lines[i].Split(" "[0]);
    //        if (i <= 4)
    //        {
    //            for (int j = 0; j < lines2.Length; j++)
    //            {
    //                if (lines2[j].Contains("scaleW="))
    //                {
    //                    totalWidth = int.Parse(lines2[j].Replace("scaleW=", ""));
    //                }
    //                if (lines2[j].Contains("scaleH="))
    //                {
    //                    totalHeight = int.Parse(lines2[j].Replace("scaleH=", ""));
    //                }
    //            }
    //        }



    //        if (i>4)
    //        {
    //            CharacterInfo info = new CharacterInfo();
    //            Rect uv = new Rect();
    //            int y=0;
    //            int x = 0;
    //            int height = 0;
    //            int width = 0;
    //            int xoffset = 0;
    //            int yoffset = 0;
    //            int xadvance = 0;
    //            for (int j = 0; j < lines2.Length; j++)
    //            {
    //                Debug.Log(lines2[j]);

    //                if (lines2[j].Contains("id="))
    //                {
    //                    info.index =int.Parse(lines2[j].Replace("id=", ""));
    //                }
    //                if (lines2[j].Contains("x="))
    //                {
    //                    x = int.Parse(lines2[j].Replace("x=", ""));
    //                    uv.x = (float)x / totalWidth;
    //                }
    //                if (lines2[j].Contains("y="))
    //                {
    //                     y = int.Parse(lines2[j].Replace("y=", ""));
    //                    uv.y = (float)y / totalWidth;
    //                }
    //                if (lines2[j].Contains("height="))
    //                {
    //                     height = int.Parse(lines2[j].Replace("height=", ""));
    //                    uv.y = (float)(totalHeight - y - height) / totalHeight;
    //                    info.glyphHeight = height;
    //                }
    //                if (lines2[j].Contains("width="))
    //                {
    //                    width = int.Parse(lines2[j].Replace("width=", ""));
    //                    uv.width = (float)width / totalWidth;
    //                    info.glyphWidth = width;
    //                }
    //                if (lines2[j].Contains("xoffset="))
    //                {
    //                    xoffset = int.Parse(lines2[j].Replace("xoffset=", ""));
    //                    info.minX = xoffset;
    //                }
    //                if (lines2[j].Contains("yoffset="))
    //                {
    //                    yoffset = int.Parse(lines2[j].Replace("yoffset=", ""));
    //                    info.minX = yoffset;
    //                }
    //                if (lines2[j].Contains("xadvance="))
    //                {
    //                    xadvance = int.Parse(lines2[j].Replace("xadvance=", ""));
    //                    info.advance = xadvance;
    //                }
    //                //if (lines2[j].Contains("y=")) 
    //                //{
    //                //    int y = int.Parse(lines2[j].Replace("y=", ""));
    //                //    uv.y = (float)(totalHeight - y - height) / totalHeight;
    //                //}
    //                //int index = Convert.ToInt32(lines2[j].Attributes["id"].InnerText);
    //                //int x = Convert.ToInt32(lines2[j].Attributes["x"].InnerText);
    //                //int y = Convert.ToInt32(lines2[j].Attributes["y"].InnerText);
    //                //int width = Convert.ToInt32(lines2[j].Attributes["width"].InnerText);
    //                //int height = Convert.ToInt32(lines2[j].Attributes["height"].InnerText);
    //                //int xOffset = Convert.ToInt32(lines2[j].Attributes["xoffset"].InnerText);
    //                //int yOffset = Convert.ToInt32(lines2[j].Attributes["yoffset"].InnerText);
    //                //int xAdvance = Convert.ToInt32(lines2[j].Attributes["xadvance"].InnerText);

    //                //uv.x = (float)x / totalWidth;
    //                //uv.y = (float)(totalHeight - y - height) / totalHeight;
    //                //uv.width = (float)width / totalWidth;
    //                //uv.height = (float)height / totalHeight;
    //                //info.index = index;
    //                //info.uvBottomLeft = new Vector2(uv.xMin, uv.yMin);
    //                //info.uvBottomRight = new Vector2(uv.xMax, uv.yMin);
    //                //info.uvTopLeft = new Vector2(uv.xMin, uv.yMax);
    //                //info.uvTopRight = new Vector2(uv.xMax, uv.yMax);
    //                //info.minX = xOffset;
    //                //info.maxX = xOffset + width;
    //                //info.minY = -yOffset - height;
    //                //info.maxY = -yOffset;
    //                //info.advance = xAdvance;
    //                //info.glyphWidth = width;
    //                //info.glyphHeight = height;
    //            }

    //            characterInfoList.Add(info);
    //        }
    //    }

    //    font.characterInfo = characterInfoList.ToArray(typeof(CharacterInfo)) as CharacterInfo[];
    //    Debug.Log("生成成功.");
    //    //return;
    //    //XmlDocument xmlDocument = new XmlDocument();
    //    //xmlDocument.LoadXml(textAsset.text);


    //    //int totalWidth = Convert.ToInt32(xmlDocument["font"]["common"].Attributes["scaleW"].InnerText);
    //    //int totalHeight = Convert.ToInt32(xmlDocument["font"]["common"].Attributes["scaleH"].InnerText);

    //    //XmlElement xml = xmlDocument["font"]["chars"];
    //    //ArrayList characterInfoList = new ArrayList();


    //    //for (int i = 0; i < xml.ChildNodes.Count; ++i)
    //    //{
    //    //    XmlNode node = xml.ChildNodes[i];
    //    //    if (node.Attributes == null)
    //    //    {
    //    //        continue;
    //    //    }
    //    //    int index = Convert.ToInt32(node.Attributes["id"].InnerText);
    //    //    int x = Convert.ToInt32(node.Attributes["x"].InnerText);
    //    //    int y = Convert.ToInt32(node.Attributes["y"].InnerText);
    //    //    int width = Convert.ToInt32(node.Attributes["width"].InnerText);
    //    //    int height = Convert.ToInt32(node.Attributes["height"].InnerText);
    //    //    int xOffset = Convert.ToInt32(node.Attributes["xoffset"].InnerText);
    //    //    int yOffset = Convert.ToInt32(node.Attributes["yoffset"].InnerText);
    //    //    int xAdvance = Convert.ToInt32(node.Attributes["xadvance"].InnerText);
    //    //    CharacterInfo info = new CharacterInfo();
    //    //    Rect uv = new Rect();
    //    //    uv.x = (float)x / totalWidth;
    //    //    uv.y = (float)(totalHeight - y - height) / totalHeight;
    //    //    uv.width = (float)width / totalWidth;
    //    //    uv.height = (float)height / totalHeight;
    //    //    info.index = index;
    //    //    info.uvBottomLeft = new Vector2(uv.xMin, uv.yMin);
    //    //    info.uvBottomRight = new Vector2(uv.xMax, uv.yMin);
    //    //    info.uvTopLeft = new Vector2(uv.xMin, uv.yMax);
    //    //    info.uvTopRight = new Vector2(uv.xMax, uv.yMax);
    //    //    info.minX = xOffset;
    //    //    info.maxX = xOffset + width;
    //    //    info.minY = -yOffset - height;
    //    //    info.maxY = -yOffset;
    //    //    info.advance = xAdvance;
    //    //    info.glyphWidth = width;
    //    //    info.glyphHeight = height;
    //    //    characterInfoList.Add(info);
    //    //}
    //    //font.characterInfo = characterInfoList.ToArray(typeof(CharacterInfo)) as CharacterInfo[];
    //    //Debug.Log("生成成功.");
    //}
}