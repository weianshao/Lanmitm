Lanmitm — Android中间人攻击测试工具
=============

#####详细介绍：<http://oinux.com>

#####下载说明：安装文件请直接进入download目录下载，开发版请自行导入eclipse中运行

* * *

更新历史
-------------------
####v1.2.1版本说明 (2014-12-15)

1、修复bug

####v1.2版本说明 (2014-12-12)

1、添加禁止上网功能

2、添加引导安装必要工具

####v1.1版本说明 (2014-12-09)

1、添加代码注入功能

####v1.0版本说明 (2014-11-30)

1、支持局域网抓包

2、局域网会话劫持

* * *

功能概述
-------------------
- 数据嗅探，可抓去局域网内部主机与外界通信数据

- 会话劫持，对局域网中的主机通过实施ARP欺骗，进行cookie欺骗，从而达到劫持会话的效果

- 简单web服务器功能，结合下面功能实施钓鱼欺骗

- 代码注入，通过数据截取，对远程主机返回的数据进行篡改，实现代码注入的效果

- WiFi终结，中断局域网内主机与外界通信

- URL重定向，实现DNS欺骗功能。配合上面web服务器功能，可进行钓鱼欺骗功能待（待续）

* * *

效果预览
-------------------
![screenshot](screenshot/lanmitm_main_page.png)
![screenshot](screenshot/lanmitm_hosts_page.png)
![screenshot](screenshot/lanmitm_hijack_page.png)
![screenshot](screenshot/lanmitm_hijack_browser.png)
![screenshot](screenshot/lanmitm_hijack_history.png)
![screenshot](screenshot/lanmitm_inject.png)
![screenshot](screenshot/lanmitm_inject_result.png)
![screenshot](screenshot/lanmitm_sniffer.png)
![screenshot](screenshot/lanmitm_http_server_page.png)

* * *

安装前提
-------------------
- 安卓手机2.3及以上，必须root

- 已安装busybox

* * *

后话
-------------------
虽然该应用功能单一，但是毕竟是自己慢慢写出来的，我还是会继续更新下去的，如果有想法，或者想学习的同学都欢迎和我交流，或者直接贡献代码 <https://github.com/ssun125/Lanmitm>


