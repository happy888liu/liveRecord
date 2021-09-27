#### 目前支持Bilibili 直播录制  

## :smile:使用方法
+ 程序调用时传入参数即可(顺序可变)  
    `java -Dfile.encoding=utf-8 -jar BiliLiveRecorder.jar "debug=false&check=true&liver=bili"`  
+ 各参数意义  

| Key  | 必选 | 释义 | 
| ------------- | ------------- | ------------- |
| debug  | 否 | debug模式,输出更多信息。默认false |  
| check  | 否 | 下载完后是否校准时间戳，默认true |  
| delete  | 否 | 校准后是否删除源文件，默认true |  
| zip  | 否 | 是否压缩成zip文件，默认false |  
| liver  | 是 | 将要录制的直播源。 详见下表 | 
| id  | 否 | 直播房间id，如未传入，后续将提示输入。 | 
| qn  | 否 | 直播视频清晰度。不同网站值意义不同。-1代表最高清晰度。 |   
| qnPri  | 否 | 直播视频清晰度优先级。分隔符`>` 例: `蓝光4M>蓝光>超清` 蓝光4M优先级最高 | 
| retry  | 否 | 异常导致录制停止后的重试次数。默认5次 |   
| fileSize  | 否 | 分段录制的参考文件大小，0为不按文件大小分段，单位`MB`。默认0 |   
| filePeriod  | 否 | 分段录制的参考时长，0为不按时长分段，单位`min`。默认0 |    
| splitScriptTags  | 否 | 校准文件时是否分割ScriptTag。默认false | 
| splitAVHeaderTags  | 否 | 校准文件时是否分割a/v header Tag时。默认与splitScriptTags一致 |  
| maxAudioHeaderSize  | 否 | 当Audio tag的data size小于该值时，认为是audio header。默认`10` | 
| maxVideoHeaderSize  | 否 | 当Video tag的data size小于该值时，认为是video header。默认`60`  | 
| fileName  | 否 | 文件命名规则，默认`{name}-{shortId} 的{liver}直播{startTime}-{seq}` | 
| timeFormat  | 否 | 文件命名中{startTime}和{endTime}的格式，默认`yyyy-MM-dd HH.mm` | 
| saveFolder  | 否 | 源文件保存路径 | 
| saveFolderAfterCheck  | 否 | FLV文件校准后的保存路径，check为true时有效。默认为空，此时与`saveFolder`等同 | 
| stopAfterOffline  | 否 | 当目标下播后，是否停止程序。为false时，需要和下面三个参数配合。默认true | 
| retryIfLiveOff  | 否 | 当目标不在直播时，是否继续重试。默认false | 
| maxRetryIfLiveOff  | 否 | 当目标不在直播时，继续重试的次数。默认0，此时会一直进行尝试，直到主播上线 | 
| retryAfterMinutes  | 否 | 当目标不在直播时，每次获取直播间信息的时间间隔，单位分钟。默认`5.0` | 
| failRetryAfterMinutes  | 否 | 当连接出现异常时，下次尝试录制的时间间隔，单位分钟。默认`1.0` |

+ 各直播源解析情况  

| liver  | 最后测试时间 | 备注 | 
| ------------- | ------------- | ------------- |
| bili      | 2021/08/16 | `flv`清晰度可多选，可不需要cookie | 

<details>
<summary>加载cookies(适用于高清晰度录制)</summary>

+ 将cookie保存到同级目录的`{liver}-cookie.txt`即可，e.g. `bilibili-cookie.txt`     
+ cookie内容为以下格式：  
```
dy_did=xxx; acf_did=xxx; acf_auth=xxx; ...
```
+ 如何获取cookie(以斗鱼举例)：  
    + 打开浏览器，进入b站直播  
    + 登录账号  
    + 进入一个热度较高的直播间，选择清晰度 
    + 按F12键
    + 任意选择一条记录，复制右边的cookie
</details>     


+ 请勿传入非法字符，如`&`  
+ 建议保留`{startTime}`和`{seq}`，以确保文件名唯一，否则很可能出现未知错误  
+ 校准时间戳这一动作将会产生若干个文件，这些文件将在原来的基础上增加-checked[0-9]+后缀  


可提供直播质量:
    0 : 超清
    2 : 高清
    1 : 流畅
传入参数： qn=2&qnPri=蓝光4M>蓝光
此时取 2 : 高清
-------------------------------
可提供直播质量:
    0 : 蓝光
    3 : 超清
    2 : 高清
    1 : 流畅
传入参数： qn=2&qnPri=蓝光4M>蓝光
此时取 0 : 蓝光
```	

+ 当未传入qn，且(qnPri为空或不匹配)，程序将提示输入qn值
```
可提供直播质量:
    0 : 超清
    2 : 高清
    1 : 流畅
传入参数： 不包含qn、qnPri   
or传入参数： qnPri=蓝光4M>蓝光
此时程序将提示输入qn值
```	

+ 当指定qn生效(指qnPri为空或不匹配)，且获取的清晰度列表不存在该清晰度值时，程序将退出
```
可提供直播质量:
    0 : 超清
    2 : 高清
    1 : 流畅
传入参数： qn=4
此时程序将退出
传入参数： qn=4&qnPri=蓝光4M>蓝光
此时程序将退出
