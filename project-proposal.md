# project proposal

## 1 简介

### 1.1 小组成员

> 组长：  
> 13331370 周基源

> 组员：  
> 13331270 [吴家荣][]   
> 13354135 景涛   
> 13331212 邱永臣

[吴家荣]: http://github.com/wujr5

### 1.2 项目内容

#### 1.2.1 MINET

**WHAT is MINET?**

> 迷网，It's the client's name

**The Server: MIRO**

> MIRO = My Root = 迷路

**MIRO basic requirements**

> Run on Linux/Unix (my appdev server)

> Wait for MINET connection.

> Serve MINET to tell who is online: IP, port, user name, etc

> Chat room suported: Broadcast received messages to all online MINET.

**MINET basic requirements**

> Connect Miro to get and list online users

> Chat room:   
> 1. Send text message to Miro.   
> 2. Receive text message from Miro.

> P2P chat   
> 1. Select from online user(s), and connect to it(them)   
> 2. Send/receive messages

**Extended functions (Optional)**

> 
* Support Image transfer.
* Audio, Video chat.
* Provide web server, for browser
* supported information query, or chat?
(Chapter 2)
* Support offline mail system (Chapter 2)
* UDP Pinger (Chapter 2)
* Proxy Cache (Chapter 2)
* 防火墙穿透
* Implementing a Reliable Transport Protocol (Chapter 3)
* Implementing a Distributed, Asynchronous Distance Vector Routing Algorithm (Chapter 4)
* Streaming Video with RTSP and RTP (Chapter 7)

#### 1.2.2 presentations

> one 10-20 min. presentations (Network related topics,
or your project proposal, final project report.)

**Presentation topics**

> 
* NS-2
* DHT
* The Darnet and the Future of Content
* Distribution
* P2P file sharing applications
* TCP friendly
* UPnP
* NATs vs IPsec
* Other routing algorithms

#### 1.2.3 Requirements: Due on Week 15

**Submit**

> 
* (1) PPTs + demo video
* (2) Source code (and the compiled executable files)
* (3) The project report documents (including introduction, design, setup and deploy, and result, project management records)
* (4) The individual report of each team members (your contributions, and anything else you want to talk about )
* (5) votes of the top 5 teams (based on their presentations and your observations, give comments of 2-3 sentences)
* (6) in-group assessment ( grade each other in group)

#### 1.2.4 Grade policy

**Basic points**

> 
* Protocol design. (10 points)
* Finish basic function correctly (w/o error). (60 points)
* On time (WEEK 15). (10 points)
* Documents, codes, presentation. (20 points) 
	* votes
	* in-group assessment

**Bonus points: 10 at most.**

> 
* Use top Library: such as POCO, boost.asio, etc. (+2) 
* Use github for version control: [github edu](https://github.com/edu) (+2)
* Creative or difficulty (+2)
* Extra functions implementation (+2)
* Friendly GUI (+2)
* Apply advanced design pattern or else (+2)
* Excellent Presentation (+2)

## 2 项目前期（MINET）

### 2.1 项目计划

#### 2.1.1 项目分工

**前期分工**

```
吴家荣：项目计划，拓展功能
景涛：功能设计
周基源：界面设计
邱永臣：协议设计
```

**开发分工**

我们小组决定做一个安卓app的Minet。

```
全体成员：信息架构，数据架构，协议架构的讨论与设计
吴家荣：产品原型设计和实现
景涛：安卓app前端
周基源：协议结构的服务器部分
邱永臣：协议架构的客户端部分
```

#### 2.1.2 计划进度

week9：讨论协议，细节化设计图，信息架构，数据建模
week10：安卓app原型
week11：协议设计完成30%
week12：协议设计完成50%
week13：协议设计完成75%，安卓app完成基本功能
week14：Minet version 1.0.0
week15：实现拓展功能，写文档，并提交。

#### 2.1.3 项目背景

Minet是中山大学数据科学与计算机学院，软件工程方向，13级，计算机网络课程的课程project。根据课程要求，我们应该通过project掌握协议设计的各种细节。然后应用到实际当中。

类似即时通讯软件，比如腾讯qq，微信等。本project的应用将实现一对一的聊天功能，还有多对多的聊天功能，也就是群聊。

#### 2.1.4 需求分析

用户：可以创建单独聊天，创建群聊，接受其他用户发送的信息，群聊信息
应用：链接Miro服务器，链接其他的Minet应用
数据：私聊数据本地保存，群聊数据服务器保存

### 2.2 扩展功能

> 计划实现以下拓展功能：

* Support Image transfer.
* Audio, Video chat.
* supported information query, or chat?
(Chapter 2)
* Support offline mail system (Chapter 2)

### 2.2 初步设计

#### 2.2.1 功能设计

MINET有三个基本的功能：

1.	MINET能够与MIRO服务器进行连接，并能够在里面创建用户。当用户登录MINET后，能够从MIRO服务器中获取到此时在线的所有用户，并且能够在MINET的界面中显示出用户列表；

2.	聊天房间功能。MINET拥有一个公共聊天房间，所有在线用户可以在这个公共房间内进行聊天。所有用户发送的聊天信息都将发送给MIRO服务器，然后服务器再将所有人的聊天信息一起分发给每个用户，这样，每个用户都将能看到其他所有用户的聊天信息；

3.	P2P服务。MINET可以让用户在在线用户列表中选择单个其他用户，并且与他之间进行连接，之后，这个用户可以在聊天窗口中向这名用户发送信息；与此相对应，被连接用户也将会受到此用户的消息，并与之交流，互发信息。

#### 2.2.2 界面设计

> 初步的设计图如下：

![](https://github.com/laosiaudi/Minet/raw/master/images/screen1.png)

![](https://github.com/laosiaudi/Minet/raw/master/images/screen2.png)

![](https://github.com/laosiaudi/Minet/raw/master/images/screen3.png)

![](https://github.com/laosiaudi/Minet/raw/master/images/screen4.png)

![](https://github.com/laosiaudi/Minet/raw/master/images/screen5.png)

![](https://github.com/laosiaudi/Minet/raw/master/images/screen6.png)

#### 2.2.3 协议设计

> 针对基础功能的协议自定义

##### 基础功能

**服务器**

* 服务器给给单个客户端发送信息，接收消息。
* 服务器给多个客户端广播信息。

**客户端**

* 客户端于服务器之间的收发
* 客户端从服务器获取用户列表
* 客户端连接另外一个客户端，并与之进行通信（P2P）

**协议说明**

* 应用层协议主要规定两方面的内容：

	* 一是通信双方交换数据的格式与顺序
	* 二是通信双方应该采取的动作。

* 应用层协议建立在运输层的基础上，在这个项目中，我们采用了面向连接的TCP协议，以之作为底层支持，并在TCP的基础上，运用Socket套接字进行开发。这样一来，我们就可以轻松制定应用层的协议，并用代码来实现该协议。

* 项目中既有C/S架构，也有P2P架构，单纯地针对客户端和服务器之间设计一套协议是不够的，还要有针对客户端与客户端之间的协议。而且，目前只有基础功能需要考虑，如果以后加上附加的功能，协议势必会变得更加复杂。

* 设计协议还要考虑安全性、扩展性、容错性等等。

**协议设计的详细内容**

**C/S架构**

> 在此架构下，服务器的任务是维护在线用户的列表，维护聊天室的列表，给特定的单个或多个用户发送消息，接受单个用户的消息；客户端的任务是获取在线用户列表，获取聊天室列表，给服务器发送消息（更新在线状态、进入聊天室、发送聊天室消息等）。

**服务器**

* 服务器保持着在线状态，一旦接收到用户Login消息，则将该用户的ip地址,端口等等消息记录下来，将该用户加入在线用户列表。

* 服务器接收到用户的Logout消息后，就执行与上面相反的操作，将该用户移出在线用户列表，给用户发送allowLogout消息。

* 服务器接收到用户的enterChatRoom消息后，如果该用户指定的聊天室存在，就将该用户加入聊天室列表，给用户返回该聊天室的用户记录和聊天记录，并给该聊天室中的所有用户发送chatRoomPeopleUpdate消息，更新客户端里该聊天室的用户记录，让其它用户知道有新人来了；如果该用户指定的聊天室存在，就在聊天室列表里新建一个聊天室，将该用户加入该聊天室的用户记录，并给所有在线用户发送chatRoomUpdate消息，更新客户端里的聊天室记录。

* 服务器接收到用户的exitChatRoom消息后，将该用户从该聊天室的用户列表中移出，并告知该聊天室中其它用户，更新客户端中该聊天室的用户列表；如果该用户退出聊天室后，聊天室中没人了，就将该聊天室从聊天室列表中移出。

* 服务器接收到用户的queryChatRoom消息后，给用户返回聊天室列表。服务器接收到用户的queryAllUser消息后，给用户返回所有在线用户列表。

* 服务器接收到用户的chatRoomMsg消息后，会更新该聊天室的聊天记录。并会给该聊天室里其它用户发出chatRoomMsgUpdate消息，让其它人都知道“有人说话了”。

* 服务器接收到用户的queryUser消息后，给用户返回消息中指定的用户的信息，比如用户ip、用户签名等等。如果服务器并没有发出allowLogout消息，与客户端之间的连接就断开了，服务器不会主动重新连接，而是等待。等了10分钟后如果客户端还没有主动重连，服务器就会认为该用户已经“粗鲁”地退出，将其当作退出登录处理。

**客户端**

* 客户端并没有一直保持在线，只有客户端主动发起了与服务器的连接之后，客户端才会保持在线状态。在客户端发出Login消息后，如果因为未知原因客户端与服务器断开连接，客户端也要努力重新与服务器连接上，只有接受到服务器发来的allowLogout消息，客户端才算“温柔型”退出登录，不然会被服务器当做“粗鲁型”退出。

* 客户端接收到了服务器的chatRoomPeopleUpdate消息后，会更新自己保存的聊天室的用户记录（消息中包含了特定聊天室名称，是有人加入还是有人退出，是谁，等等）。

* 客户端接收到了服务器的chatRoomUpdate消息后，会更新自己的聊天室记录。

* 客户端接收到了服务器的chatRoomMsgUpdate消息后，会更新该聊天室的聊天记录。

* 客户端首先会发出Login来登录，得到服务器的确认后，就进一步发出queryAllUser和queryChatRoom，如果想要进入聊天室，就发送enterChatRoom，如果想要和某人进行P2P聊天，就发出queryUser来得到用户详情以便和TA进行连接。在聊天室里如果想发消息，就发出chatRoomMsg消息。


## 3 项目部署与更新

### 3.1 部署

```
git clone https://github.com/wujr5/computer-network-project.git
cd computer-network-project
```

### 3.2 更新

先跟远程仓库同步：

```
git pull origin master
```

然后添加更新并提交：

```
git add -A
git commit -m 'your comment'
git push origin master
```
