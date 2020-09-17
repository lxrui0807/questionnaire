/** EasyWeb spa v3.0.8 data:2019-03-24 License By http://easyweb.vip */
layui.define(["layer", "config", "layRouter"], function(h) {
    var j = layui.jquery;
    var m = layui.layer;
    var d = layui.config;
    var q = layui.layRouter;
    var a = ".layui-layout-admin>.layui-body";
    var n = a + ">.layui-tab";
    var g = ".layui-layout-admin>.layui-side>.layui-side-scroll";
    var l = ".layui-layout-admin>.layui-header";
    var c = "admin-pagetabs";
    var f = "admin-side-nav";
    var e = "theme-admin";
    var p = {
        flexible: function(r) {
            var s = j(".layui-layout-admin").hasClass("admin-nav-mini");
            if (s == !r) {
                return
            }
            if (r) {
                j(".layui-layout-admin").removeClass("admin-nav-mini")
            } else {
                j(".layui-layout-admin").addClass("admin-nav-mini")
            }
            p.removeNavHover();
            b()
        },
        activeNav: function(r) {
            if (!r) {
                r = location.hash
            }
            if (r && r != "") {
                j(g + ">.layui-nav .layui-nav-item .layui-nav-child dd").removeClass("layui-this");
                j(g + ">.layui-nav .layui-nav-item").removeClass("layui-this");
                var v = j(g + '>.layui-nav a[href="#' + r + '"]');
                if (v && v.length > 0) {
                    if (j(g + ">.layui-nav").attr("lay-accordion") == "true") {
                        j(g + ">.layui-nav .layui-nav-itemed").removeClass("layui-nav-itemed")
                    }
                    v.parent().addClass("layui-this");
                    v.parent("dd").parents(".layui-nav-child").parent().addClass("layui-nav-itemed");
                    j('ul[lay-filter="' + f + '"]').addClass("layui-hide");
                    var t = v.parents(".layui-nav");
                    t.removeClass("layui-hide");
                    j(l + ">.layui-nav>.layui-nav-item").removeClass("layui-this");
                    j(l + '>.layui-nav>.layui-nav-item>a[nav-bind="' + t.attr("nav-id") + '"]').parent().addClass("layui-this");
                    var s = v.offset().top + v.outerHeight() + 30 - p.getPageHeight();
                    var u = 50 + 65 - v.offset().top;
                    if (s > 0) {
                        j(g).animate({
                            "scrollTop": j(g).scrollTop() + s
                        }, 100)
                    } else {
                        if (u > 0) {
                            j(g).animate({
                                "scrollTop": j(g).scrollTop() - u
                            }, 100)
                        }
                    }
                } else {}
            } else {
                console.warn("active url is null")
            }
        },
        popupRight: function(r) {
            if (r.title == undefined) {
                r.title = false;
                r.closeBtn = false
            }
            if (r.anim == undefined) {
                r.anim = 2
            }
            if (r.fixed == undefined) {
                r.fixed = true
            }
            r.isOutAnim = false;
            r.offset = "r";
            r.shadeClose = true;
            r.area = "336px";
            r.skin = "layui-layer-adminRight";
            r.move = false;
            return p.open(r)
        },
        open: function(t) {
            if (!t.area) {
                t.area = (t.type == 2) ? ["360px", "300px"] : "360px"
            }
            if (!t.skin) {
                t.skin = "layui-layer-admin"
            }
            if (!t.offset) {
                t.offset = "35px"
            }
            if (t.fixed == undefined) {
                t.fixed = false
            }
            t.resize = t.resize != undefined ? t.resize : false;
            t.shade = t.shade != undefined ? t.shade : 0.1;
            var r = t.end;
            t.end = function() {
                m.closeAll("tips");
                r && r()
            };
            if (t.url) {
                t.type = 1;
                var s = t.success;
                t.success = function(u, v) {
                    p.showLoading(u, 2);
                    s ? s(u, v) : "";
                    j(u).children(".layui-layer-content").load(t.url, function() {
                        p.removeLoading(u, false)
                    })
                }
            }
            return m.open(t)
        },
        req: function(r, s, t, u) {
            if ("put" == u.toLowerCase()) {
                u = "POST";
                s._method = "PUT"
            } else {
                if ("delete" == u.toLowerCase()) {
                    u = "POST";
                    s._method = "DELETE"
                }
            }
            p.ajax({
                url: d.base_server + r,
                data: s,
                type: u,
                dataType: "json",
                success: t
            })
        },
        ajax: function(s) {
            var r = s.success;
            s.success = function(t, u, w) {
                var v;
                if ("json" == s.dataType.toLowerCase()) {
                    v = t
                } else {
                    v = p.parseJSON(t)
                }
                v && (v = t);
                if (d.ajaxSuccessBefore(v, s.url) == false) {
                    return
                }
                r(t, u, w)
            };
            s.error = function(t) {
                s.success({
                    code: t.status,
                    msg: t.statusText
                })
            };
            s.beforeSend = function(v) {
                var u = d.getAjaxHeaders(s.url);
                for (var t = 0; t < u.length; t++) {
                    v.setRequestHeader(u[t].name, u[t].value)
                }
            };
            j.ajax(s)
        },
        hasPerm: function(t) {
            var r = d.getUserAuths();
            if (r) {
                for (var s = 0; s < r.length; s++) {
                    if (t == r[s]) {
                        return true
                    }
                }
            }
            return false
        },
        parseJSON: function(t) {
            if (typeof t == "string") {
                try {
                    var s = JSON.parse(t);
                    if (typeof s == "object" && s) {
                        return s
                    }
                } catch (r) {}
            }
        },
        showLoading: function(u, t, s) {
            var r = ['<div class="ball-loader"><span></span><span></span><span></span><span></span></div>', '<div class="rubik-loader"></div>'];
            if (!u) {
                u = "body"
            }
            if (t == undefined) {
                t = 1
            }
            j(u).addClass("page-no-scroll");
            var v = j(u).children(".page-loading");
            if (v.length <= 0) {
                j(u).append('<div class="page-loading">' + r[t - 1] + "</div>");
                v = j(u).children(".page-loading")
            }
            s && v.css("background-color", "rgba(255,255,255," + s + ")");
            v.show()
        },
        removeLoading: function(s, u, r) {
            if (!s) {
                s = "body"
            }
            if (u == undefined) {
                u = true
            }
            var t = j(s).children(".page-loading");
            if (r) {
                t.remove()
            } else {
                u ? t.fadeOut() : t.hide()
            }
            j(s).removeClass("page-no-scroll")
        },
        putTempData: function(r, s) {
            if (s != undefined && s != null) {
                layui.sessionData("tempData", {
                    key: r,
                    value: s
                })
            } else {
                layui.sessionData("tempData", {
                    key: r,
                    remove: true
                })
            }
        },
        getTempData: function(r) {
            var s = layui.sessionData("tempData");
            if (s) {
                return s[r]
            } else {
                return false
            }
        },
        rollPage: function(u) {
            var s = j(n + ">.layui-tab-title");
            var t = s.scrollLeft();
            if ("left" === u) {
                s.animate({
                    "scrollLeft": t - 120
                }, 100)
            } else {
                if ("auto" === u) {
                    var r = 0;
                    s.children("li").each(function() {
                        if (j(this).hasClass("layui-this")) {
                            return false
                        } else {
                            r += j(this).outerWidth()
                        }
                    });
                    s.animate({
                        "scrollLeft": r - 120
                    }, 100)
                } else {
                    s.animate({
                        "scrollLeft": t + 120
                    }, 100)
                }
            }
        },
        refresh: function(r) {
            q.refresh(r)
        },
        closeThisTabs: function(r) {
            p.closeTabOperNav();
            var s = j(n + ">.layui-tab-title");
            if (!r) {
                if (s.find("li").first().hasClass("layui-this")) {
                    m.msg("主页不能关闭", {
                        icon: 2
                    });
                    return
                }
                s.find("li.layui-this").find(".layui-tab-close").trigger("click")
            } else {
                if (r == s.find("li").first().attr("lay-id")) {
                    m.msg("主页不能关闭", {
                        icon: 2
                    });
                    return
                }
                s.find('li[lay-id="' + r + '"]').find(".layui-tab-close").trigger("click")
            }
        },
        closeOtherTabs: function(r) {
            if (!r) {
                j(n + ">.layui-tab-title li:gt(0):not(.layui-this)").find(".layui-tab-close").trigger("click")
            } else {
                j(n + ">.layui-tab-title li:gt(0)").each(function() {
                    if (r != j(this).attr("lay-id")) {
                        j(this).find(".layui-tab-close").trigger("click")
                    }
                })
            }
            p.closeTabOperNav()
        },
        closeAllTabs: function() {
            j(n + ">.layui-tab-title li:gt(0)").find(".layui-tab-close").trigger("click");
            j(n + ">.layui-tab-title li:eq(0)").trigger("click");
            p.closeTabOperNav()
        },
        closeTabOperNav: function() {
            j(".layui-icon-down .layui-nav .layui-nav-child").removeClass("layui-show")
        },
        changeTheme: function(x) {
            if (x) {
                layui.data(d.tableName, {
                    key: "theme",
                    value: x
                });
                if (e == x) {
                    x = undefined
                }
            } else {
                layui.data(d.tableName, {
                    key: "theme",
                    remove: true
                })
            }
            p.removeTheme(top);
            !x || top.layui.link(p.getThemeDir() + x + ".css", x);
            var y = top.window.frames;
            for (var t = 0; t < y.length; t++) {
                var v = y[t];
                try {
                    p.removeTheme(v)
                } catch (w) {}
                if (x && v.layui) {
                    v.layui.link(p.getThemeDir() + x + ".css", x)
                }
                var u = v.frames;
                for (var s = 0; s < u.length; s++) {
                    var r = u[s];
                    try {
                        p.removeTheme(r)
                    } catch (w) {}
                    if (x && r.layui) {
                        r.layui.link(p.getThemeDir() + x + ".css", x)
                    }
                }
            }
        },
        removeTheme: function(r) {
            if (!r) {
                r = window
            }
            if (r.layui) {
                var s = "layuicss-theme";
                r.layui.jquery('link[id^="' + s + '"]').remove()
            }
        },
        getThemeDir: function() {
            return layui.cache.base + "theme/"
        },
        closeThisDialog: function() {
            parent.layer.close(parent.layer.getFrameIndex(window.name))
        },
        closeDialog: function(r) {
            var s = j(r).parents(".layui-layer").attr("id").substring(11);
            m.close(s)
        },
        iframeAuto: function() {
            parent.layer.iframeAuto(parent.layer.getFrameIndex(window.name))
        },
        getPageHeight: function() {
            return document.documentElement.clientHeight || document.body.clientHeight
        },
        getPageWidth: function() {
            return document.documentElement.clientWidth || document.body.clientWidth
        },
        removeNavHover: function() {
            j(".admin-nav-hover>.layui-nav-child").css({
                "top": "auto",
                "max-height": "none",
                "overflow": "auto"
            });
            j(".admin-nav-hover").removeClass("admin-nav-hover")
        },
        setNavHoverCss: function(t) {
            var r = j(".admin-nav-hover>.layui-nav-child");
            if (t && r.length > 0) {
                var v = (t.offset().top + r.outerHeight()) > window.innerHeight;
                if (v) {
                    var s = t.offset().top - r.outerHeight() + t.outerHeight();
                    if (s < 50) {
                        var u = p.getPageHeight();
                        if (t.offset().top < u / 2) {
                            r.css({
                                "top": "50px",
                                "max-height": u - 50 + "px",
                                "overflow": "auto"
                            })
                        } else {
                            r.css({
                                "top": t.offset().top,
                                "max-height": u - t.offset().top,
                                "overflow": "auto"
                            })
                        }
                    } else {
                        r.css("top", s)
                    }
                } else {
                    r.css("top", t.offset().top)
                }
                k = true
            }
        }
    };
    p.events = {
        flexible: function(s) {
            var r = j(".layui-layout-admin").hasClass("admin-nav-mini");
            p.flexible(r)
        },
        refresh: function() {
            p.refresh()
        },
        back: function() {
            history.back()
        },
        theme: function() {
            var r = j(this).attr("data-url");
            p.popupRight({
                id: "layer-theme",
                url: r ? r : "components/tpl/theme.html"
            })
        },
        note: function() {
            var r = j(this).attr("data-url");
            p.popupRight({
                id: "layer-note",
                url: r ? r : "components/tpl/note.html"
            })
        },
        message: function() {
            var r = j(this).attr("data-url");
            p.popupRight({
                id: "layer-notice",
                url: r ? r : "components/tpl/message.html"
            })
        },
        psw: function() {
            var r = j(this).attr("data-url");
            p.open({
                id: "pswForm",
                title: "修改密码",
                shade: 0,
                url: r ? r : "components/tpl/password.html"
            })
        },
        logout: function() {
            var r = j(this).attr("data-url");
            m.confirm("确定要退出登录吗？", {
                title: "温馨提示",
                skin: "layui-layer-admin"
            }, function() {
                d.removeToken();
                r ? location.replace(r) : location.reload()
            })
        },
        fullScreen: function(x) {
            var z = "layui-icon-screen-full",
                t = "layui-icon-screen-restore";
            var r = j(this).find("i");
            var w = document.fullscreenElement || document.msFullscreenElement || document.mozFullScreenElement || document.webkitFullscreenElement || false;
            if (w) {
                var v = document.exitFullscreen || document.webkitExitFullscreen || document.mozCancelFullScreen || document.msExitFullscreen;
                if (v) {
                    v.call(document)
                } else {
                    if (window.ActiveXObject) {
                        var y = new ActiveXObject("WScript.Shell");
                        y && y.SendKeys("{F11}")
                    }
                }
                r.addClass(z).removeClass(t)
            } else {
                var s = document.documentElement;
                var u = s.requestFullscreen || s.webkitRequestFullscreen || s.mozRequestFullScreen || s.msRequestFullscreen;
                if (u) {
                    u.call(s)
                } else {
                    if (window.ActiveXObject) {
                        var y = new ActiveXObject("WScript.Shell");
                        y && y.SendKeys("{F11}")
                    }
                }
                r.addClass(t).removeClass(z)
            }
        },
        leftPage: function() {
            p.rollPage("left")
        },
        rightPage: function() {
            p.rollPage()
        },
        closeThisTabs: function() {
            p.closeThisTabs()
        },
        closeOtherTabs: function() {
            p.closeOtherTabs()
        },
        closeAllTabs: function() {
            p.closeAllTabs()
        },
        closeDialog: function() {
            p.closeDialog(this)
        },
        closeIframeDialog: function() {
            p.closeThisDialog()
        }
    };
    j("body").on("click", "*[ew-event]", function() {
        var r = j(this).attr("ew-event");
        var s = p.events[r];
        s && s.call(this, j(this))
    });
    j("body").on("mouseenter", "*[lay-tips]", function() {
        var r = j(this).attr("lay-tips");
        var s = j(this).attr("lay-direction");
        var t = j(this).attr("lay-bg");
        m.tips(r, this, {
            tips: [s || 3, t || "#333333"],
            time: -1
        })
    }).on("mouseleave", "*[lay-tips]", function() {
        m.closeAll("tips")
    });
    var k = false;
    j("body").on("mouseenter", ".layui-layout-admin.admin-nav-mini .layui-side .layui-nav .layui-nav-item>a", function() {
        if (p.getPageWidth() > 750) {
            var t = j(this);
            j(".admin-nav-hover>.layui-nav-child").css("top", "auto");
            j(".admin-nav-hover").removeClass("admin-nav-hover");
            t.parent().addClass("admin-nav-hover");
            var r = j(".admin-nav-hover>.layui-nav-child");
            if (r.length > 0) {
                p.setNavHoverCss(t)
            } else {
                var s = t.find("cite").text();
                m.tips(s, t, {
                    tips: [2, "#333333"],
                    time: -1
                })
            }
        }
    }).on("mouseleave", ".layui-layout-admin.admin-nav-mini .layui-side .layui-nav .layui-nav-item>a", function() {
        m.closeAll("tips")
    });
    j("body").on("mouseleave", ".layui-layout-admin.admin-nav-mini .layui-side", function() {
        k = false;
        setTimeout(function() {
            if (!k) {
                p.removeNavHover()
            }
        }, 500)
    });
    j("body").on("mouseenter", ".layui-layout-admin.admin-nav-mini .layui-side .layui-nav .layui-nav-item.admin-nav-hover .layui-nav-child", function() {
        k = true
    });
    var o = true;
    var b = function() {
        o = false;
        setTimeout(function() {
            o = false;
            j(window).resize();
            setTimeout(function() {
                o = true
            }, 100)
        }, 500)
    };
    j(window).on("resize", function() {
        if (o) {
            setTimeout(function() {
                o = false;
                j(window).resize();
                setTimeout(function() {
                    o = true
                }, 100)
            }, 500)
        }
    });
    var i = layui.data(d.tableName);
    if (i && i.theme) {
        (i.theme == e) || layui.link(p.getThemeDir() + i.theme + ".css", i.theme)
    } else {
        if (e != d.defaultTheme) {
            layui.link(p.getThemeDir() + d.defaultTheme + ".css", d.defaultTheme)
        }
    }
    h("admin", p)
});