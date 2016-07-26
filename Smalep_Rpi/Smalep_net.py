# coding:utf8
"""

这是本工程的网络类，通过在主程序内调用封装好的网络通讯方法实现功能。

类函数：
    Netconn():          类构造方法
        传入参数：   无

    sync():             网络同步函数
        传入参数:   sensor[]    urgent  cmd[]
        返回值:     True/False

    uploadimg():        将工程目录下 face.png 图片上传至服务器
        传入参数:   无
        返回值:     True/False

    __analyse():        解析指令控制字符串至指令数组    【类内隐函数不可外部调用】
        传入参数:   rtstr   cmd[]
        返回值:     无

"""
import httplib
from poster.encode import multipart_encode
from poster.streaminghttp import register_openers
import urllib2

class Netconn:
    __url = ""
    net_urgent=0

    def __init__(self):
        self.__url = 'www.tyutiot.com'

    def sync(self, sensor, urgent, cmd):
        self.net_urgent=urgent
        try:
            requestpath = '/raspberrypi.php?token=sp199539&Temp=' + str(sensor[0]) + '&Humidity=' + str(sensor[1]) + '&Urgent=' + str(urgent)
            conn = httplib.HTTPConnection(self.__url, 80, timeout=3)
            conn.request('GET', requestpath)
            rtcode = conn.getresponse()
            if rtcode.status == 200:
                rtstr = rtcode.read()
                conn.close()
                # print "从服务器收到指令字符串：" + rtstr
                self.__analyse(rtstr, cmd)
                return True
            else:
                conn.close()
                return False
        except Exception, e:
            print(e)

    def __analyse(self, rtstr, cmd):  # 分析返回指令至cmd
        if rtstr != "":
            reverse = 0
            count = 0
            for i in range(0, len(rtstr)):
                if rtstr[i] == '{':
                    reverse = 0
                    count = 0
                elif rtstr != '}' and rtstr[i] != '}' and rtstr[i] != '+':
                    reverse = reverse * 10 + int(rtstr[i])
                elif rtstr[i] == '+' or rtstr[i] == '}':
                    if cmd[count] != reverse:
                        cmd[count] = reverse
                    reverse = 0
                    count = count + 1

    def uploadimg(self):  # 在Smalep_cv中在检测到非主任时调用上传图片至服务器
        try:
            register_openers()
            datagen, headers = multipart_encode({'file': open("face.png", "rb")})  # 按照二进制文件方式打开图片
            request = urllib2.Request("http://www.tyutiot.com/upload_img.php", datagen, headers)
            if urllib2.urlopen(request).read() == 1:
                return True
            else:
                return False
        except Exception, e:
            print(e)
