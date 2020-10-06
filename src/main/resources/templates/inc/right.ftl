<div class="layui-col-md4">

    <dl class="fly-panel fly-list-one">
        <dt class="fly-panel-title">本周热议</dt>

        <@weekrank>
            <#list results as post>
                <dd>
                    <a href="/post/${post.id}">${post.title}</a>
                    <span><i class="iconfont icon-pinglun1"></i> ${post.commentCount}</span>
                </dd>
            </#list>
        </@weekrank>

    </dl>

    <div class="fly-panel">
        <div class="fly-panel-title">
            可以交个朋友
        </div>
        <div class="fly-panel-main">
            <img src="/res/images/cuteshui_wx.jpg" alt="CUTESHUI" style="height: 365px;" width="280">
        </div>
    </div>

    <div class="fly-panel fly-link">
        <h3 class="fly-panel-title">友情链接</h3>
        <dl class="fly-panel-main">
            <dd><a href="http://www.layui.com/" target="_blank">layui</a><dd>
            <dd><a href="http://layim.layui.com/" target="_blank">WebIM</a><dd>
            <dd><a href="http://layer.layui.com/" target="_blank">layer</a><dd>
            <dd><a href="http://www.layui.com/laydate/" target="_blank">layDate</a><dd>
            <dd><a href="mailto:xianxin@layui-inc.com?subject=%E7%94%B3%E8%AF%B7Fly%E7%A4%BE%E5%8C%BA%E5%8F%8B%E9%93%BE" class="fly-link">申请友链</a><dd>
        </dl>
    </div>

</div>