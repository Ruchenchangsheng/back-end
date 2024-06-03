$(document).ready(function () {
    var vue = new Vue({
        el: ".register",
        data: {
            uid: "",
            username: "",
            password: "",
            realName: "",
            gender: "",
            age: "",
            phone: "",
            email: "",
            role: "3",
            method: "post",
        },
        methods: {
            JustDoIt() {
                setTimeout(() => {
                    axios({
                        method: vue.method,
                        url: "/user/crud",
                        data: {
                            uid: parseInt(vue.uid),
                            username: vue.username,
                            password: vue.password,
                            realName: vue.realName,
                            gender: vue.gender,
                            age: vue.age == "无" ? null : parseInt(vue.age),
                            phone: vue.phone,
                            email: vue.email,
                            rid: vue.role,
                        },
                    })
                        .then((response) => {
                            var result = response.data;
                            alert(result.message);
                            if (result.code == 200) {
                                window.location.reload();
                            }
                        })
                        .catch((error) => {
                            alert("请求出错：" + error);
                        });
                }, 400);
            },
        },
    });

    $("#add").click(function () {
        $(".register h2").text("添加用户");
        $(".input_hidden").css("display", "none");
        $(".input_pwd").css("display", "inline-block");
        vue.uid = "";
        vue.username = "";
        vue.password = "";
        vue.email = "";
        vue.role = "3";
        vue.method = "post";
        $(".mask").fadeIn();
        $(".register")
            .css({
                display: "block",
                opacity: 0,
                marginTop: "-100px",
            })
            .animate(
                {
                    opacity: 1,
                    marginTop: "0",
                },
                350
            );
    });

    $("#cancel").click(function () {
        $(".mask").fadeOut();
        $(".register").animate(
            {
                opacity: 0,
                marginTop: "-100px",
            },
            350,
            function () {
                $(this).css("display", "none");
            }
        );
    });

    $("#submit").click(function (e) {
        e.preventDefault();
        $(".mask").fadeOut();
        $(".register").animate({ top: "-60%", opacity: "1" }, 350, "swing");
    });

    $(".modify").click(function () {
        var texts = [];
        $(this)
            .parent()
            .siblings()
            .each(function () {
                texts.push($(this).text());
            });
        $(".register h2").text("编辑用户");
        $(".input_hidden").css("display", "inline-block");
        $(".input_pwd").css("display", "none");
        vue.uid = texts[0];
        vue.username = texts[1];
        vue.password = null;
        vue.realName = texts[2] == "无" ? "" : texts[2];
        vue.gender = texts[3] == "无" ? "" : texts[3];
        vue.age = texts[4] == "无" ? "" : texts[4];
        vue.phone = texts[5] == "无" ? "" : texts[5];
        vue.email = texts[6];
        vue.role = texts[7] == "客户" ? "3" : "2";
        vue.method = "put";
        $(".mask").fadeIn();
        $(".register")
            .css({
                display: "block",
                opacity: 0,
                marginTop: "-100px",
            })
            .animate(
                {
                    opacity: 1,
                    marginTop: "0",
                },
                350
            );
    });

    $(".delete").click(function () {
        if (!confirm("确定要删除吗？")) {
            return;
        }
        $.ajax({
            url: "/user/crud/" + event.target.id,
            type: "DELETE",
            data: {},
            success: function (data) {
                alert(data.message);
                if (data.code == 200) {
                    window.location.reload();
                }
            },
            error: function (textStatus, errorThrown) {
                alert("请求出错：" + textStatus + "，" + errorThrown);
            },
        });
    });

    $(".disabled").click(function () {
        alert("你无法编辑管理员用户！");
    });
});
