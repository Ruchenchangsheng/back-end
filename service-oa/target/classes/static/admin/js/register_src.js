var vue = new Vue({
    el: ".register",
    data: {
        text: "验证邮箱",
        count: 60,
        sending: false,
        loading: false, // 锁
        loading2: false, // 锁
        username: "",
        password: "",
        email: "",
        code: null,
        level: "Guest",
    },
    methods: {
        sendMail() {
            if (this.email === "") {
                alert("邮箱不能为空！");
                return;
            }
            this.loading2 = true;
            axios
                .get("/user/email?email=" + this.email)
                .then((response) => {
                    this.loading2 = false;
                    var result = response.data;
                    if (result.code == 200) {
                        commonUtil.message(result.message);
                        this.code = result.data;
                        this.sending = true;
                        this.text = `(${this.count})`;
                        const timer = setInterval(() => {
                            this.text = `(${--this.count})`;
                            if (this.count <= 0) {
                                clearInterval(timer);
                                this.count = 60;
                                this.sending = false;
                                this.code = null;
                                this.text = "重新发送";
                            }
                        }, 1000);
                    } else {
                        this.text = "重新发送";
                        commonUtil.message(result.message, "danger");
                    }
                })
                .catch((error) => {
                    alert("邮箱验证请求出错：" + error);
                    this.count = 0;
                });
        },
        submitData() {
            if (this.username === "" || this.password === "") {
                alert("用户名或密码不能为空！");
                return;
            }
            if ($("#code input").val() != this.code) {
                alert("验证码错误或已失效！");
                return;
            }
            this.loading = true;
            axios({
                method: "post",
                url: "/user/register",
                params: {
                    username: vue.username,
                    password: vue.password,
                    email: vue.email,
                    level: vue.level,
                },
            })
                .then((response) => {
                    vue.loading = false;
                    var result = response.data;
                    alert(result.message);
                    if (result.code == 200) {
                        window.location.href = "/";
                    }
                })
                .catch((error) => {
                    vue.loading = false;
                    alert("注册请求出错：" + error);
                });
        },
    },
});