$(document).ready(function () {

    var vue = new Vue({
        el: ".register",
        data: {
            hotelId: "",
            hotelName: "",
            managerId: "",
            address: "",
            phone: "",
            method: "post",
        },
        methods: {
            JustDoIt() {
                setTimeout(() => {
                    axios({
                        method: vue.method,
                        url: "/hotel/crud",
                        data: {
                            hotelId: vue.hotelId,
                            hotelName: vue.hotelName,
                            managerId: vue.managerId,
                            address: vue.address,
                            phone: vue.phone,
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
        window.location.href = "/hotel/modify/" + event.target.id;
    });

    $(".delete").click(function () {
        if (!confirm("确定要删除吗？")) {
            return;
        }
        $.ajax({
            url: "/hotel/crud/" + event.target.id,
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
});
