# =o=!@  coding:utf-8  @_@
"""

这是本工程的主程序入口

在树莓派本工程目录下通过:
    $ workon cv
    $ python Smalep.py
指令开始运行程序

在程序运行过程中不会有任何工作状态提示，但当发生错误时会显示错误信息

由于使用了多线程工作，要想结束本程序的运行，需要在树莓派新的终端内通过:
    $ ps aux
    $ kill (python Smalep.py's PID)
指令来结束

"""
import Smalep_cv
import Smalep_net
import Smalep_serial
import threading
import time
# import MySQLdb

# 全局变量声明=========================================================================

# 系统控制参数
system_mode = None
# 0:待机  1：进行初始化   2：运行  3：进行休眠
ex_is_person = False
is_person = False
is_host = None

reset_cmd = False   # 指令发生改变后重置指令

# 设备控制参数
cmd = [0, 128, 0, 0, 128, 0]
# 0：灯光开关    1：灯光亮度  2：加湿器开关 3：空调开关  4：自定空调风力    5：加热板开关
cmd_to_arduino = [0, 0, 0, 0]
# 0：灯光  1：加湿器   2：空调    3：加热板

# 传感器信息
sensor = [0, 0, 0]
# 0：温度  1：湿度    2：光照强度
urgent = 0

# 初始化串口
sr = Smalep_serial.Serial()

# 工程内部函数=========================================================================

# 进行初始化操作：
def system_initialize():
    global cmd_to_arduino
    # 发送指令
    sr.send(cmd_to_arduino)

# 进行休眠操作：
def system_sleep():
    global cmd
    global urgent

    # 数据及指令归位等待下次启动
    urgent = 0
    sleep_cmd = [0, 0, 0, 0]
    sr.send(sleep_cmd)

# 工作状态函数：
def system_run():
    global reset_cmd
    global urgent
    global cmd
    global cmd_to_arduino
    global sensor

    # 若非主人，发出警报，后由网络线程返回置为完成态；
    if (is_host == False):
        if (urgent != 2):  # 若已置为完成态则不做处理
            urgent = 1
    elif (is_host == True):  # 若是主人，则直接置为完成态
        urgent = 0

    # 接受Arduino发送来的数据更新sensor数据
    sr.receive(sensor)

    # 计算cmd_to_arduino并按需发送

    # 1.计算灯光亮度     cmd_to_arduino[0]
    if cmd[0] == 0:
        tmp = 0
    elif cmd[0] == 1:
        tmp = cmd[1]
    elif cmd[0] == 2:
        tmp = int(sensor[2] / 4)

    if abs(tmp - cmd_to_arduino[0]) > 1:
        cmd_to_arduino[0] = tmp
        reset_cmd = True

    # 2.计算加湿器开关   cmd_to_arduino[1]
    if cmd[2] == 0:
        tmp = 0
    elif cmd[2] == 1:
        tmp = 1
    elif cmd[2] == 2:
        if sensor[1] < 43:
            tmp = 1
        elif sensor[1] > 47:
            tmp = 0

    if tmp != cmd_to_arduino[1]:
        cmd_to_arduino[1] = tmp
        reset_cmd = True

    # 3.计算风扇强度     cmd_to_arduino[2]
    if cmd[3] == 0:
        tmp = 0
    elif cmd[3] == 1:
        tmp = cmd[4]
    elif cmd[3] == 2:
        tmp = cmd[4]

    if abs(tmp - cmd_to_arduino[2]) > 1:
        cmd_to_arduino[2] = tmp
        reset_cmd = True

    # 4.计算加热板开关   cmd_to_arduino[3]
    if cmd[5] == 0:
        tmp = 0
    elif cmd[5] == 1:
        tmp = 1
    elif cmd[5] == 2:
        if sensor[0] < 20:
            tmp = 1
        elif sensor[0] > 23:
            tmp = 0

    if tmp != cmd_to_arduino[3]:
        cmd_to_arduino[3] = tmp
        reset_cmd = True

    # 如cmd_to_arduino发生了变动，发送更新
    if reset_cmd == True:
        sr.send(cmd_to_arduino)
        reset_cmd = False  # 重置指令参数复位

# 多线程任务===========================================================================

# OpenCV线程：
def thread_cv():
    global is_person
    global is_host
    cv = Smalep_cv.CV()

    while True:
        cv.run()
        # 结果返回
        is_person = cv.is_person
        is_host = cv.is_host

# 网络线程：
def thread_net():
    global reset_cmd
    global urgent
    global system_mode

    global sensor
    global cmd
    global cmd_to_arduino
    net = Smalep_net.Netconn()

    while True:
        result = net.sync(sensor, urgent, cmd)
        # 与服务器成功通信后的收尾操作
        if result == True:
            # 将警报置为设置完毕状态
            if net.net_urgent == 1:
                urgent = 2
        else:
            print("NET ERROR!")
        # 延迟
        if system_mode == 2:
            time.sleep(0.2)
        else:
            time.sleep(2)

# 主线程：
def thread_main():
    global ex_is_person
    global is_person
    global system_mode

    while True:
        # print "PERSON:",is_person,"    ","HOST:",is_host,"     Urgent:",urgent
        tmp_is_person = is_person
        if ex_is_person == True:
            if is_person == True:  # 工作状态
                system_mode = 2
                system_run()
                time.sleep(0.05)
            else:  # 进入休眠
                system_mode = 3
                system_sleep()
        else:
            if is_person == True:  # 初始化
                system_mode = 1
                system_initialize()
            else:  # 休眠状态
                system_mode = 0
                time.sleep(1)
        ex_is_person = tmp_is_person

# 线程启动入口=========================================================================

if __name__ == '__main__':
    threading.Thread(target=thread_main).start()
    threading.Thread(target=thread_cv).start()
    threading.Thread(target=thread_net).start()
