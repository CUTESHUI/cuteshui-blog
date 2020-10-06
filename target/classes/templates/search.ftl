<#include "/inc/layout.ftl" />

<@layout "搜索 - ${q}">

<#include "/inc/header-panel.ftl" />

<div class="layui-container">
    <div class="layui-row layui-col-space15">

        <div class="layui-col-md8">
            <div class="fly-panel">
                <div class="fly-panel-title fly-filter">
                    <a>您正在搜索关键字 “ ${q} ” - 共有 <strong>${searchData.total}</strong> 条记录</a>
                </div>
                <ul class="fly-list">

                    <#list searchData.records as post>
                        <@plisting post></@plisting>
                    </#list>
                </ul>
                <@paging searchData></@paging>
            </div>
        </div>

        <#include "/inc/right.ftl" />

    </div>
</div>
</@layout>