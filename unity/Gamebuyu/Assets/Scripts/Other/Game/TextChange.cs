using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class TextChange : MonoBehaviour
{
	// Start is called before the first frame update
	public Text mText;
	public bool m_IsSpace;
	void OnEnable()
    {
		//获取Text的文本内容
		string temp_content = mText.text;
		string temp_indent = "";
		//首行缩进的字符数
		int m_Text_indent = 2;
		for (int i = 0; i < m_Text_indent; i++)
		{
			temp_indent = string.Format("{0}{1}", temp_indent, "\u3000");
		}
		temp_content = string.Format("{0}{1}", temp_indent, mText.text);
		//处理空格
		if (m_IsSpace)
		{
			temp_content = temp_content.Replace(" ", "\u3000");
		}
		//首行缩进替换
		temp_content = temp_content.Replace("\n", "\n" + temp_indent);
		//重新设置Text文字
		mText.text = temp_content;
	}

  
}
