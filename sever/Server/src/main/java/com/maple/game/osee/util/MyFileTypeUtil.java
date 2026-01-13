package com.maple.game.osee.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.List;

public class MyFileTypeUtil {

    // 图片文件集合
    public static final List<String> IMG_FILE_TYPE_LIST =
        CollUtil.newArrayList(ImgUtil.IMAGE_TYPE_JPG, ImgUtil.IMAGE_TYPE_JPEG, ImgUtil.IMAGE_TYPE_PNG);

    /**
     * 获取文件类型（不含点）：读取文件头部字节，获取文件类型，如果没有匹配上，则返回 null
     */
    @Nullable
    public static String getType(InputStream inputStream, String fileName) {

        String typeName = FileTypeUtil.getType(inputStream);

        IoUtil.close(inputStream); // 这里直接关闭流，因为这个 流已经不完整了

        if ("xls".equals(typeName)) {
            // xls、doc、msi的头一样，使用扩展名辅助判断
            final String extName = FileUtil.extName(fileName);
            if ("doc".equalsIgnoreCase(extName)) {
                typeName = "doc";
            } else if ("msi".equalsIgnoreCase(extName)) {
                typeName = "msi";
            }
        } else if ("zip".equals(typeName)) {
            // zip可能为docx、xlsx、pptx、jar、war、ofd等格式，扩展名辅助判断
            final String extName = FileUtil.extName(fileName);
            if ("docx".equalsIgnoreCase(extName)) {
                typeName = "docx";
            } else if ("xlsx".equalsIgnoreCase(extName)) {
                typeName = "xlsx";
            } else if ("pptx".equalsIgnoreCase(extName)) {
                typeName = "pptx";
            } else if ("jar".equalsIgnoreCase(extName)) {
                typeName = "jar";
            } else if ("war".equalsIgnoreCase(extName)) {
                typeName = "war";
            } else if ("ofd".equalsIgnoreCase(extName)) {
                typeName = "ofd";
            }
        } else if ("jar".equals(typeName)) {
            // wps编辑过的 .xlsx文件与 .jar的开头相同，通过扩展名判断
            final String extName = FileUtil.extName(fileName);
            if ("xlsx".equalsIgnoreCase(extName)) {
                typeName = "xlsx";
            } else if ("docx".equalsIgnoreCase(extName)) {
                typeName = "docx";
            }
        }

        return typeName;

    }

}
