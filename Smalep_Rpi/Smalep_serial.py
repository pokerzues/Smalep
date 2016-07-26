# coding:utf8
"""

这是本工程的串口类，通过在主程序内调用封装好的串口通讯方法实现功能。

类函数：
    Serial():           类构造方法
        传入参数：   无

    send():             向Arduino发送指令函数
        传入参数:   cmd_to_arduino
        返回值:     无

    receive():          从Arduino读取传感器参数字符串
        传入参数:   sensor[]
        返回值:     无

    __analyse():        解析传感器参数字符串至传感器数组    【类内隐函数不可外部调用】
        传入参数:   sensorstr   sensor[]
        返回值:     无

"""
import serial

class Serial:
    __sr = None

    def __init__(self):
        self.__sr = serial.Serial('COM3', 9600)

    def send(self, cmd_to_arduino):
        cmdstr = "{" + str(cmd_to_arduino[0]) + "+" + str(cmd_to_arduino[1]) + "+" + str(cmd_to_arduino[2]) + "+" + str(cmd_to_arduino[3]) + "}"
        print "更新Arduino：" + cmdstr
        self.__sr.write(cmdstr)

    def receive(self, sensor):
        if self.__sr.readable():
            sensorstr = self.__sr.readline()
            # print "接受到Arduino传感器字符串：" + sensorstr
            self.__analyse(sensorstr, sensor)

    def __analyse(self, sensorstr, sensor):
        reverse = 0
        count = 0
        try:
            if sensorstr != "":
                for i in range(0, len(sensorstr)-2):    # 结尾有两个\n，不予考虑
                    if sensorstr[i] == '{':
                        reverse = 0
                        count = 0
                    elif sensorstr != '}' and sensorstr[i] != '}' and sensorstr[i] != '+':
                        reverse = reverse * 10 + int(sensorstr[i])
                    elif sensorstr[i] == '+' or sensorstr[i] == '}':
                        sensor[count] = reverse
                        reverse = 0
                        count = count + 1
        except Exception, e:
            print e
