package com.jeesite.modules.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.comparator.LengthComparator;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.lang.RegexPool;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 国际化工具类
 */
@Data
public class MyI18nUtil {

    private static final LinkedHashSet<String> PRINTLN_SET = new LinkedHashSet<>();

    @Getter
    @AllArgsConstructor
    enum PathKeyEnum {

        用户列表(
            "E:\\Gitblit\\BaoYouQianGM\\src\\main\\resources\\views\\modules\\osee\\player\\playerList.html",
            "用户列表"), //

        充值明细(
            "E:\\Gitblit\\BaoYouQianGM\\src\\main\\resources\\views\\modules\\osee\\money\\recharge.html",
            "充值明细"), //

        扣除明细(
            "E:\\Gitblit\\BaoYouQianGM\\src\\main\\resources\\views\\modules\\osee\\money\\deduct.html",
            "扣除明细"), //

        账户明细(
            "E:\\Gitblit\\BaoYouQianGM\\src\\main\\resources\\views\\modules\\osee\\money\\account.html",
            "账户明细"), //

        游戏记录(
            "E:\\Gitblit\\BaoYouQianGM\\src\\main\\resources\\views\\modules\\osee\\money\\fishing.html",
            "游戏记录"), //

        全服资产记录(
            "E:\\Gitblit\\BaoYouQianGM\\src\\main\\resources\\views\\modules\\osee\\money\\changeAll.html",
            "全服资产记录"), //

        调节记录(
            "E:\\Gitblit\\BaoYouQianGM\\src\\main\\resources\\views\\modules\\osee\\money\\userControllerLog.html",
            "调节记录"), //

        邮件记录(
            "E:\\Gitblit\\BaoYouQianGM\\src\\main\\resources\\views\\modules\\osee\\money\\mail.html",
            "邮件记录"), //

        代理邮件记录(
            "E:\\Gitblit\\BaoYouQianGM\\src\\main\\resources\\views\\modules\\osee\\game\\agentMailView.html",
            "代理邮件记录"), //

        游走字幕列表(
            "E:\\Gitblit\\BaoYouQianGM\\src\\main\\resources\\views\\modules\\osee\\game\\subtitle.html",
            "游走字幕列表"), //

        游戏公告列表(
            "E:\\Gitblit\\BaoYouQianGM\\src\\main\\resources\\views\\modules\\osee\\game\\notice.html",
            "游戏公告列表"), //

        角色管理(
            "E:\\Gitblit\\BaoYouQianGM\\src\\main\\resources\\views\\modules\\sys\\roleList.html",
            "角色管理"), //

        二级管理员(
            "E:\\Gitblit\\BaoYouQianGM\\src\\main\\resources\\views\\modules\\sys\\user\\secAdminList.html",
            "二级管理员"), //

        系统管理员(
            "E:\\Gitblit\\BaoYouQianGM\\src\\main\\resources\\views\\modules\\sys\\user\\corpAdminList.html",
            "系统管理员"), //

        用户管理(
            "E:\\Gitblit\\BaoYouQianGM\\src\\main\\resources\\views\\modules\\sys\\user\\empUserList.html",
            "用户管理"), //

        反馈记录(
            "E:\\Gitblit\\BaoYouQianGM\\src\\main\\resources\\views\\modules\\osee\\game\\feedBack.html",
            "反馈记录"), //

        ;

        private final String path;

        private final String key;

    }

    /**
     * 不在前面添加 key的字符串集合
     */
    private static final Set<String> NOT_ADD_KEY_STR_SET = CollUtil.newHashSet("隐藏", "查询",
        "重置", "新增");

    public static void main(String[] args) {

        for (PathKeyEnum value : PathKeyEnum.values()) {

            // 执行
            doMain(value.getPath(), value.getKey());

        }

    }

    public static final Pattern ONE = Pattern.compile(":[ ]?'\\$\\{text\\(\"(.*)\"\\)}'");

    public static final Pattern TWO = Pattern.compile("<a (.*)title=\"\\$\\{text\\('(.*)'\\)}\"");

    public static final Pattern THREE = Pattern.compile("title=\"\\$\\{text\\(\"(.*)\"\\)}\"");

    public static final Pattern FOUR =
        Pattern.compile("<label class=\"control-label\">\\$\\{text\\('(.*)'\\)}：</label>");

    public static final Pattern FIVE =
        Pattern.compile(
            "<button type=\"submit\" class=\"btn btn-primary btn-sm\">\\$\\{text\\('查询'\\)}</button>");

    public static final Pattern SIX =
        Pattern.compile(
            "<button type=\"reset\" class=\"btn btn-default btn-sm\">\\$\\{text\\('重置'\\)}</button>");

    /**
     * 执行
     */
    private static void doMain(String path, String key) {

        PRINTLN_SET.clear();

        File file = FileUtil.newFile(path);

        String readUtf8String = FileUtil.readUtf8String(file);

        readUtf8String = replaceAll(readUtf8String, ONE, ":`${jsValue('$.i18n.t(\"$1\")')}`", 1,
            key);

        readUtf8String = replaceAll(readUtf8String, TWO, "<a $1data-i18n=\"$2\"", 2, key);

        readUtf8String =
            replaceAll(readUtf8String, THREE, "title=\"' + `${jsValue('$.i18n.t(\"$1\")')}` + '\"",
                1, key);

        readUtf8String =
            replaceAll(readUtf8String, FOUR,
                "<label class=\"control-label\" data-i18n=\"$1\">${text('($1)')}：</label>",
                1, key);

        readUtf8String = replaceAll(readUtf8String, FIVE,
            "<button type=\"submit\" class=\"btn btn-primary btn-sm\" data-i18n=\"查询\">${text('查询')}</button>",
            null,
            key);

        readUtf8String = replaceAll(readUtf8String, SIX,
            "<button type=\"reset\" class=\"btn btn-default btn-sm\" data-i18n=\"重置\">${text('重置')}</button>",
            null,
            key);

        for (String item : PRINTLN_SET) {
            if (RegexPool.WORD.matches(item)) {
                System.err.println(item); // 如果是：英文，则打印为红色
            } else {
                System.out.println(item);
            }
        }

        FileUtil.writeUtf8String(readUtf8String, file);

    }

    public static String replaceAll(CharSequence content, Pattern pattern,
        String replacementTemplate,
        Integer printlnGroup, String key) {
        if (StrUtil.isEmpty(content)) {
            return StrUtil.str(content);
        }

        final Matcher matcher = pattern.matcher(content);
        boolean result = matcher.find();
        if (result) {
            final Set<String> varNums = ReUtil.findAll(PatternPool.GROUP_VAR, replacementTemplate,
                1,
                new TreeSet<>(LengthComparator.INSTANCE.reversed()));
            final StringBuffer sb = new StringBuffer();
            do {
                String replacement = replacementTemplate;
                for (final String var : varNums) {
                    final int group = Integer.parseInt(var);
                    String groupStr = matcher.group(group);

                    if (printlnGroup != null && printlnGroup == group) { // 改动的地方 ↓

                        if (!NOT_ADD_KEY_STR_SET.contains(groupStr)) {

                            PRINTLN_SET.add(groupStr);

                            groupStr = key + "." + groupStr;

                        }

                    } // 改动的地方 ↑

                    replacement = replacement.replace("$" + var, groupStr);
                }
                matcher.appendReplacement(sb, ReUtil.escape(replacement));
                result = matcher.find();
            } while (result);
            matcher.appendTail(sb);
            return sb.toString();
        }
        return StrUtil.str(content);
    }

}
