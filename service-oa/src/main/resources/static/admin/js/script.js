$(document).ready(function () {
    //点击高亮
    $(".menu a").click(function () {
        let href = $(this).attr("href");
        if (href != "javascript:;") {
            $(".menu a").removeClass("active");
            $(this).addClass("active");
        }
    });

    //菜单收展
    $('.menu .top-menu').click(function () {
        let sub_menu = $(this).next();
        let arrow = $(this).parent().find(".arrow");
        if (sub_menu.hasClass('unfolded')) {  // 折叠
            sub_menu.slideUp(250);
            arrow.animate({rotate: '0deg'},250);
            sub_menu.removeClass('unfolded');
        } else { // 展开
            sub_menu.slideDown(250);
            arrow.animate({rotate: '90deg'},250);
            sub_menu.addClass('unfolded');
        }
    })
});

function delCache() {
    sessionStorage.clear();
    localStorage.clear();
}
