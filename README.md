# Minet and Miro

> 项目地址：https://github.com/wujr5/computer-network-project

## 0 Bug Report and Update News

> Bug 报告

## 1 简介

### 1.1 项目背景

这个项目是：2015年秋季，2013级软件工程，[《计算机网络》课程项目](http://edin.sysu.edu.cn/~zhgf/)。

项目有两个部分组成，分别是客户端：Minet，服务端：Miro。核心是成为MMProcotol的数据协议。

**Minet功能：**

1. 登录，注册等简单的用户管理功能
2. 在线用户发起P2P聊天功能
3. 创建群聊，聊天室功能
4. 在线用户间进行P2P的屏幕分享功能

**Miro功能：**

1. 作为Minet的后台服务器，连接数据库和Minet
2. 广播在线用户基本信息，包括IP和端口
3. 更新和记录上下线的用户的信息
4. 群聊时，向群组内用户广播群聊信息

### 1.2 小组信息

| 姓名      												| 学号     | 角色  |贡献 |分工 											|
|-----------------------------------|----------|-------|-----|--------------------------|
| 周基源    												| 13331370 | 组长  |25%  |UI设计，前端swing前端编程 |
| [吴家荣](http://github.com/wujr5) | 13331270 | 组员  |25%  |MMProtocol在客户端的实现  |
| 景涛 															| 13354135 | 组员  |25%  |MMProtocol在服务端的实现  |
| 邱永臣    												| 13331212 | 组员  |25%  |数据库表设计与存储的实现  |

## 2 项目详情

### 2.1 源代码

1. 前后台均用java语言
2. 前端使用swing实现
3. 项目的核心是TCP Socket编程
4. 实现了成为MMProtocol的数据协议，但是协议的设计欠合理，没有实现统一的对外接口

### 2.2 基本界面

> 注意，本项目没有实现NAT，意思是，服务端和客户端的运行需要在同一局域网内，或者都在公网内。

**“可视化”服务端**

![](http://ww1.sinaimg.cn/large/ed796d65gw1ezwc7h6b17j20ro0m6tb6.jpg)

**登陆与注册**

登陆前需要填写服务器的IP地址

![](http://ww3.sinaimg.cn/large/ed796d65gw1ezwc8vsgdfj209k04cdga.jpg)

**登陆界面**

![](http://ww1.sinaimg.cn/large/ed796d65gw1ezwcfeak0hj20ds08agma.jpg)

**注册界面**

![](http://ww3.sinaimg.cn/large/ed796d65gw1ezwcfqbygjj20ds08a755.jpg)

**聊天界面**

![](http://ww3.sinaimg.cn/large/ed796d65gw1ezwcg03cz6j216x0qltb1.jpg)

**屏幕共享**

![](http://ww2.sinaimg.cn/large/ed796d65gw1ezwcg9ff8aj20x80oy412.jpg)
![](http://ww4.sinaimg.cn/large/ed796d65gw1ezwcgn847cj20xk0u0qae.jpg)

### 2.3 可执行文件下载、运行、部署、贡献

#### 2.3.1 下载

* 可视化服务端：[Miro_v2.0.0](https://github.com/wujr5/computer-network-project/raw/master/execute%20file/Miro.jar)
* 简易客户端：[Minet_v2.0.0](https://github.com/wujr5/computer-network-project/raw/master/execute%20file/Minet.jar)

#### 2.3.2 运行

```js
1. 双击Miro.jar，运行可视化服务程序
2. 复制Miro面板上显示的IP地址
3. 双击Minet.jar，运行客户端程序
4. 粘贴复制的IP地址
5. 选择登陆或者注册
```

#### 2.3.3 部署

开发工具：Eclipse  
操作系统：windows 10  
运行环境：JRE  
引用库文件：  
![](http://ww3.sinaimg.cn/large/ed796d65gw1ezwds8vqufj20g806s75v.jpg)

下载到本地：

git方式：

```bash
git clone https://github.com/wujr5/computer-network-project.git
```

或者点击下载：

![](http://ww2.sinaimg.cn/large/ed796d65gw1ezwd6r31xuj216r0m078f.jpg)

项目开发时是用Eclipse进行开发的，因此下面步骤是针对Eclipse的。

1. 打开Eclipse，选择`File > Switch Workspace > Others...`
2. 接着选择刚才下载的项目的最外面的文件夹：`computer-network-project`作为新的workspace
3. 选择`File > Import...`
4. 选择`General`中的`Existing Projects into Workspace`
5. 然后选择`Select root directory`的单选选项，点击`Browse...`
6. 选择`computer-network-project`文件夹下的`Minet`文件夹
7. 点击确定，然后点击`Finish`，完成`Minet`项目的导入
8. 重复步骤`3-7`导入`Miro`项目
9. 在Eclipse的Package Exporer面板中选中Minet项目，接着`右键 > Properties`
10. 在搜索栏中输入`build`，在结果中单击`Java Build Path`
11. 选中`Libraies`面板，选中出现错误（有错误提示）的条目（可多选），点击`Remove`
12. 点击`Add External JARs`选择文件夹`computer-network-project/jar/`下面的全部jar文件，点击打开，点击OK
13. 此时，Minet应该没有错误提示了，对`Miro`重复步骤`9-12`
14. 此时，Miro还会有错误，因为Miro引用了Minet内的文件
15. 在Eclipse的Package Exporer面板中选中Miro项目，接着`右键 > Properties`
16. 在搜索栏中输入`build`，在结果中单击`Java Build Path`
17. 选中`Source`面板，点击`Link Source...`
18. 在`Link folder location`下，点击`Browse...`，选择`computer-network-project/Minet/ShareCode`文件夹，点击确定，点击`Finish`，点击OK
19. 此时，正常情况是，Minet和Miro项目都已经正常部署了，无错误发生
20. 接下来，先运行Miro的代码，再运行Minet的代码即可

## 3 MMProtocol协议设计

### 3.1 MMProtocol协议类型

![](http://ww3.sinaimg.cn/large/ed796d65gw1ezweiyn3vhj20pr0o6doi.jpg)

### 3.2 MMProtocol协议客户端功能实现
![](http://ww1.sinaimg.cn/large/ed796d65gw1ezwer7rnk7j20tf0n9n82.jpg)

### 3.3 MMProtocol协议服务端功能实现
![](http://ww4.sinaimg.cn/large/ed796d65gw1ezwev14xvyj20us0of12g.jpg)

### 3.4 MMProtocol协议数据包结构
![](http://ww1.sinaimg.cn/large/ed796d65gw1ezwewpqlivj20u30k5n4f.jpg)

## 4 亮点

### 4.1 可视化服务器端

服务器端结合java swing用户界面，直接在机器上运行jar可执行文件，即可完成对服务端的部署

![](http://ww1.sinaimg.cn/large/ed796d65gw1ezwc7h6b17j20ro0m6tb6.jpg)

### 4.2 清晰的六层项目逻辑结构

* UI层：Java Swing实现，负责界面逻辑呈现
* Minet业务逻辑层：负责处理交互的逻辑
* Minet通信层：MMProtocol客户端部分实现
* Miro通信层：MMProtocol服务端部分实现
* Miro后台业务层：负责与数据库的逻辑交互
* Sqlite数据层：使用Sqlite数据库管理后台数据

**Minet项目结构**

![](http://ww4.sinaimg.cn/large/ed796d65gw1ezwf8hv7bkj20h40gmtbu.jpg)

**Miro项目结构**

![](http://ww1.sinaimg.cn/large/ed796d65gw1ezwf9gtxsdj20h60c1taf.jpg)

**项目间共享代码**

![](http://ww3.sinaimg.cn/large/ed796d65gw1ezwfakgsvzj20hg05v0tj.jpg)

### 4.3 屏幕共享功能

![](http://ww1.sinaimg.cn/large/ed796d65gw1ezwfrfnj73j21hc0u0to3.jpg)

### 4.4 利用git进行版本管理

git地址：https://github.com/wujr5/computer-network-project

![](http://ww4.sinaimg.cn/large/ed796d65gw1ezwfwsnucxj21640qbdkx.jpg)

### 4.5 Creative or difficulty

项目统计：

![](http://ww1.sinaimg.cn/large/ed796d65gw1ezwg1m0iruj20qh0kh429.jpg)

### 4.6 Friendly GUI

在GUI和交互方面，我们小组进行了较充分的设计和调试，在有限时间尽力做到了最好。

## 5 展示PPT

![](http://ww2.sinaimg.cn/large/ed796d65gw1ezwfk91wi8j21hc0u00ut.jpg)

点击下载：[ppt](https://github.com/wujr5/computer-network-project/raw/master/ppt/Minet%20and%20Miro.pptx)

## 6 展示视频和部署视频

展示视频：

![](http://ww2.sinaimg.cn/large/ed796d65gw1ezwk9qjjiuj21er0u0wi5.jpg)

观看：[展示视频](http://v.qq.com/boke/page/u/y/d/u018066wkyd.html)


部署视频：

![](http://ww1.sinaimg.cn/large/ed796d65gw1ezwka3wlkoj21br0u07cy.jpg)

观看：[部署视频](http://v.qq.com/page/r/e/b/r01803oaneb.html)

### 7 每个小组成员的总结

### 7.1 吴家荣

这次实验负责的是MMProtocol在客户端的实现，算是通信层的内容。其实总体感觉，虽然说是分工明确，其实还是免不了要了解在这个技术堆栈或者说层次堆栈上下两层的内容的。协作开发绝对少不了沟通交流，单干是可以，但是会缺少很多思想碰撞的机会。

设计和实现协议的过程，让我感触很大，也对协议及其概念本身有了很深刻的理解。

在做这个项目之前，对协议的理解是一种服务。比如HTTP协议，它规定了数据传输的格式、数据包的格式，以及不同请求方法对应的操作。以前的理解是，就像浏览器的网络服务，就是HTTP协议的功能实现。其实这样的理解应该是不对的。

协议，应该是一种标准。而协议的实现，可以多种多样。比如HTTP协议，在客户端，协议的实现可以是浏览器，也可以是其他的软件，在服务端，可以是Apache，Nginx，Nodejs等，这些都是根据协议的规定而实现的服务。

而这些服务在应用层实现的核心，就是对TCP和UDP的Socket编程。

开始的时候，我们没有充分意识到这些认识，因此开始就走了弯路，后来不断重构代码，花了不少时间。

而最合理的协议实现，应该是对外提供统一的接口。比如优秀的HTTP协议，对外利用统一资源定位符进行资源定位，构造比如get, put, post, delete等方法对应不同的操作，对外接口非常简洁，因此利用起来也非常的方便。

虽然最终还是实现了要求的功能，但是不能尽善尽美，可是接近期末了，我们也没有充分的时间去改良了。因此，先就此作罢。

### 7.2 景涛

### 7.3 邱永臣

### 7.4 周基源

## 8 in-group assessment

### 8.1 吴家荣

| 姓名      												| 学号     | 分数  |
|-----------------------------------|----------|-------|
| 周基源    												| 13331370 |  93   |
| 景涛 															| 13354135 |  94   |
| 邱永臣    												| 13331212 |  95   |

### 8.2 景涛

| 姓名      												| 学号     | 分数  |
|-----------------------------------|----------|-------|
| 周基源    												| 13331370 |  95   |
| [吴家荣](http://github.com/wujr5) | 13331270 |  96   |
| 邱永臣    												| 13331212 |  95   |

### 8.3 邱永臣

| 姓名      												| 学号     | 分数  |
|-----------------------------------|----------|-------|
| 周基源    												| 13331370 |  90   |
| [吴家荣](http://github.com/wujr5) | 13331270 |  92   |
| 景涛 															| 13354135 |  91   |

### 8.4 周基源

| 姓名      												| 学号     | 分数  |
|-----------------------------------|----------|-------|
| [吴家荣](http://github.com/wujr5) | 13331270 |  90   |
| 景涛 															| 13354135 |  90   |
| 邱永臣    												| 13331212 |  93   |

## 9 附加作业

附加功能是：图像传输功能，也就是屏幕共享

![](http://ww1.sinaimg.cn/large/ed796d65gw1ezwfrfnj73j21hc0u0to3.jpg)

## 10 检查回顾

作品提交要求：
![](http://ww2.sinaimg.cn/large/ed796d65gw1ezwe7ts711j20p10hnail.jpg)

检查：

1. 源代码 & 可执行文件 & Readme。**（100%）**
2. PPT：**（100%）**
3. demo video。**（100%）**
4. report documents：**（100%）**
5. 每个小组成员的总结：**（100%）**
6. in-group assessment：**（100%）**
7. (Optional)附加作业：**（100%）**