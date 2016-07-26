# coding:utf8
"""

这是一个利用 Opencv 开源计算机视觉图形库来进行工作的模块，用于在 Smalep 运行时实时监测设备环境变量。是上一版 Smalep 中
    红外感应模块的绝佳替代，增强了灵敏度、抗干扰性和测量范围的同时得益于树莓派较强的计算能力可以实现比上一版的更加智能
        化（实际上现在的项目应该改名叫做“基于树莓派的学习平台”了 =_=||| ）

为了更好地实现人脸识别功能，我们使用了一款非常强有力的在线检测产品：Face++
    向天才们致敬！感谢你们的技术支持！


        ##############         ######          ##############   ##############         ####            ####
        ##############        ########         ##############   ##############         ####            ####
        ####                 ####  ####        ####             ####              ##############  ##############
        #############       ####    ####       ####             ##############    ##############  ##############
        #############      ##############      ####             ##############         ####            ####
        ####              ################     ####             ####                   ####            ####
        ####             ####          ####    ##############   ##############
        ####            ####            ####   ##############   ##############


本类提供基本构造方法“CV()”，以及一个方法“run()”可供调用。这个模块会在你直接调用“run()”方法后自动运行，并可以返回
    两个结果值：is_person 以及 is_host，用于在主线程调用

本类提供两个变量接口，可以在线程中直接通过“对象.属性”方法获得。如下：
    is_person         is_host
        注意：这也是唯一可以进行外部获取的两个属性值，不建议使用本类中的其他属性！！！（实际上你也用不了 @_@~ ）

"""
import cv2
import cv2.cv as cv
import imutils
import time
from Smalep_facepp import Myfacepp
from Smalep_net import Netconn

class CV:
    __camera = None  # CV模块将要调用的摄像头
    __cvmode = 0  # 0:休眠   1:启动   2:工作
    __frame1 = None  # 用于对比的第一帧，同时也是用于人脸识别的帧
    __frame2 = None  # 用于对比的第二帧

    is_person = False  # 是否有人，用于外部直接调用
    __temp_is_person = None  # 临时是否有人变量，None为未确定状态
    __timer_is_person = 0  # 计时器
    __configuretime = 3
    __configure_is_person = __configuretime  # 确定有人的次数，与计时器配合使用
    __sleep_delay_time = 25  # 休眠时间设置

    is_host = None  # 是否是host，用于外部直接调用
    __temp_is_host = None
    __retrytime = 7
    __retry_is_host = __retrytime
    __timer_is_host = 0

    __cascade = cv2.CascadeClassifier("./haarcascade_frontalface_alt.xml")
    __facepp = None
    __uploadimg = None
    __faceimg_path = 'face.png'

    def __init__(self):
        self.__camera = cv2.VideoCapture(1)
        self.__facepp = Myfacepp()
        self.__uploadimg = Netconn()
        time.sleep(0.25)

    # 主运行方法
    def run(self):
        if self.__cvmode == 0:  # 当CV模块休眠时执行此模块
            # print("------now sleeping")
            self.__cammd()
            # print self.__temp_is_person
            # print(self.__configure_is_person)
            if self.__temp_is_person == True:
                self.__configure_is_person = self.__configure_is_person - 1
            else:
                self.__configure_is_person = self.__configuretime
            if self.__configure_is_person == 0:
                self.is_person = True
                self.__configure_is_person = self.__configuretime
                self.__cvmode = 1
                # print(self.__configuretime)

        elif self.__cvmode == 1:  # 当CV模块检测到运动启动时执行此模块
            # print("------now starting")
            self.__cammd()
            if (self.__timer_is_host % 20) == 0 and self.__retry_is_host != 0:
                self.__timer_is_host = self.__timer_is_host + 1
                self.__retry_is_host = self.__retry_is_host - 1
                self.__idcfg()
                if self.__temp_is_host == True:
                    self.is_host = True
                    self.__timer_is_host = 0
                    self.__retry_is_host = self.__retrytime
                    self.__cvmode = 2
            else:
                self.__timer_is_host = self.__timer_is_host + 1
                if self.__retry_is_host == 0:
                    self.is_host = False
                    self.__timer_is_host = 0
                    self.__retry_is_host = self.__retrytime
                    self.__cvmode = 2
                    # print(self.__timer_is_host)

        elif self.__cvmode == 2:  # 当CV模块工作时执行此模块
            # print("------now running")
            self.__cammd()
            if self.__temp_is_person == True:
                self.is_person = True
                self.__timer_is_person = 0
            else:
                self.__timer_is_person = self.__timer_is_person + 1
                if self.__timer_is_person == self.__sleep_delay_time:
                    self.is_person = False
                    self.is_host = None
                    self.__timer_is_person = 0
                    self.__cvmode = 0
                    # print self.__temp_is_person,self.__timer_is_person

        cv2.waitKey(1)  # opencv 等待键入（opencv运行结构必要语句）

    # 人脸识别模块
    def __idcfg(self):
        is_face = len(self.__cascade.detectMultiScale(self.__frame1, scaleFactor=1.3, minNeighbors=4, minSize=(30, 30),
                                                      flags=cv.CV_HAAR_SCALE_IMAGE))
        # 判断__frame1 帧中是否有人脸出现
        if is_face != 0:
            cv2.imwrite(self.__faceimg_path, self.__frame1)  # 将有人脸出现的帧进行保存
            result = self.__facepp.do_recognize(self.__faceimg_path)  # 若有人脸出现，则调用face++模块进行识别，判定是否为host
            if result == True:
                self.__temp_is_host = True
            else:
                self.__temp_is_host = False
                if self.__uploadimg.uploadimg() == False:
                    print "upload image failed!"
                else:
                    print "uploaded image!"
        else:
            self.__temp_is_host = False

        # 运动检测模块

    def __cammd(self):
        if self.__cvmode == 2:  # 根据当前的工作状态智能调节相机检测阈值及灵敏度
            blurratio = 21  # 高斯模糊系数
            threshratio = 2  # 二值化系数（色差系数）
        else:
            blurratio = 21
            threshratio = 2

        (grabbed, self.__frame1) = self.__camera.read()  # 获取用于对比的第一帧
        time.sleep(0.1)  # 两帧间隔时间
        (grabbed, self.__frame2) = self.__camera.read()  # 获取用于对比的第二帧
        time.sleep(0.1)  # 与下一次循环的间隔时间
        self.__frame1 = imutils.resize(self.__frame1, width=400)  # 重新定义帧大小
        self.__frame2 = imutils.resize(self.__frame2, width=400)
        gray1 = cv2.cvtColor(self.__frame1, cv2.COLOR_BGR2GRAY)  # 转换为灰阶图
        gray1 = cv2.GaussianBlur(gray1, (blurratio, blurratio), 0)  # 高斯模糊化灰阶图
        gray2 = cv2.cvtColor(self.__frame2, cv2.COLOR_BGR2GRAY)
        gray2 = cv2.GaussianBlur(gray2, (blurratio, blurratio), 0)
        frameDelta = cv2.absdiff(gray1, gray2)  # 比较两帧灰阶图像的差异
        thresh = cv2.threshold(frameDelta, threshratio, 255, cv2.THRESH_BINARY)[1]
        # 如果两灰阶帧色差大于threshratio，则直接将其标记为白色，存入thresh图像
        thresh = cv2.dilate(thresh, None, iterations=2)
        (cnts, _) = cv2.findContours(thresh.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        sum = 0
        for c in cnts:  # 将图中所有差异部分的面积累加
            area = cv2.contourArea(c)
            if area < 4:  # 过滤面积较小的色差块，以求稳定
                continue
            else:
                sum = sum + area

        if sum > 50:  # 若差异部分面积累加大于50像素，则判定当前状态确实有人，并赋值给__temp_is_person
            self.__temp_is_person = True
        else:
            self.__temp_is_person = False
        # cv2.imshow("1", thresh)
        # cv2.imshow("Face-SignUp", self.__frame2)
        # cv2.imshow("3", gray2)
