/*!
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 * 项目自定义的公共JavaScript，可覆盖jeesite.js里的方法
 */

if (window.parent.window.$.InitI18next) {
    window.parent.window.$.InitI18next(window.$)
    window.$.InitI18next = window.parent.window.$.InitI18next
}

$(() => {

    let a = $('.select2-hidden-accessible > option')

    for (let i = 0; i < a.length; i++) {

        let innerHTML = a[i].innerHTML;

        if (String(innerHTML) !== '&nbsp;') {

            // console.log(innerHTML)

            let t = $.i18n.t("下拉选." + innerHTML);

            if (!String(t).startsWith("下拉选.")) {
                a[i].innerHTML = `${t}`
            }

        }

    }

    setTimeout(() => {

        let selectStr = ".tabpanel_mover > li > div[class=title]";

        let b = $(selectStr);

        handleSelect(b);

        let c = window.parent.window.$(selectStr);

        handleSelect(c, true);

    }, 100)

    function handleSelect(a, parentFlag = false) {

        for (let i = 0; i < a.length; i++) {

            let i18n = $(a[i]).attr("data-i18n");

            if (i18n) {
                continue
            }

            let innerHTML = a[i].innerHTML;

            if (innerHTML.startsWith("<i")) {

                innerHTML.match(/<i .*><\/i> (.*)/g)

                innerHTML = RegExp.$1

            }

            $(a[i]).attr("data-i18n", "菜单." + innerHTML)

        }

        if (parentFlag) {

            if (window.parent.window.$('[data-i18n]').localize) {
                window.parent.window.$('[data-i18n]').localize();
            }

        } else {

            if ($('[data-i18n]').localize) {
                $('[data-i18n]').localize();
            }

        }

    }

    if ($('[data-i18n]').localize) {
        $('[data-i18n]').localize();
    }

})
